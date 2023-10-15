package org.jenkinsci.plugins.pitmutation;

import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.PrintStream;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PitLoggerTest {

  @Mock
  private TaskListener listener;
  @Mock
  private PitBuildAction action;
  @Mock
  private MutationResult report;
  @Mock
  private MutationStats stats;
  @Mock
  private PrintStream printStream;

  private PitLogger pitLogger;

  @BeforeEach
  void setUp() {
    pitLogger = new PitLogger();

    when(listener.getLogger()).thenReturn(printStream);
  }

  @Test
  void logResults() {
    when(action.getReport()).thenReturn(report);
    when(action.getPreviousAction()).thenReturn(action);
    when(report.getMutationStats()).thenReturn(stats);
    when(stats.getKillPercent()).thenReturn(70.0f);

    pitLogger.logResults(listener, action);

    verify(printStream, atLeastOnce()).println(anyString());
  }

  @Test
  void logMissingReportsIgnored() {
    pitLogger.logMissingReportsIgnored(listener);
    verify(printStream).println(anyString());
  }

  @Test
  void logBuildFailedNoReports() {
    pitLogger.logBuildFailedNoReports(listener);
    verify(printStream).println(anyString());
  }

  @Test
  void logLookingForReports() {
    FilePath workspace = new FilePath(new File("path/to/workspace"));
    pitLogger.logLookingForReports(listener, workspace);

    verify(printStream).println(anyString());
  }

  @Test
  void logPublishReport() {
    FilePath workspace = new FilePath(new File("path/to/workspace"));
    pitLogger.logPublishReport(listener, workspace);

    verify(printStream).println(anyString());
  }
}
