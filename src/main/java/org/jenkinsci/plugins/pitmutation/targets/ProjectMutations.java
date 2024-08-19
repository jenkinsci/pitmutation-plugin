package org.jenkinsci.plugins.pitmutation.targets;

import hudson.model.Run;
import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Ed Kimber
 */
public class ProjectMutations extends MutationResult<ProjectMutations> {

    private PitBuildAction action;

    public ProjectMutations(PitBuildAction action) {
        super("aggregate", null);
        this.action = action;
    }

    @Override
    public Run<?, ?> getOwner() {
        return action.getOwner();
    }

    @Override
    public MutationResult getPreviousResult() {
        PitBuildAction previousAction = action.getPreviousAction();
        return previousAction == null ? null : previousAction.getReport();
    }

    @Override
    public MutationStats getMutationStats() {
        return aggregateStats(action.getReports().values());
    }

    private static MutationStats aggregateStats(Collection<MutationReport> reports) {
        MutationStats stats = new MutationStatsImpl("", new ArrayList<>());
        for (MutationReport report : reports) {
            stats = stats.aggregate(report.getMutationStats());
        }
        return stats;
    }

    @Override
    public String getName() {
        return "Aggregated Reports";
    }

    public String getDisplayName() {
        return "Modules";
    }

    @Override
    public Map<String, ? extends MutationResult<?>> getChildMap() {
        Map<String, ModuleResult> childMap = new HashMap<>();
        Map<String, MutationReport> reports = action.getReports();
        for (Map.Entry<String, MutationReport> report : reports.entrySet()) {
            String reportName = report.getKey();
            childMap.put(reportName, new ModuleResult(reportName, this, report.getValue()));
        }
        return childMap;
    }

    @Override
    public int compareTo(@Nonnull ProjectMutations other) {
        return this.getMutationStats().getUndetected() - other.getMutationStats().getUndetected();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ProjectMutations that = (ProjectMutations) o;
        return Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action);
    }
}
