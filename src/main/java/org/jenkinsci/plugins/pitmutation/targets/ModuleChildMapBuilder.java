package org.jenkinsci.plugins.pitmutation.targets;

import static java.util.stream.Collectors.groupingBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.pitmutation.Mutation;
import org.jenkinsci.plugins.pitmutation.MutationReport;

public class ModuleChildMapBuilder {

    private final MutationReport report;
    private final MutationResult parent;

    public ModuleChildMapBuilder(MutationReport report, MutationResult parent) {
        this.report = report;
        this.parent = parent;
    }

    public Map<String, MutatedPackage> build() {
        Map<String, MutatedPackage> childMap = new HashMap<>();
        for (String packageName : report.getMutationsByPackage().keySet()) {
            Map<String, List<Mutation>> classMutations =
                report.getMutationsForPackage(packageName).stream().collect(groupingBy(this::getClassName));

            childMap.put(packageName, new MutatedPackage(packageName, parent, classMutations));
        }
        return childMap;
    }

    private String getClassName(Mutation mutation) {
        String mutatedClassName = mutation.getMutatedClass();
        int firstDollar = mutatedClassName.indexOf('$');
        return firstDollar >= 0 ? mutatedClassName.substring(0, firstDollar) : mutatedClassName;
    }
}
