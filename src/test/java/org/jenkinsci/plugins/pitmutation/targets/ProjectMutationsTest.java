package org.jenkinsci.plugins.pitmutation.targets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ProjectMutationsTest {
    private ProjectMutations projectMutations;

    private @Mock PitBuildAction currentAction;
    private @Mock PitBuildAction previousAction;
    private @Mock ProjectMutations previousReport;

    @Before
    public void setUp() {
        initMocks(this);
        projectMutations = new ProjectMutations(currentAction);
    }

    @Test
    public void shouldReturnNullIfPreviousActionIsNull() {
        when(currentAction.getPreviousAction()).thenReturn(null);

        ProjectMutations previousResult = projectMutations.getPreviousResult();

        assertNull(previousResult);
    }

    @Test
    public void shouldReturnNullIfPreviousActionHasNoReport() {
        when(previousAction.getReport()).thenReturn(null);
        when(currentAction.getPreviousAction()).thenReturn(previousAction);

        ProjectMutations previousResult = projectMutations.getPreviousResult();

        assertNull(previousResult);
    }

    @Test
    public void shouldReturnPreviousReport() {
        when(previousAction.getReport()).thenReturn(previousReport);
        when(currentAction.getPreviousAction()).thenReturn(previousAction);

        ProjectMutations previousResult = projectMutations.getPreviousResult();

        assertEquals(previousReport, previousResult);
    }
}
