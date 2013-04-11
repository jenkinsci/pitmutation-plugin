package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ed Kimber
 */
public class MutationResultTest {
  @Before
  public void setUp() throws IOException, SAXException {
    InputStream[] mutationsXml_ = new InputStream[2];
    mutationsXml_[0] = MutationReport.class.getResourceAsStream("mutations-00.xml");
    mutationsXml_[1] = MutationReport.class.getResourceAsStream("mutations-01.xml");
    MutationReport reportOld = MutationReport.create(mutationsXml_[0]);
    MutationReport reportNew = MutationReport.create(mutationsXml_[1]);
    Map<String, MutationReport> reportsNew = new HashMap<String, MutationReport>();
    Map<String, MutationReport> reportsOld = new HashMap<String, MutationReport>();
    reportsNew.put("test_report", reportNew);
    reportsOld.put("test_report", reportOld);


    PitBuildAction buildAction_ = mock(PitBuildAction.class);
    when(buildAction_.getReports()).thenReturn(reportsNew);

    PitBuildAction previousBuildAction_ = mock(PitBuildAction.class);
    when(previousBuildAction_.getReports()).thenReturn(reportsOld);
    when(previousBuildAction_.getReport()).thenReturn(new ProjectMutations(previousBuildAction_));

    when(buildAction_.getPreviousAction()).thenReturn(previousBuildAction_);


    projectResult_ = new ProjectMutations(buildAction_);
    moduleResult_ = projectResult_.getChildMap().get("test_report");
    assertThat(moduleResult_, not(nullValue()));
    assertThat(projectResult_.getPreviousResult(), not(nullValue()));
  }

  @Test
  public void mutationResultStatsDelta() {
    MutationStats delta = projectResult_.getStatsDelta();
    assertThat(delta.getTotalMutations(), is(3));
    assertThat(delta.getKillCount(), is(-1));
  }

  @Test
  public void classResultsOrdered() {
    Iterator<? extends MutationResult> classes = moduleResult_.getChildren().iterator();
    int undetected = classes.next().getMutationStats().getUndetected();

    while(classes.hasNext()) {
      MutationResult result = classes.next();
      assertThat(result.getMutationStats().getUndetected(), lessThan(undetected));
      undetected = result.getMutationStats().getUndetected();
    }
  }

  @Test
  public void urlTransformPackageName() {
    assertThat(moduleResult_.getChildMap().get("org.jenkinsci.plugins.pitmutation").getUrl(),
            is("org_jenkinsci_plugins_pitmutation"));
  }

  @Test
  public void urlTransformClassName() {
    assertThat(moduleResult_.getChildMap().get("org.jenkinsci.plugins.pitmutation")
            .getChildMap().get("org.jenkinsci.plugins.pitmutation.PitParser").getUrl(),
            is("org_jenkinsci_plugins_pitmutation_PitParser"));
  }
//  @Test
//  public void findsClassesWithNewSurvivors() {
//    Collection<MutatedClass> survivors = result_.getClassesWithNewSurvivors();
//    assertThat(survivors, hasSize(2));
//
//    Iterator<MutatedClass> it = survivors.iterator();
//    MutatedClass a = it.next();
//    MutatedClass b = it.next();
//    if (a.getFileName().equals("Mutation.java.html")) {
//      checkMutationJava(a);
//      checkPitBuildActionJava(b);
//    }
//    else {
//      checkMutationJava(b);
//      checkPitBuildActionJava(a);
//    }
//  }
//
//  private void checkMutationJava(MutatedClass mutant) {
//    assertThat(mutant.getFileName(), is("Mutation.java.html"));
//    assertThat(mutant.getMutatedLines(), hasSize(1));
//  }
//
//  private void checkPitBuildActionJava(MutatedClass mutant) {
//    assertThat(mutant.getFileName(), is("PitBuildAction.java.html"));
//    assertThat(mutant.getMutatedLines(), hasSize(2));
//  }

    @Test
    public void findsMutationsOnPitParserClass() {
      MutationResult pitPackage = moduleResult_.getChildMap().get("org.jenkinsci.plugins.pitmutation");
      assertThat(pitPackage.getChildren(), hasSize(5));
      MutationResult pitParser = pitPackage.getChildMap().get("org.jenkinsci.plugins.pitmutation.PitParser");
      assertThat(pitParser.getChildren(), hasSize(3));
    }

    @Test
    public void collectsMutationStats() {
      MutationStats stats = projectResult_.getMutationStats();
      assertThat(stats.getTotalMutations(), is(19));
      assertThat(stats.getUndetected(), is(15));
    }

  @Test
  public void correctSourceLevels() {
    MutationResult pitPackage = moduleResult_.getChildMap().get("org.jenkinsci.plugins.pitmutation");
    MutationResult pitParser = pitPackage.getChildMap().get("org.jenkinsci.plugins.pitmutation.PitParser");
    MutationResult lineResult = pitParser.getChildMap().values().iterator().next();

    assertThat(projectResult_.isSourceLevel(), is(false));
    assertThat(moduleResult_.isSourceLevel(), is(false));
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
//
//  @Test
//  public void collectsStatsOnNewTargets() {
//    Collection<MutationStats> newTargetStats = result_.getStatsForNewTargets();
//    assertThat(newTargetStats, hasSize(1));
//
//    MutationStats stats = newTargetStats.iterator().next();
//    assertThat(stats.getTotalMutations(), is(1));
//    assertThat(stats.getUndetected(), is(1));
//  }

  private MutationResult projectResult_;
  private MutationResult moduleResult_;
}
