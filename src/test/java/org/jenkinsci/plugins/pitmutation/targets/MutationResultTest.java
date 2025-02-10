package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ed Kimber
 */
public abstract class MutationResultTest {

    private MutationResult<?> projectResult;
    private MutationResult<?> moduleResult;

    @BeforeEach
    public void setUp() throws IOException, SAXException {
        MutationReport reportOld = MutationReport.create(MutationReport.class.getResourceAsStream("testmutations-00.xml"));
        MutationReport reportNew = MutationReport.create(MutationReport.class.getResourceAsStream("testmutations-01.xml"));
        Map<String, MutationReport> reportsNew = new HashMap<>();
        Map<String, MutationReport> reportsOld = new HashMap<>();
        reportsNew.put("test_report", reportNew);
        reportsOld.put("test_report", reportOld);

        PitBuildAction buildAction = mock(PitBuildAction.class);
        when(buildAction.getReports()).thenReturn(reportsNew);

        PitBuildAction previousBuildAction = mock(PitBuildAction.class);
        when(previousBuildAction.getReports()).thenReturn(reportsOld);
        when(previousBuildAction.getReport()).thenReturn(new ProjectMutations(previousBuildAction));

        when(buildAction.getPreviousAction()).thenReturn(previousBuildAction);

        projectResult = new ProjectMutations(buildAction);
        moduleResult = projectResult.getChildMap().get("test_report");
        assertThat(moduleResult, not(nullValue()));
        assertThat(projectResult.getPreviousResult(), not(nullValue()));
    }

    @Test
    public void mutationResultStatsDelta() {
        MutationStats delta = projectResult.getStatsDelta();
        assertThat(delta.getTotalMutations(), is(3));
        assertThat(delta.getKillCount(), is(-1));
    }

    private MutationResult packageResult() {
        return projectResult.getChildResult("test_report").getChildResult("org.jenkinsci.plugins.pitmutation");
    }

    @Test
    public void packageResultsStatsDelta() {
        MutationStats delta = packageResult().getStatsDelta();
        assertThat(delta.getTotalMutations(), is(3));
        assertThat(delta.getKillCount(), is(-1));
    }

    private MutationResult classResult(String className) {
        return packageResult().getChildResult(className);
    }

    @Test
    public void classResultsStats() {
        MutationStats stats = classResult("org.jenkinsci.plugins.pitmutation.Mutation").getMutationStats();
        assertThat(stats.getTotalMutations(), is(3));
        assertThat(stats.getKillCount(), is(1));
    }

    @Test
    public void classResultsStatsDelta() {
        MutationStats delta = classResult("org.jenkinsci.plugins.pitmutation.Mutation").getStatsDelta();
        assertThat(delta.getTotalMutations(), is(-1));
        assertThat(delta.getKillCount(), is(-2));
    }

    @Test
    public void classResultsForNewClass() {
        MutationStats stats = classResult("org.jenkinsci.plugins.pitmutation.NewMutatedClass").getMutationStats();
        assertThat(stats.getTotalMutations(), is(1));
        assertThat(stats.getKillCount(), is(0));
    }

    @Test
    public void classResultsForNewClassDelta() {
        MutationStats stats = classResult("org.jenkinsci.plugins.pitmutation.NewMutatedClass").getStatsDelta();
        assertThat(stats.getTotalMutations(), is(1));
        assertThat(stats.getKillCount(), is(0));
    }

    @Test
    public void classResultsOrdered() {
        Iterator<? extends MutationResult> classes = moduleResult.getChildren().iterator();
        int undetected = classes.next().getMutationStats().getUndetected();

        while (classes.hasNext()) {
            MutationResult result = classes.next();
            assertThat(result.getMutationStats().getUndetected(), lessThan(undetected));
            undetected = result.getMutationStats().getUndetected();
        }
    }

    @Test
    public void urlTransformPackageName() {
        assertThat(moduleResult.getChildMap().get("org.jenkinsci.plugins.pitmutation").getUrl(),
            is("org_jenkinsci_plugins_pitmutation"));
    }

    @Test
    public void urlTransformClassName() {
        assertThat(moduleResult.getChildMap().get("org.jenkinsci.plugins.pitmutation")
                .getChildMap().get("org.jenkinsci.plugins.pitmutation.PitParser").getUrl(),
            is("org_jenkinsci_plugins_pitmutation_PitParser"));
    }

    @Test
    public void findsMutationsOnPitParserClass() {
        MutationResult<?> pitPackage = moduleResult.getChildMap().get("org.jenkinsci.plugins.pitmutation");
        assertThat(pitPackage.getChildren(), hasSize(5));
        MutationResult<?> pitParser = pitPackage.getChildMap().get("org.jenkinsci.plugins.pitmutation.PitParser");
        assertThat(pitParser.getChildren(), hasSize(3));
    }

    @Test
    public void collectsMutationStats() {
        MutationStats stats = projectResult.getMutationStats();
        assertThat(stats.getTotalMutations(), is(19));
        assertThat(stats.getUndetected(), is(15));
    }

    @Test
    public void correctSourceLevels() {
        MutationResult<?> pitPackage = moduleResult.getChildMap().get("org.jenkinsci.plugins.pitmutation");
        MutationResult<?> pitParser = pitPackage.getChildMap().get("org.jenkinsci.plugins.pitmutation.PitParser");
        MutationResult<?> lineResult = pitParser.getChildMap().values().iterator().next();

        assertThat(projectResult.isSourceLevel(), is(false));
        assertThat(moduleResult.isSourceLevel(), is(false));
        assertThat(pitPackage.isSourceLevel(), is(false));
        assertThat(pitParser.isSourceLevel(), is(true));
        assertThat(lineResult.isSourceLevel(), is(false));
    }

    @Test
    public void testXmlTransform() {
        assertThat(MutationResult.xmlTransform("replace&and<and>"), is("replace&amp;and&lt;and&gt;"));
    }

    @Test
    public void testUrlTransform() {
        assertThat(MutationResult.urlTransform("^*!replace::non+'alphas@}129"), is("___replace__non__alphas__129"));
    }
}
