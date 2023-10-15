package org.jenkinsci.plugins.pitmutation;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;

import java.io.File;
import java.io.IOException;

/**
 * The type Parse report callable.
 */
public class ParseReportCallable implements FilePath.FileCallable<FilePath[]> {

  private static final long serialVersionUID = 1L;

  private final String reportFilePath;

  /**
   * Instantiates a new Parse report callable.
   *
   * @param reportFilePath the report file path
   */
  public ParseReportCallable(String reportFilePath) {
    this.reportFilePath = reportFilePath;
  }

  @Override
  public FilePath[] invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
    FilePath[] r = new FilePath(f).list(reportFilePath);
    if (r.length < 1) {
      throw new IOException("No reports found at location:" + reportFilePath);
    }
    return r;
  }

  @Override
  public void checkRoles(RoleChecker roleChecker) throws SecurityException {

  }
}
