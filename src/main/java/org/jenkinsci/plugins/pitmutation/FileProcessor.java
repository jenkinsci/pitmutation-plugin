package org.jenkinsci.plugins.pitmutation;

import hudson.FilePath;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class FileProcessor {

  public static final String SINGLE_MODULE_REPORT_FOLDER = "mutation-report-all";
  public static final String MULTI_MODULE_REPORT_FORMAT = "mutation-report-%s";

  public void copySingleModuleReport(FilePath source, FilePath buildTarget) throws IOException, InterruptedException {
    copyMutationReports(source, buildTarget, SINGLE_MODULE_REPORT_FOLDER);
  }

  public void copyMultiModuleReport(FilePath source, FilePath buildTarget, String module) throws IOException, InterruptedException {
    copyMutationReports(source, buildTarget, String.format(MULTI_MODULE_REPORT_FORMAT, module));
  }

  private void copyMutationReports(FilePath source, FilePath buildTarget, String mutationFilePath) throws IOException,
                                                                                                          InterruptedException {
    var targetPath = new FilePath(buildTarget, mutationFilePath);
    var parent = ofNullable(source.getParent()).orElseThrow(() -> new IOException("Mutation file not found"));
    parent.copyRecursiveTo(targetPath);
  }

  public Map<FilePath, String> getNames(FilePath[] reports, String base) {
    Map<FilePath, String> names = new HashMap<>();
    for (int i = 0; i < reports.length; i++) {
      FilePath report = reports[i];

      final String moduleName;
      if (StringUtils.isBlank(base)) {
        moduleName = String.valueOf(i == 0 ? null : i);
      } else {
        String[] partsFromRemoteWithoutBase = report.getRemote().replace(base, "").split("[/\\\\]");
        if (partsFromRemoteWithoutBase.length > 1) {
          moduleName = partsFromRemoteWithoutBase[1];
        } else {
          moduleName = String.valueOf(i == 0 ? null : i);
        }
      }

      names.put(report, moduleName);
    }
    return names;
  }
}