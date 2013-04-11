package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.metrics.*;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Ed Kimber
 */
public class MutationSetTest {

  @Test
  public void basicOps() {
    MetricSet metricSet = new MetricSet(null);
    IntMetric undetected = MutationMetric.UNDETECTED.createMetric(8);
    IntMetric total = MutationMetric.TOTAL_MUTATIONS.createMetric(16);
    IntPercentMetric killRatio = MutationMetric.KILL_RATIO.createMetric(new IntPercentage(undetected, total));

    metricSet.put(MutationMetric.UNDETECTED, undetected);
    metricSet.put(MutationMetric.TOTAL_MUTATIONS, total);
    metricSet.put(MutationMetric.KILL_RATIO, killRatio);

    assertThat(metricSet.getMetric(MutationMetric.UNDETECTED).getValue(), is(8));
    assertThat(metricSet.getMetric(MutationMetric.TOTAL_MUTATIONS).getValue(), is(16));
    assertThat(metricSet.getMetric(MutationMetric.KILL_RATIO).getValue().getPercentage(), is(50f));
  }
}
