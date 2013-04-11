package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ed Kimber
 */
public class MutatedLineTest {

  @Test
  public void getsMutatorClassNames() {
    Mutation[] mutations = new Mutation[3];

    mutations[0] = mock(Mutation.class);
    mutations[1] = mock(Mutation.class);
    mutations[2] = mock(Mutation.class);

    when(mutations[0].getMutatorClass()).thenReturn("BobsMutator");
    when(mutations[1].getMutatorClass()).thenReturn("NinjaMutator");
    when(mutations[2].getMutatorClass()).thenReturn("BobsMutator");

    MutatedLine line = new MutatedLine("0", null, Arrays.asList(mutations));

    assertThat(line.getMutationCount(), is(3));
    assertThat(line.getMutators(), hasSize(2));
  }


}
