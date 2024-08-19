package org.jenkinsci.plugins.pitmutation.targets;

import java.util.Map;

import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;

public class BaseResultFactory {

    private BaseResultFactory() {
    }

    public static MutationResult getBaseMutationResultFrom(PitBuildAction action) {
        Map<String, MutationReport> reports = action.getReports();
        return reports.size() == 1 ? new SingleModuleResult(action) : new ProjectMutations(action);
    }
}
