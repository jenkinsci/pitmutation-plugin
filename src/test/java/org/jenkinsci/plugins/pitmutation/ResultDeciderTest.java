package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static hudson.model.Result.FAILURE;
import static hudson.model.Result.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultDeciderTest {

  @TestFactory
  Stream<DynamicTest> testFactory() {
    // Create a collection of inputs for the test.
    Collection<Object[]> inputs = Arrays.asList(new Object[][] {
      {SUCCESS, SUCCESS, SUCCESS},
      {FAILURE, SUCCESS, FAILURE},
      {SUCCESS, FAILURE, FAILURE},
      {FAILURE, FAILURE, FAILURE}
    });

    // Create and return the dynamic tests.
    return inputs.stream().map(input -> DynamicTest.dynamicTest(
      "Testing with thresholdResult = " + input[0] + ", mustImproveResult = " + input[1],
      () -> runTest((Result) input[0], (Result) input[1], (Result) input[2])
                                                               ));
  }

  private void runTest(Result thresholdResult, Result mustImproveResult, Result expectedResult) {
    PercentageThresholdCondition thresholdCondition = mock(PercentageThresholdCondition.class);
    MustImproveCondition mustImproveCondition = mock(MustImproveCondition.class);
    PitBuildAction action = mock(PitBuildAction.class);
    when(thresholdCondition.decideResult(action)).thenReturn(thresholdResult);
    when(mustImproveCondition.decideResult(action)).thenReturn(mustImproveResult);
    ResultDecider decider = new ResultDecider(thresholdCondition, mustImproveCondition);
    Result result = decider.decideBuildResult(action);
    assertEquals(expectedResult, result);
  }
}
