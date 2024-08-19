package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;
import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static hudson.model.Result.SUCCESS;
import static hudson.model.Result.UNSTABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MustImproveConditionTest {

    @TestFactory
    Stream<DynamicTest> testFactory() {
        return Stream
            .of(new TestCase(null, 90f, SUCCESS, "noPreviousAction"),
                new TestCase(85f, 90f, SUCCESS, "currentKillPercentGreater"),
                new TestCase(85f, 85f, SUCCESS, "currentKillPercentEqual"),
                new TestCase(85f, 80f, UNSTABLE, "currentKillPercentLess"))
            .map(testCase -> dynamicTest(testCase.name, () -> {
                PitBuildAction action = mock(PitBuildAction.class);
                PitBuildAction previousAction = mock(PitBuildAction.class);
                MutationResult currentReport = mock(MutationResult.class);
                MutationResult previousReport = mock(MutationResult.class);
                MutationStats currentStats = mock(MutationStats.class);
                MutationStats previousStats = mock(MutationStats.class);

                when(action.getReport()).thenReturn(currentReport);
                when(currentReport.getMutationStats()).thenReturn(currentStats);
                when(currentStats.getKillPercent()).thenReturn(testCase.currentKillPercent);

                if (testCase.previousKillPercent != null) {
                    when(action.getPreviousAction()).thenReturn(previousAction);
                    when(previousAction.getReport()).thenReturn(previousReport);
                    when(previousReport.getMutationStats()).thenReturn(previousStats);
                    when(previousStats.getKillPercent()).thenReturn(testCase.previousKillPercent);
                }

                MustImproveCondition condition = new MustImproveCondition();
                assertEquals(testCase.expectedResult, condition.decideResult(action));
            }));
    }

    private static class TestCase {
        Float previousKillPercent;
        float currentKillPercent;
        Result expectedResult;
        String name;

        TestCase(Float previousKillPercent, float currentKillPercent, Result expectedResult, String name) {
            this.previousKillPercent = previousKillPercent;
            this.currentKillPercent = currentKillPercent;
            this.expectedResult = expectedResult;
            this.name = name;
        }
    }
}
