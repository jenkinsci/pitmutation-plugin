package org.jenkinsci.plugins.pitmutation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

/**
 * @author edward
 */
class MutationReportTest {

    private static final String MUTATIONS_OLD = "<mutations>"
                                                + "<mutation detected='true' status='NO_COVERAGE'>\n"
                                                + "<sourceFile>SafeMultipartFile.java</sourceFile>\n"
                                                + "<mutatedClass>com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile</mutatedClass>\n"
                                                + "<mutatedMethod>getSize</mutatedMethod>\n"
                                                + "<lineNumber>54</lineNumber>\n"
                                                + "<mutator>org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator</mutator>\n"
                                                + "<index>5</index>\n"
                                                + "<killingTest/>\n"
                                                + "</mutation>"
                                                + "<mutation detected='false' status='KILLED'>"
                                                + "<sourceFile>SafeMultipartFile.java</sourceFile>"
                                                + "<mutatedClass>com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile</mutatedClass>"
                                                + "<mutatedMethod>getSize</mutatedMethod>"
                                                + "<lineNumber>57</lineNumber>"
                                                + "<mutator>org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator</mutator>"
                                                + "<index>6</index>"
                                                + "<block>3</block>"
                                                + "<killingTest/>"
                                                + "</mutation>"
                                                + "</mutations>";

    private static final String MUTATIONS = "<mutations>"
                                            + "<mutation detected='true' status='NO_COVERAGE'>\n"
                                            + "<sourceFile>SafeMultipartFile.java</sourceFile>\n"
                                            + "<mutatedClass>com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile</mutatedClass>\n"
                                            + "<mutatedMethod>getSize</mutatedMethod>\n"
                                            + "<lineNumber>54</lineNumber>\n"
                                            + "<mutator>org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator</mutator>\n"
                                            + "<indexes><index>5</index></indexes>\n"
                                            + "<killingTest/>\n"
                                            + "</mutation>"
                                            + "<mutation detected='false' status='KILLED'>"
                                            + "<sourceFile>SafeMultipartFile.java</sourceFile>"
                                            + "<mutatedClass>com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile</mutatedClass>"
                                            + "<mutatedMethod>getSize</mutatedMethod>"
                                            + "<lineNumber>57</lineNumber>"
                                            + "<mutator>org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator</mutator>"
                                            + "<indexes><index>6</index></indexes>"
                                            + "<blocks><block>3</block></blocks>"
                                            + "<killingTest/>"
                                            + "</mutation>"
                                            + "</mutations>";

    private InputStream mutationsXml;

    @BeforeEach
    public void setUp() {
        mutationsXml = getClass().getResourceAsStream("testmutations-00.xml");
    }

    @Test
    void packageNameFinder() {
        assertThat(MutationReport.packageNameFromClass("xxx.yyy.zzz.Foo"), is("xxx.yyy.zzz"));
        assertThat(MutationReport.packageNameFromClass("Foo"), is(""));
    }

    @Test
    void countsKills() throws IOException, SAXException {
        MutationReport report = new MutationReport(mutationsXml);
        assertThat(report.getMutationStats().getKillCount(), is(5));
        assertThat(report.getMutationStats().getTotalMutations(), is(16));
    }

    @Test
    void sortsMutationsByClassName() throws IOException, SAXException {
        MutationReport report = new MutationReport(mutationsXml);
        Collection<Mutation> mutations =
            report.getMutationsForClassName("org.jenkinsci.plugins.pitmutation.MutationReport");
        assertThat(mutations.size(), is(5));
    }

    @Test
    void indexesMutationsByPackage() throws IOException, SAXException {
        MutationReport report = new MutationReport(mutationsXml);
        assertThat(report.getMutationsForPackage("org.jenkinsci.plugins.pitmutation"), hasSize(16));
        assertThat(report.getMutationsForPackage(""), hasSize(0));
    }

    @Test
    void canDigestAMutation() throws IOException, SAXException {
        MutationReport report = new MutationReport(new ByteArrayInputStream(MUTATIONS.getBytes("UTF-8")));

        assertThat(report.getMutationStats().getTotalMutations(), is(2));

        Iterator<Mutation> mutations = report
            .getMutationsForClassName("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile")
            .iterator();

        Mutation m1 = mutations.next();
        Mutation m2 = mutations.next();

        if (m1.getStatus().equals("KILLED")) {
            verifyKilled(m1);
            verifyNoCoverage(m2);
        } else {
            verifyKilled(m2);
            verifyNoCoverage(m1);
        }
    }

    @Test
    void canDigestAMutationOlderMutations() throws IOException, SAXException {
        MutationReport report = new MutationReport(new ByteArrayInputStream(MUTATIONS_OLD.getBytes("UTF-8")));

        assertThat(report.getMutationStats().getTotalMutations(), is(2));

        Iterator<Mutation> mutations = report
            .getMutationsForClassName("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile")
            .iterator();

        Mutation m1 = mutations.next();
        Mutation m2 = mutations.next();

        if (m1.getStatus().equals("KILLED")) {
            verifyKilled(m1);
            verifyNoCoverage(m2);
        } else {
            verifyKilled(m2);
            verifyNoCoverage(m1);
        }
    }


    private void verifyNoCoverage(Mutation m) {
        assertThat(m.getLineNumber(), is(54));
        assertThat(m.isDetected(), is(true));
        assertThat(m.getStatus(), is("NO_COVERAGE"));
        assertThat(m.getSourceFile(), is("SafeMultipartFile.java"));
        assertThat(m.getMutatedClass(), is("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile"));
        assertThat(m.getMutator(), is("org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator"));
    }

    private void verifyKilled(Mutation m) {
        assertThat(m.getLineNumber(), is(57));
        assertThat(m.isDetected(), is(false));
        assertThat(m.getStatus(), is("KILLED"));
        assertThat(m.getSourceFile(), is("SafeMultipartFile.java"));
        assertThat(m.getMutatedClass(), is("com.mediagraft.podsplice.controllers.massupload.SafeMultipartFile"));
        assertThat(m.getMutator(), is("org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator"));
    }
}
