package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;

import static hudson.model.Result.SUCCESS;
import static hudson.model.Result.UNSTABLE;

class MustImproveCondition implements Condition {
  @Override
  public Result decideResult(final PitBuildAction action) {
    PitBuildAction previousAction = action.getPreviousAction();
    if (previousAction != null) {
      MutationStats previousStats = previousAction.getReport().getMutationStats();
      return action.getReport().getMutationStats().getKillPercent() >= previousStats.getKillPercent() ?
             SUCCESS :
             UNSTABLE;
    } else {
      return SUCCESS;
    }
  }
}
