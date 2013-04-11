package org.jenkinsci.plugins.pitmutation.targets;

import org.jenkinsci.plugins.pitmutation.Mutation;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Ed Kimber
 * Date: 19/03/13
 * Time: 18:45
 */
public class MutatedClassTest extends MutationResultTest {

  @Before
  public void setup() {
    Collection<Mutation> mutations = new ArrayList<Mutation>();
    mutatedClass_ = new MutatedClass("TestClass", null, mutations);
  }

  @Test
  public void lineUrlsAreSet() {

  }

  private MutatedClass mutatedClass_;
}
