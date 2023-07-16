package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;

import static hudson.model.Result.FAILURE;
import static hudson.model.Result.SUCCESS;

class PercentageThresholdCondition implements Condition {
  private final float percentage;

  PercentageThresholdCondition(float percentage) {
    super();
    this.percentage = percentage;
  }

  @Override
  public Result decideResult(PitBuildAction action) {
    return action.getReport().getMutationStats().getKillPercent() >= percentage ? SUCCESS : FAILURE;
  }
}
