package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;
import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static hudson.model.Result.FAILURE;
import static hudson.model.Result.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PercentageThresholdConditionTest {

    @TestFactory
    Stream<DynamicTest> testFactory() {
        return Stream
            .of(new TestCase(80f, 85f, SUCCESS, "killPercentGreaterThanThreshold"),
                new TestCase(85f, 85f, SUCCESS, "killPercentEqualToThreshold"),
                new TestCase(90f, 85f, FAILURE, "killPercentLessThanThreshold"))
            .map(testCase -> dynamicTest(testCase.name, () -> {
                PitBuildAction action = mock(PitBuildAction.class);
                MutationResult report = mock(MutationResult.class);
                MutationStats stats = mock(MutationStats.class);
                when(action.getReport()).thenReturn(report);
                when(report.getMutationStats()).thenReturn(stats);
                when(stats.getKillPercent()).thenReturn(testCase.killPercent);

                PercentageThresholdCondition condition = new PercentageThresholdCondition(testCase.threshold);
                assertEquals(testCase.expectedResult, condition.decideResult(action));
            }));
    }

    private static class TestCase {
        float threshold;
        float killPercent;
        Result expectedResult;
        String name;

        TestCase(float threshold, float killPercent, Result expectedResult, String name) {
            this.threshold = threshold;
            this.killPercent = killPercent;
            this.expectedResult = expectedResult;
            this.name = name;
        }
    }
}
