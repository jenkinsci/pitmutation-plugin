package org.jenkinsci.plugins.pitmutation;

import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;

/**
 * Logging class for various pit information.
 *
 * @author edward
 * @author vasile.jureschi
 */
public class PitLogger {

    PitLogger() {
    }

    public void logResults(TaskListener listener, PitBuildAction action) {
        MutationStats mutationStats = action.getReport().getMutationStats();
        PitBuildAction previousAction = action.getPreviousAction();
        if (previousAction != null) {
            float previousKillPercent = previousAction.getReport().getMutationStats().getKillPercent();
            logPrevious(mutationStats.getKillPercent(), previousKillPercent, listener);
        }
        logCurrent(mutationStats, listener);
    }

    public void logMissingReportsIgnored(TaskListener listener) {
        listener
            .getLogger()
            .println("Build successful as no reports generated and build set to ignore missing reports. "
                     + "If the reports should be present set 'ignoreMissingReports' to false and run the build again to fail the build.");
    }

    public void logBuildFailedNoReports(TaskListener listener) {
        listener
            .getLogger()
            .println("Build failed as no reports generated and build set to not ignore missing reports. "
                     + "If no reports should be generated set 'ignoreMissingReports' to true and run the build again to pass the build.");
    }

    private void logCurrent(MutationStats currentStats, TaskListener listener) {
        listener
            .getLogger()
            .printf("Kill ratio is %f \\u0025 (%d %d)%n",
                    currentStats.getKillPercent(),
                    currentStats.getKillCount(),
                    currentStats.getTotalMutations());
    }


    private void logPrevious(float currentKillPercent, float previousKillPercent, TaskListener listener) {
        listener.getLogger().println("Previous kill ratio was " + previousKillPercent + "%");
        listener.getLogger().println("This kill ration is " + currentKillPercent + "%");
    }

    public void logLookingForReports(TaskListener listener, FilePath workspace) {
        listener.getLogger().println("Looking for PIT reports in " + workspace.getRemote());
    }

    public void logPublishReport(TaskListener listener, FilePath workspace) {
        listener.getLogger().println("Publishing mutation report: " + workspace.getRemote());
    }

    public void log(TaskListener listener, String message) {
        listener.getLogger().println(message);
    }
}
