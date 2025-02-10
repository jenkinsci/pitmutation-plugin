package org.jenkinsci.plugins.pitmutation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hudson.model.AbstractBuild;
import hudson.model.Result;

/**
 * @author edward
 */
public class PitBuildActionTest {
    private PitBuildAction action;
    private AbstractBuild owner;
    private AbstractBuild failedBuild;
    private AbstractBuild successBuild;

    @BeforeEach
    public void setUp() {
        failedBuild = mock(AbstractBuild.class);
        when(failedBuild.getResult()).thenReturn(Result.FAILURE);
        successBuild = mock(AbstractBuild.class);
        when(successBuild.getResult()).thenReturn(Result.SUCCESS);
        owner = mock(AbstractBuild.class);
        File mockFileSys = mock(File.class);
        File mockFile = mock(File.class);
        File[] files = new File[1];
        files[0] = mockFile;
        when(mockFileSys.listFiles(any(FilenameFilter.class))).thenReturn(files);

        when(owner.getRootDir()).thenReturn(mockFileSys);
        action = new PitBuildAction(owner);
    }

    @Test
    public void previousReturnsNullIfNoPreviousBuilds() {
        assertThat(action.getPreviousAction(), nullValue());
    }

    @Test
    public void previousReturnsNullIfAllPreviousBuildsFailed() {
        when(owner.getPreviousBuild()).thenReturn(failedBuild);
        assertThat(action.getPreviousAction(), nullValue());
    }

    @Test
    public void previousReturnsLastSuccessfulBuild() {
        PitBuildAction previousSucccessAction = mock(PitBuildAction.class);
        when(owner.getPreviousBuild()).thenReturn(failedBuild);
        when(failedBuild.getPreviousBuild()).thenReturn(successBuild);
        when(successBuild.getAction(PitBuildAction.class)).thenReturn(previousSucccessAction);

        assertThat(action.getPreviousAction(), is(previousSucccessAction));
    }
}
