package org.jenkinsci.plugins.pitmutation;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author edward
 */
public class MutationReport {

  private final Map<String, List<Mutation>> mutationsByPackage;

  private final Map<String, List<Mutation>> mutationsByClass;
  private int killCount = 0;

  public MutationReport(InputStream xmlReport) throws IOException, SAXException {
    this.mutationsByClass = new HashMap<>();
    this.mutationsByPackage = new HashMap<>();


    //    https://github.com/FasterXML/jackson-dataformat-xml/issues/219
    JacksonXmlModule module = new JacksonXmlModule();
    module.setDefaultUseWrapper(false);
    XmlMapper xmlMapper = new XmlMapper(module);
    Mutations mutations = xmlMapper.readValue(xmlReport, Mutations.class);

    mutations.getMutation().forEach(mutation -> {
      mutationsByClass.computeIfAbsent(mutation.getMutatedClass(), k -> new ArrayList<>()).add(mutation);
      if (mutation.isDetected()) {
        killCount++;
      }
      mutationsByPackage
        .computeIfAbsent(packageNameFromClass(mutation.getMutatedClass()), k -> new ArrayList<>())
        .add(mutation);

    });
  }

  public Collection<Mutation> getMutationsForPackage(String packageName) {
    return mutationsByPackage.computeIfAbsent(packageName, k -> new ArrayList<>());
  }

  public Map<String, List<Mutation>> getMutationsByPackage() {
    return mutationsByPackage;
  }

  public Collection<Mutation> getMutationsForClassName(String className) {
    return mutationsByClass.computeIfAbsent(className, k -> new ArrayList<>());
  }

  public MutationStats getMutationStats() {
    return new MutationStats() {
      @Override
      public String getTitle() {
        return "Report Stats";
      }

      @Override
      public int getUndetected() {
        return getTotalMutations() - killCount;
      }

      @Override
      public int getTotalMutations() {
        return mutationsByClass.values().stream().mapToInt(Collection::size).sum();
      }
    };
  }

  // TODO: Move somewhere out of this class
  static String packageNameFromClass(String fqcn) {
    int idx = fqcn.lastIndexOf('.');
    return fqcn.substring(0, idx != -1 ? idx : 0);
  }
}
