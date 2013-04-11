package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Ed Kimber
 * Date: 17/03/13
 * Time: 09:50
 */
public class MutationStatsTest {

  @Test
  public void correctPercentValue() {
    MutationStats stats = new MutationStats() {

      @Override
      public String getTitle() {
        return null;
      }

      @Override
      public int getUndetected() {
        return 89;
      }

      @Override
      public int getTotalMutations() {
        return 101;
      }
    };

    assertThat(stats.getKillPercent(), is(11.881f));
  }

  @Test
  public void mutationStatsImplTest() {
    MutationStats a = createMutationStatsA();
    MutationStats b = createMutationStatsB();
    assertThat(a.getTitle(), is("test"));
    assertThat(a.getTotalMutations(), is(3));
    assertThat(b.getTotalMutations(), is(3));
    assertThat(a.getUndetected(), is(1));
    assertThat(b.getUndetected(), is(2));
    assertThat(a.getKillPercent(), is(66.667f));
    assertThat(b.getKillPercent(), is(33.333f));
  }

  @Test
  public void mutationStatsDelta() {
    MutationStats delta = createMutationStatsA().delta(createMutationStatsB());

    assertThat(delta.getTotalMutations(), is(0));
    assertThat(delta.getUndetected(), is(-1));
    assertThat(delta.getKillPercent(), is(33.334f));
  }

  private MutationStatsImpl createMutationStatsA() {
    Mutation[] mutations = new Mutation[] {mock(Mutation.class), mock(Mutation.class), mock(Mutation.class)};
    when(mutations[0].isDetected()).thenReturn(true);
    when(mutations[1].isDetected()).thenReturn(false);
    when(mutations[2].isDetected()).thenReturn(true);
    return new MutationStatsImpl("test", Arrays.asList(mutations));
  }

  private MutationStatsImpl createMutationStatsB() {
    Mutation[] mutations = new Mutation[] {mock(Mutation.class), mock(Mutation.class), mock(Mutation.class)};
    when(mutations[0].isDetected()).thenReturn(true);
    when(mutations[1].isDetected()).thenReturn(false);
    when(mutations[2].isDetected()).thenReturn(false);
    return new MutationStatsImpl("test", Arrays.asList(mutations));
  }
}
