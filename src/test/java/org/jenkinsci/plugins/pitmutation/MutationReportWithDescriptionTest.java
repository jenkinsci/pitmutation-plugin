package org.jenkinsci.plugins.pitmutation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * @author edward
 */
public class MutationReportWithDescriptionTest {

    private InputStream mutationsXml;

    @BeforeEach
    public void setUp() {
        mutationsXml = getClass().getResourceAsStream("testmutations-02.xml");
    }

    @Test
    public void packageNameFinder() {
        assertThat(MutationReport.packageNameFromClass("xxx.yyy.zzz.Foo"), is("xxx.yyy.zzz"));
        assertThat(MutationReport.packageNameFromClass("Foo"), is(""));
    }

    @Test
    public void countsKills() throws IOException, SAXException {
        MutationReport report = MutationReport.create(mutationsXml);
        assertThat(report.getMutationStats().getKillCount(), is(3));
        assertThat(report.getMutationStats().getTotalMutations(), is(4));
    }

    @Test
    public void sortsMutationsByClassName() throws IOException, SAXException {
        MutationReport report = MutationReport.create(mutationsXml);
        Collection<Mutation> mutations = report.getMutationsForClassName("es.rodri.controllers.CompositorController");
        assertThat(mutations.size(), is(4));
    }

    @Test
    public void indexesMutationsByPackage() throws IOException, SAXException {
        MutationReport report = MutationReport.create(mutationsXml);
        assertThat(report.getMutationsForPackage("es.rodri.controllers"), hasSize(4));
        assertThat(report.getMutationsForPackage(""), notNullValue());
        assertThat(report.getMutationsForPackage(""), hasSize(0));
    }
}
