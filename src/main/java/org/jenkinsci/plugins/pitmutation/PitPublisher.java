package org.jenkinsci.plugins.pitmutation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hudson.Util;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import net.sf.json.JSONObject;

/**
 * @author edward
 */
public class PitPublisher extends Recorder {
  @Extension
  public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

  @DataBoundConstructor
  public PitPublisher(String mutationStatsFile, float minimumKillRatio, boolean killRatioMustImprove) {
    mutationStatsFile_ = mutationStatsFile;
    killRatioMustImprove_ = killRatioMustImprove;
    minimumKillRatio_ = minimumKillRatio;
    buildConditions_ = new ArrayList<Condition>();
    buildConditions_.add(percentageThreshold(minimumKillRatio));
    if (killRatioMustImprove) {
      buildConditions_.add(mustImprove());
    }
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException,
          InterruptedException {
    listener_ = listener;
    build_ = build;

    if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
      listener_.getLogger().println("Looking for PIT reports in " + build.getModuleRoot().getRemote());

      final FilePath[] moduleRoots = build.getModuleRoots();
      final boolean multipleModuleRoots =
              moduleRoots != null && moduleRoots.length > 1;
      final FilePath moduleRoot = multipleModuleRoots ? build.getWorkspace() : build.getModuleRoot();

      ParseReportCallable fileCallable = new ParseReportCallable(mutationStatsFile_);
      FilePath[] reports = moduleRoot.act(fileCallable);
      publishReports(reports, new FilePath(build.getRootDir()));

      //publish latest reports
      PitBuildAction action = new PitBuildAction(build);
      build.getActions().add(action);
      build.setResult(decideBuildResult(action));
    }
    return true;
  }

  void publishReports(FilePath[] reports, FilePath buildTarget) {
    for (int i = 0; i < reports.length; i++) {
      FilePath report = reports[i];
      listener_.getLogger().println("Publishing mutation report: " + report.getRemote());

      final FilePath targetPath = new FilePath(buildTarget, "mutation-report" + (i == 0 ? "" : i));
      try {
        reports[i].getParent().copyRecursiveTo(targetPath);
      } catch (IOException e) {
        Util.displayIOException(e, listener_);
        e.printStackTrace(listener_.fatalError("Unable to copy coverage from " + reports[i] + " to " + buildTarget));
        build_.setResult(Result.FAILURE);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  boolean mutationsReportExists(FilePath reportDir) {
    if (reportDir == null) {
      return false;
    }
    try {
      FilePath[] search = reportDir.list("**/mutations.xml");
      return search.length > 0;
    }
    catch (IOException e) {
      return false;
    }
    catch (InterruptedException e) {
      return false;
    }
  }

  /**
   * @return the worst result from all conditions
   */
  public Result decideBuildResult(PitBuildAction action) {
    Result result = Result.SUCCESS;
    for (Condition condition : buildConditions_) {
      Result conditionResult = condition.decideResult(action);
      result = conditionResult.isWorseThan(result) ? conditionResult : result;
    }
    return result;
  }


  /**
   * Required by plugin config
   */
  public float getMinimumKillRatio() {
    return minimumKillRatio_;
  }

  /**
   * Required by plugin config
   */
  public boolean getKillRatioMustImprove() {
    return killRatioMustImprove_;
  }

  /**
   * Required by plugin config
   */
  public String getMutationStatsFile() {
    return mutationStatsFile_;
  }

  private Condition percentageThreshold(final float percentage) {
    return new Condition() {
      public Result decideResult(PitBuildAction action) {
        MutationStats stats = action.getReport().getMutationStats();
        listener_.getLogger().println("Kill ratio is " + stats.getKillPercent() +"% ("
                                      + stats.getKillCount() + "  " + stats.getTotalMutations() +")");
        return stats.getKillPercent() >= percentage ? Result.SUCCESS : Result.FAILURE;
      }
    };
  }

  private Condition mustImprove() {
    return new Condition() {
      public Result decideResult(final PitBuildAction action) {
        PitBuildAction previousAction = action.getPreviousAction();
        if (previousAction != null) {
          MutationStats stats = previousAction.getReport().getMutationStats();
          listener_.getLogger().println("Previous kill ratio was " + stats.getKillPercent() + "%");
          return action.getReport().getMutationStats().getKillPercent() <= stats.getKillPercent()
                  ? Result.UNSTABLE : Result.SUCCESS;
        }
        else {
          return Result.SUCCESS;
        }
      }
    };
  }

  @Override
  public BuildStepDescriptor<Publisher> getDescriptor() {
    return DESCRIPTOR;
  }

  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }

  private FilePath getReportDir(FilePath root) throws IOException, InterruptedException {
    FilePath reportsDir = new FilePath(root, mutationStatsFile_);
    if (reportsDir.isDirectory()) {
      return reportsDir;
    }
    else {
      return reportsDir.getParent();
    }
  }

  private List<Condition> buildConditions_;
  private String mutationStatsFile_;
  private boolean killRatioMustImprove_;
  private float minimumKillRatio_;
  private transient BuildListener listener_;
  private AbstractBuild<?,?> build_;

  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    public DescriptorImpl() {
      super(PitPublisher.class);
    }

    @Override
    public String getDisplayName() {
      return Messages.PitPublisher_DisplayName();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      req.bindParameters(this, "pitmutation");
      save();
      return super.configure(req, formData);
    }

    /**
     * Creates a new instance of {@link PitPublisher} from a submitted form.
     */
    @Override
    public PitPublisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
      PitPublisher instance = req.bindJSON(PitPublisher.class, formData);
      return instance;
    }
  }

  public static class ParseReportCallable implements FilePath.FileCallable<FilePath[]> {

    private static final long serialVersionUID = 1L;

    private final String reportFilePath;

    public ParseReportCallable(String reportFilePath) {
      this.reportFilePath = reportFilePath;
    }

    public FilePath[] invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
      FilePath[] r = new FilePath(f).list(reportFilePath);
      if (r.length < 1) {
        throw new IOException("No reports found at location:" + reportFilePath);
      }
      return r;
    }
  }
}
