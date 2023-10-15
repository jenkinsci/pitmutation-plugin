package org.jenkinsci.plugins.pitmutation;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Project;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.jenkinsci.plugins.pitmutation.targets.ProjectMutations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static hudson.model.Result.FAILURE;
import static hudson.model.Result.SUCCESS;
import static hudson.tasks.BuildStepMonitor.STEP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * User: Ed Kimber Date: 17/03/13 Time: 17:55
 */
@ExtendWith(MockitoExtension.class)
class PitPublisherTest {

  private static final float MINIMUM_KILL_RATIO = 0.25f;

  @Mock
  private PitBuildAction action;
  @Mock
  private ProjectMutations report;
  @Mock
  private MutationStats stats;
  @Mock
  private PitLogger pitLogger;

  @Mock
  private FileProcessor fileProcessor;

  @Mock
  private ResultDecider resultDecider;

  @Test
  void ignoreMissingReports_ignore_noReports() throws Exception {
    FileProcessor fileProcessor = mock(FileProcessor.class);
    ResultDecider decider = mock(ResultDecider.class);
    PitPublisher publisher =
      new PitPublisher("**/mutations.xml", MINIMUM_KILL_RATIO, true, true, fileProcessor, decider, pitLogger);
    TaskListener taskListener = mock(TaskListener.class);
    Run<?, ?> build = mock(Run.class);
    publisher.perform(build, mock(FilePath.class), mock(EnvVars.class), mock(Launcher.class), taskListener);

    verify(build).setResult(SUCCESS);
    verify(pitLogger).logMissingReportsIgnored(any());
  }

  @Test
  void ignoreMissingReports_doNotIgnore_noReports() throws Exception {
    FileProcessor fileProcessor = mock(FileProcessor.class);
    ResultDecider decider = mock(ResultDecider.class);
    PitPublisher publisher =
      new PitPublisher("**/mutations.xml", MINIMUM_KILL_RATIO, true, false, fileProcessor, decider, pitLogger);
    TaskListener taskListener = mock(TaskListener.class);
    Run<?, ?> build = mock(Run.class);
    FilePath workspace = mock(FilePath.class);
    when(workspace.act(any(FilePath.FileCallable.class))).thenReturn(new FilePath[]{mock(FilePath.class), mock(FilePath.class)});

    publisher.perform(build, workspace, mock(EnvVars.class), mock(Launcher.class), taskListener);

    verify(build).setResult(FAILURE);
    verify(pitLogger).logBuildFailedNoReports(any());
  }

  @Test
  void oneReport_ioException() throws Exception {
    FileProcessor fileProcessor = mock(FileProcessor.class);
    ResultDecider decider = mock(ResultDecider.class);
    PitPublisher publisher = new PitPublisher(
      "./src/test/resources/org/jenkinsci/plugins/pitmutation/testmutations-00.xml",
      MINIMUM_KILL_RATIO,
      true,
      false,
      fileProcessor,
      decider,
      pitLogger);
    TaskListener taskListener = mock(TaskListener.class);
    FilePath workspace = mock(FilePath.class);
    FilePath report = mock(FilePath.class);
    when(report.exists()).thenReturn(true);
    when(report.isDirectory()).thenReturn(false);
    FilePath[] reports = {report};
    when(workspace.act(any(ParseReportCallable.class))).thenReturn(reports);
    doThrow(IOException.class).when(fileProcessor).copySingleModuleReport(any(), any());
    Run<?, ?> build = mock(Run.class);
    when(build.getRootDir()).thenReturn(new File("src/test/resources"));
    publisher.perform(build, workspace, mock(EnvVars.class), mock(Launcher.class), taskListener);

    verify(build).setResult(FAILURE);
    verify(pitLogger, never()).logResults(any(), any());
  }

  @Test
  void oneReport() throws Exception {
    FileProcessor fileProcessor = mock(FileProcessor.class);
    ResultDecider decider = mock(ResultDecider.class);
    PitPublisher publisher = new PitPublisher(
      "./src/test/resources/org/jenkinsci/plugins/pitmutation/testmutations-00.xml",
      MINIMUM_KILL_RATIO,
      true,
      false,
      fileProcessor,
      decider,
      pitLogger);
    TaskListener taskListener = mock(TaskListener.class);
    FilePath workspace = mock(FilePath.class);
    FilePath report = mock(FilePath.class);
    when(report.exists()).thenReturn(true);
    when(report.isDirectory()).thenReturn(false);
    FilePath[] reports = {report};
    when(workspace.act(any(ParseReportCallable.class))).thenReturn(reports);
    doNothing().when(fileProcessor).copySingleModuleReport(any(), any());
    Run<?, ?> build = mock(Run.class);
    when(build.getRootDir()).thenReturn(new File("src/test/resources"));
    when(decider.decideBuildResult(any())).thenReturn(SUCCESS);
    publisher.perform(build, workspace, mock(EnvVars.class), mock(Launcher.class), taskListener);

    verify(build).setResult(SUCCESS);
    verify(pitLogger).logResults(any(), any());
  }


  @Test
  void multiModuleReports() throws Exception {
    FileProcessor fileProcessor = mock(FileProcessor.class);
    ResultDecider decider = mock(ResultDecider.class);
    PitPublisher publisher = new PitPublisher(
      "./src/test/resources/org/jenkinsci/plugins/pitmutation/testmutations-00.xml",
      MINIMUM_KILL_RATIO,
      true,
      false,
      fileProcessor,
      decider,
      pitLogger);
    TaskListener taskListener = mock(TaskListener.class);
    FilePath workspace = mock(FilePath.class);
    FilePath report1 = mock(FilePath.class);
    when(report1.exists()).thenReturn(true);
    when(report1.isDirectory()).thenReturn(false);
    FilePath report2 = mock(FilePath.class);
    when(report2.exists()).thenReturn(true);
    when(report2.isDirectory()).thenReturn(false);

    FilePath[] reports = {report1, report2};
    when(workspace.act(any(ParseReportCallable.class))).thenReturn(reports);
    when(fileProcessor.getNames(any(), any())).thenReturn(Map.of(mock(FilePath.class),
                                                                   "1",
                                                                   mock(FilePath.class),
                                                                   "2"));
    doNothing().when(fileProcessor).copyMultiModuleReport(any(), any(), any());
    Run<?, ?> build = mock(Run.class);
    when(build.getRootDir()).thenReturn(new File("src/test/resources"));
    when(decider.decideBuildResult(any())).thenReturn(SUCCESS);
    publisher.perform(build, workspace, mock(EnvVars.class), mock(Launcher.class), taskListener);

    verify(build).setResult(SUCCESS);
    verify(pitLogger).logResults(any(), any());
  }


  @Test
  void multiModuleReports_ioException() throws Exception {
    FileProcessor fileProcessor = mock(FileProcessor.class);
    ResultDecider decider = mock(ResultDecider.class);
    PitPublisher publisher = new PitPublisher(
      "./src/test/resources/org/jenkinsci/plugins/pitmutation/testmutations-00.xml",
      MINIMUM_KILL_RATIO,
      true,
      false,
      fileProcessor,
      decider,
      pitLogger);
    TaskListener taskListener = mock(TaskListener.class);
    FilePath workspace = mock(FilePath.class);
    FilePath report1 = mock(FilePath.class);
    when(report1.exists()).thenReturn(true);
    when(report1.isDirectory()).thenReturn(false);

    FilePath report2 = mock(FilePath.class);
    when(report2.exists()).thenReturn(true);
    when(report2.isDirectory()).thenReturn(false);

    FilePath[] reports = {report1, report2};
    when(workspace.act(any(ParseReportCallable.class))).thenReturn(reports);
    when(fileProcessor.getNames(any(), any())).thenReturn(Map.of(mock(FilePath.class),
                                                                 "1",
                                                                 mock(FilePath.class),
                                                                 "2"));
    doThrow(IOException.class).when(fileProcessor).copyMultiModuleReport(any(), any(), any());
    Run<?, ?> build = mock(Run.class);
    when(build.getRootDir()).thenReturn(new File("src/test/resources"));
    publisher.perform(build, workspace, mock(EnvVars.class), mock(Launcher.class), taskListener);

    verify(build).setResult(FAILURE);
    verify(pitLogger, never()).logResults(any(), any());
  }
  @Test
  void getMinimumKillRatio() {
    PitPublisher pitPublisher = new PitPublisher("target/pit-reports/mutations.xml", 0.5f, false, false);
    assertEquals(0.5f, pitPublisher.getMinimumKillRatio());
  }

  @Test
  void getKillRatioMustImprove() {
    PitPublisher pitPublisher = new PitPublisher("target/pit-reports/mutations.xml", 0.5f, true, false);
    assertTrue(pitPublisher.getKillRatioMustImprove());
  }

  @Test
  void getIgnoreMissingReports() {
    PitPublisher pitPublisher = new PitPublisher("target/pit-reports/mutations.xml", 0.5f, false, true);
    assertTrue(pitPublisher.getIgnoreMissingReports());
  }

  @Test
  void setIgnoreMissingReports_true() {
    PitPublisher pitPublisher = new PitPublisher("target/pit-reports/mutations.xml", 0.5f, false, false);
    pitPublisher.setIgnoreMissingReports(true);
    assertTrue(pitPublisher.getIgnoreMissingReports());
  }

  @Test
  void setIgnoreMissingReports_false() {
    PitPublisher pitPublisher = new PitPublisher("target/pit-reports/mutations.xml", 0.5f, false, true);
    pitPublisher.setIgnoreMissingReports(false);
    assertFalse(pitPublisher.getIgnoreMissingReports());
  }

  @Test
  void getProjectAction() {
    PitPublisher pitPublisher = new PitPublisher("target/pit-reports/mutations.xml", 0.5f, false, true);
    Action projectAction = pitPublisher.getProjectAction((AbstractProject<?, ?>) mock(Project.class));
    assertNotNull(projectAction);
  }

  @Test
  void getMutationStatsFile() {
    PitPublisher pitPublisher = new PitPublisher("target/pit-reports/mutations.xml", 0.5f, false, true);
    assertEquals("target/pit-reports/mutations.xml", pitPublisher.getMutationStatsFile());
  }

  @Test
  void getRequiredMonitorService() {
    PitPublisher pitPublisher = new PitPublisher("target/pit-reports/mutations.xml", 0.5f, false, true);
    assertEquals(STEP, pitPublisher.getRequiredMonitorService());
  }
}
