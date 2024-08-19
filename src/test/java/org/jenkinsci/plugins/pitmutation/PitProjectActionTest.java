package org.jenkinsci.plugins.pitmutation;


import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static hudson.model.Result.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PitProjectActionTest {

    @Mock
    private AbstractProject<?, ?> project;
    @Mock
    private AbstractBuild<?, ?> lastSuccessfulBuild;
    @Mock
    private AbstractBuild<?, ?> previousSuccessfulBuild;
    @Mock
    private AbstractBuild<?, ?> failedBuild;
    @Mock
    private PitBuildAction action;
    @Mock
    private StaplerRequest req;
    @Mock
    private StaplerResponse rsp;

    private PitProjectAction pitProjectAction;

    @BeforeEach
    void setUp() {
        pitProjectAction = new PitProjectAction(project);
    }

    @Test
    void getLastResult_noLastResult() {
        doReturn(null).when(project).getLastSuccessfulBuild();
        PitBuildAction lastResult = pitProjectAction.getLastResult();
        assertNull(lastResult);
    }

    @Test
    void getLastResult_returnsLastResult() {
        doReturn(lastSuccessfulBuild).when(project).getLastSuccessfulBuild();
        doReturn(SUCCESS).when(lastSuccessfulBuild).getResult();
        doReturn(action).when(lastSuccessfulBuild).getAction(PitBuildAction.class);

        PitBuildAction lastResult = pitProjectAction.getLastResult();
        assertEquals(action, lastResult);
    }

    @Test
    void getLastResultBuild_returnsLastBuildNumber() {
        doReturn(lastSuccessfulBuild).when(project).getLastSuccessfulBuild();
        doReturn(action).when(lastSuccessfulBuild).getAction(PitBuildAction.class);
        doReturn(10).when(lastSuccessfulBuild).getNumber();

        Integer lastResultBuild = pitProjectAction.getLastResultBuild();
        assertEquals(10, lastResultBuild);
    }

    @Test
    void getIconFileName_returnsCorrectPath() {
        String iconPath = pitProjectAction.getIconFileName();
        assertEquals("/plugin/pitmutation/pitest.png", iconPath);
    }

    @Test
    void getDisplayName_returnsCorrectName() {
        String name = pitProjectAction.getDisplayName();
        assertEquals("PIT Mutation Report", name);
    }

    @Test
    void getSearchUrl_returnsCorrectUrl() {
        String url = pitProjectAction.getSearchUrl();
        assertEquals("pitmutation", url);
    }

    @Test
    void isFloatingBoxActive_returnsTrue() {
        assertTrue(pitProjectAction.isFloatingBoxActive());
    }

    @Test
    void doIndex_redirectsCorrectly() throws IOException {
        doReturn(lastSuccessfulBuild).when(project).getLastSuccessfulBuild();
        doReturn(action).when(lastSuccessfulBuild).getAction(PitBuildAction.class);
        doReturn(10).when(lastSuccessfulBuild).getNumber();

        pitProjectAction.doIndex(req, rsp);

        verify(rsp).sendRedirect2("../10/pitmutation");
    }

    @Test
    void doIndex_buildNumberNull_redirectsCorrectly() throws IOException {
        doReturn(null).when(project).getLastSuccessfulBuild();
        pitProjectAction.doIndex(req, rsp);
        verify(rsp).sendRedirect2("nodata");
    }

    @Test
    void doGraph_CallsDoGraphOnLastResult() throws IOException {
        doReturn(lastSuccessfulBuild).when(project).getLastSuccessfulBuild();
        doReturn(action).when(lastSuccessfulBuild).getAction(PitBuildAction.class);

        pitProjectAction.doGraph(req, rsp);

        verify(action).doGraph(req, rsp);
    }
}
