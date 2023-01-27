package org.jenkinsci.plugins.pitmutation;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * `
 *
 * @author edward
 */
@Data
public class Mutation {
  private boolean detected;
  private String status;
  private String sourceFile;
  private String mutatedClass;
  private String mutatedMethod;
  private int lineNumber;
  private String mutator;
  /**
   * @deprecated from 1 .9 the mutation library generates a list of indexes <a href="https://issues.jenkins.io/browse/JENKINS-68990">JENKINS-68990</a>
   */
  @Deprecated(forRemoval = true, since = "1.9")
  private Integer index;
  private List<Integer> indexes = new ArrayList<>();
  private String killingTest;
  private String methodDescription;
  private String description;
  /**
   * @deprecated from 1 .9 the mutation library generates a list of blocks <a href="https://issues.jenkins.io/browse/JENKINS-68990">JENKINS-68990</a>
   */
  @Deprecated(forRemoval = true, since = "1.9")
  private String block;
  private List<String> blocks = new ArrayList<>();

  public Mutation() {
  }

  public String getMutatorClass() {
    int lastDot = mutator.lastIndexOf('.');
    String className = mutator.substring(lastDot + 1);
    return className.endsWith("Mutator") ? className.substring(0, className.length() - 7) : className;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Mutation;
  }

}
