package org.jenkinsci.plugins.pitmutation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * `
 *
 * @author edward
 */
public class Mutation {
  private boolean detected;
  private String status;
  @JsonProperty("sourceFile")
  private String sourceFile;
  private String mutatedClass;
  private String mutatedMethod;
  private int lineNumber;
  private String mutator;
  private List<Integer> indexes = new ArrayList<>();
  private String killingTest;
  private String methodDescription;
  private String description;
  private List<String> blocks = new ArrayList<>();

  public Mutation() {
  }

  public String getMutatorClass() {
    int lastDot = mutator.lastIndexOf('.');
    String className = mutator.substring(lastDot + 1);
    return className.endsWith("Mutator") ? className.substring(0, className.length() - 7) : className;
  }

  public boolean isDetected() {
    return this.detected;
  }

  public String getStatus() {
    return this.status;
  }

  public String getSourceFile() {
    return this.sourceFile;
  }

  public String getMutatedClass() {
    return this.mutatedClass;
  }

  public String getMutatedMethod() {
    return this.mutatedMethod;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  public String getMutator() {
    return this.mutator;
  }

  public List<Integer> getIndexes() {
    return this.indexes;
  }

  public String getKillingTest() {
    return this.killingTest;
  }

  public String getMethodDescription() {
    return this.methodDescription;
  }

  public String getDescription() {
    return this.description;
  }

  public List<String> getBlocks() {
    return this.blocks;
  }

  public void setDetected(boolean detected) {
    this.detected = detected;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setSourceFile(String sourceFile) {
    this.sourceFile = sourceFile;
  }

  public void setMutatedClass(String mutatedClass) {
    this.mutatedClass = mutatedClass;
  }

  public void setMutatedMethod(String mutatedMethod) {
    this.mutatedMethod = mutatedMethod;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public void setMutator(String mutator) {
    this.mutator = mutator;
  }

  public void setIndexes(List<Integer> indexes) {
    this.indexes = indexes;
  }

  public void setKillingTest(String killingTest) {
    this.killingTest = killingTest;
  }

  public void setMethodDescription(String methodDescription) {
    this.methodDescription = methodDescription;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setBlocks(List<String> blocks) {
    this.blocks = blocks;
  }

  public boolean equals(final Object o) {
    if (o == this) {return true;}
    if (!(o instanceof Mutation)) {return false;}
    final Mutation other = (Mutation) o;
    if (!other.canEqual((Object) this)) {return false;}
    if (this.isDetected() != other.isDetected()) {return false;}
    final Object this$status = this.getStatus();
    final Object other$status = other.getStatus();
    if (this$status == null ? other$status != null : !this$status.equals(other$status)) {return false;}
    final Object this$sourceFile = this.getSourceFile();
    final Object other$sourceFile = other.getSourceFile();
    if (this$sourceFile == null ? other$sourceFile != null : !this$sourceFile.equals(other$sourceFile)) {return false;}
    final Object this$mutatedClass = this.getMutatedClass();
    final Object other$mutatedClass = other.getMutatedClass();
    if (this$mutatedClass == null ? other$mutatedClass != null : !this$mutatedClass.equals(other$mutatedClass)) {
      return false;
    }
    final Object this$mutatedMethod = this.getMutatedMethod();
    final Object other$mutatedMethod = other.getMutatedMethod();
    if (this$mutatedMethod == null ? other$mutatedMethod != null : !this$mutatedMethod.equals(other$mutatedMethod)) {
      return false;
    }
    if (this.getLineNumber() != other.getLineNumber()) {return false;}
    final Object this$mutator = this.getMutator();
    final Object other$mutator = other.getMutator();
    if (this$mutator == null ? other$mutator != null : !this$mutator.equals(other$mutator)) {return false;}
    final Object this$indexes = this.getIndexes();
    final Object other$indexes = other.getIndexes();
    if (this$indexes == null ? other$indexes != null : !this$indexes.equals(other$indexes)) {return false;}
    final Object this$killingTest = this.getKillingTest();
    final Object other$killingTest = other.getKillingTest();
    if (this$killingTest == null ? other$killingTest != null : !this$killingTest.equals(other$killingTest)) {
      return false;
    }
    final Object this$methodDescription = this.getMethodDescription();
    final Object other$methodDescription = other.getMethodDescription();
    if (this$methodDescription == null ?
        other$methodDescription != null :
        !this$methodDescription.equals(other$methodDescription)) {return false;}
    final Object this$description = this.getDescription();
    final Object other$description = other.getDescription();
    if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
      return false;
    }
    final Object this$blocks = this.getBlocks();
    final Object other$blocks = other.getBlocks();
    if (this$blocks == null ? other$blocks != null : !this$blocks.equals(other$blocks)) {return false;}
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Mutation;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + (this.isDetected() ? 79 : 97);
    final Object $status = this.getStatus();
    result = result * PRIME + ($status == null ? 43 : $status.hashCode());
    final Object $sourceFile = this.getSourceFile();
    result = result * PRIME + ($sourceFile == null ? 43 : $sourceFile.hashCode());
    final Object $mutatedClass = this.getMutatedClass();
    result = result * PRIME + ($mutatedClass == null ? 43 : $mutatedClass.hashCode());
    final Object $mutatedMethod = this.getMutatedMethod();
    result = result * PRIME + ($mutatedMethod == null ? 43 : $mutatedMethod.hashCode());
    result = result * PRIME + this.getLineNumber();
    final Object $mutator = this.getMutator();
    result = result * PRIME + ($mutator == null ? 43 : $mutator.hashCode());
    final Object $indexes = this.getIndexes();
    result = result * PRIME + ($indexes == null ? 43 : $indexes.hashCode());
    final Object $killingTest = this.getKillingTest();
    result = result * PRIME + ($killingTest == null ? 43 : $killingTest.hashCode());
    final Object $methodDescription = this.getMethodDescription();
    result = result * PRIME + ($methodDescription == null ? 43 : $methodDescription.hashCode());
    final Object $description = this.getDescription();
    result = result * PRIME + ($description == null ? 43 : $description.hashCode());
    final Object $blocks = this.getBlocks();
    result = result * PRIME + ($blocks == null ? 43 : $blocks.hashCode());
    return result;
  }

  public String toString() {
    return "Mutation(detected="
           + this.isDetected()
           + ", status="
           + this.getStatus()
           + ", sourceFile="
           + this.getSourceFile()
           + ", mutatedClass="
           + this.getMutatedClass()
           + ", mutatedMethod="
           + this.getMutatedMethod()
           + ", lineNumber="
           + this.getLineNumber()
           + ", mutator="
           + this.getMutator()
           + ", indexes="
           + this.getIndexes()
           + ", killingTest="
           + this.getKillingTest()
           + ", methodDescription="
           + this.getMethodDescription()
           + ", description="
           + this.getDescription()
           + ", blocks="
           + this.getBlocks()
           + ")";
  }
}
