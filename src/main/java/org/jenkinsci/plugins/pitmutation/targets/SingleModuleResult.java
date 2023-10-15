package org.jenkinsci.plugins.pitmutation.targets;

import hudson.model.Run;
import org.jenkinsci.plugins.pitmutation.MutationReport;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static org.jenkinsci.plugins.pitmutation.FileProcessor.SINGLE_MODULE_REPORT_FOLDER;

public class SingleModuleResult extends MutationResult<SingleModuleResult> {

    private final PitBuildAction action;

    public SingleModuleResult(PitBuildAction action) {
        super("packages", null);
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
        return "Packages";
    }

    @Override
    public String getDisplayName() {
        return "Packages";
    }

    @Override
    protected String getMutationReportDirectory() {
        return SINGLE_MODULE_REPORT_FOLDER;
    }

    @Override
    public Map<String, ? extends MutationResult<?>> getChildMap() {
        final Map<String, MutationReport> reports = action.getReports();
        if (reports.size() != 1) {
            throw new IllegalStateException("Expected one report.");
        }
        final MutationReport report = reports.values().stream().findFirst().orElse(null);
        return new ModuleChildMapBuilder(report, this).build();
    }

    @Override
    public int compareTo(@Nonnull SingleModuleResult other) {
        return this.getMutationStats().getUndetected() - other.getMutationStats().getUndetected();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SingleModuleResult that = (SingleModuleResult) o;
        return Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action);
    }
}
