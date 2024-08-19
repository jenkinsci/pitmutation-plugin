package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;

import java.util.Collection;
import java.util.List;

import static hudson.model.Result.SUCCESS;

/**
 * Decides if a build is failed or not based on conditions.
 *
 * @author edward
 * @author vasile.jureschi
 */
public class ResultDecider {

    private final Collection<Condition> buildConditions;

    protected ResultDecider(PercentageThresholdCondition thresholdCondition) {
        buildConditions = List.of(thresholdCondition);
    }

    protected ResultDecider(PercentageThresholdCondition thresholdCondition,
                            MustImproveCondition mustImproveCondition) {
        buildConditions = List.of(thresholdCondition, mustImproveCondition);
    }

    /**
     * Decide build result result.
     *
     * @param action the action
     * @return the worst result from all conditions
     */
    public Result decideBuildResult(PitBuildAction action) {
        Result result = SUCCESS;
        for (Condition condition : buildConditions) {
            Result conditionResult = condition.decideResult(action);
            result = conditionResult.isWorseThan(result) ? conditionResult : result;
        }
        return result;
    }
}
