package org.jenkinsci.plugins.pitmutation;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static hudson.model.Result.FAILURE;
import static hudson.model.Result.SUCCESS;
import static hudson.tasks.BuildStepMonitor.STEP;

/**
 * The type Pit publisher.
 *
 * @author edward
 * @author vasile.jureschi
 */
public class PitPublisher extends Recorder implements SimpleBuildStep {

    public static final String GLOB_MUTATIONS_XML = "**/target/pit-reports/**/mutations.xml";
    private String mutationStatsFile;
    private boolean killRatioMustImprove;
    /**
     * If true and no reports are generated, the missing reports will not cause a build failure. Uses for cases where
     * there are test but nothing is mutated them and there are no reports generated.
     */
    private boolean ignoreMissingReports;
    private float minimumKillRatio;

    private FileProcessor fileProcessor;
    private ResultDecider resultDecider;
    private PitLogger pitLogger;

    /**
     * Instantiates a new Pit publisher. Only used in tests.
     *
     * @param mutationStatsFile    the mutation stats file
     * @param minimumKillRatio     the minimum kill ratio
     * @param killRatioMustImprove the kill ratio must improve
     * @param ignoreMissingReports do not fail the build if there are no reports
     */
    PitPublisher(String mutationStatsFile,
                 float minimumKillRatio,
                 boolean killRatioMustImprove,
                 boolean ignoreMissingReports,
                 FileProcessor fileProcessor,
                 ResultDecider resultDecider,
                 PitLogger pitLogger) {
        this.mutationStatsFile = mutationStatsFile;
        this.killRatioMustImprove = killRatioMustImprove;
        this.minimumKillRatio = minimumKillRatio;
        this.ignoreMissingReports = ignoreMissingReports;
        this.fileProcessor = fileProcessor;
        this.resultDecider = resultDecider;
        this.pitLogger = pitLogger;
    }

    /**
     * Instantiates a new Pit publisher with Default-Values.
     * <p>
     * {@link #mutationStatsFile} is set to {@code **{@literal /}target/pit-reports/**{@literal /}mutations.xml},
     * {@link #minimumKillRatio} is set to {@code 0.0},
     * {@link #killRatioMustImprove} is set to {@code false},
     * {@link #ignoreMissingReports} is set to {@code false},
     */
    @DataBoundConstructor
    public PitPublisher(String mutationStatsFile,
                        float minimumKillRatio,
                        boolean killRatioMustImprove,
                        boolean ignoreMissingReports) {
        this(mutationStatsFile,
             minimumKillRatio,
             killRatioMustImprove,
             ignoreMissingReports,
             new FileProcessor(),
             killRatioMustImprove ?
             new ResultDecider(new PercentageThresholdCondition(minimumKillRatio)) :
             new ResultDecider(new PercentageThresholdCondition(minimumKillRatio), new MustImproveCondition()),
             new PitLogger());
    }

    @DataBoundSetter
    public void setIgnoreMissingReports(final boolean ignoreMissingReports) {
        this.ignoreMissingReports = ignoreMissingReports;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> build,
                        @Nonnull FilePath workspace,
                        @NonNull EnvVars env,
                        @Nonnull Launcher launcher,
                        @Nonnull TaskListener listener) {
        try {
            process(build, workspace, env, launcher, listener);
        } catch (IOException e) {
            Util.displayIOException(e, listener);
            build.setResult(FAILURE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void process(@Nonnull Run<?, ?> build,
                         @Nonnull FilePath workspace,
                         @NonNull EnvVars env,
                         @Nonnull Launcher launcher,
                         @Nonnull TaskListener listener) throws InterruptedException, IOException {
        FilePath[] reports = workspace.act(new ParseReportCallable(mutationStatsFile));

        if (noReportsAndMissingReportsIgnored(reports)) {
            pitLogger.logMissingReportsIgnored(listener);
            build.setResult(SUCCESS);
            return;
        } else if (noReportsAndMissingReportsNotIgnored(reports)) {
            pitLogger.logBuildFailedNoReports(listener);
            build.setResult(FAILURE);
            return;
        }

        pitLogger.logLookingForReports(listener, workspace);
        FilePath buildTarget = new FilePath(build.getRootDir());
        if (reports.length == 1) {
            fileProcessor.copySingleModuleReport(reports[0], buildTarget);
        } else {
            for (Map.Entry<FilePath, String> entry : fileProcessor
                .getNames(reports, workspace.getRemote())
                .entrySet()) {
                FilePath filePath = entry.getKey();
                String module = entry.getValue();
                pitLogger.logPublishReport(listener, workspace);
                fileProcessor.copyMultiModuleReport(filePath, buildTarget, module);
            }
        }


        PitBuildAction action = new PitBuildAction(build);
        build.addAction(action);
        build.setResult(resultDecider.decideBuildResult(action));

        pitLogger.logResults(listener, action);
    }

    private boolean noReportsAndMissingReportsIgnored(FilePath[] reports) {
        if (reports == null && ignoreMissingReports) {
            return true;
        } else if (reports == null) {
            return false;
        }
        return Arrays.stream(reports).anyMatch(report -> {
            try {
                return (!report.exists() || report.isDirectory()) && ignoreMissingReports;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    private boolean noReportsAndMissingReportsNotIgnored(FilePath[] reports) {
        if (reports == null && !ignoreMissingReports) {
            return false;
        } else if (reports == null) {
            return true;
        }
        return Arrays.stream(reports).anyMatch(report -> {
            try {
                return (!report.exists() || report.isDirectory()) && !ignoreMissingReports;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new PitProjectAction(project);
    }


    /**
     * Required by plugin config
     *
     * @return the minimum kill ratio
     */
    public float getMinimumKillRatio() {
        return minimumKillRatio;
    }

    /**
     * Required by plugin config
     *
     * @return the kill ratio must improve
     */
    public boolean getKillRatioMustImprove() {
        return killRatioMustImprove;
    }

    /**
     * Required by plugin config
     *
     * @return the mutation stats file
     */
    public String getMutationStatsFile() {
        return mutationStatsFile;
    }

    /**
     * Required by plugin config
     *
     * @return ignore missing reports flag
     */
    public boolean getIgnoreMissingReports() {
        return ignoreMissingReports;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return STEP;
    }

}
