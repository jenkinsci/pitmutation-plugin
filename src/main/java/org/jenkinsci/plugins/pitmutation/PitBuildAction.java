package org.jenkinsci.plugins.pitmutation;

import hudson.FilePath;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Result;
import hudson.model.Run;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.jenkinsci.plugins.pitmutation.targets.BaseResultFactory;
import org.jenkinsci.plugins.pitmutation.targets.MutationResult;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hudson.model.Result.FAILURE;


/**
 * @author edward
 */
@Slf4j
public class PitBuildAction implements HealthReportingAction,
                                       StaplerProxy {

    private static final Pattern MUTATION_REPORT_PATTERN = Pattern.compile(".*mutation-report-([^/\\\\]*).*");

    @Getter
    private Run<?, ?> owner;
    private Map<String, MutationReport> reports;

    public PitBuildAction(Run<?, ?> owner) {
        this.owner = owner;
    }

    public PitBuildAction getPreviousAction() {
        Run<?, ?> build = owner;
        while (true) {
            build = build.getPreviousBuild();
            if (build == null) {return null;}
            if (build.getResult() == FAILURE) {continue;}
            PitBuildAction action = build.getAction(PitBuildAction.class);
            if (action != null) {return action;}
        }
    }

    @Override
    public MutationResult getTarget() {
        return getReport();
    }

    public MutationResult getReport() {
        return BaseResultFactory.getBaseMutationResultFrom(this);
    }

    public synchronized Map<String, MutationReport> getReports() {
        if (reports == null) {
            reports = readReports();
        }
        return reports;
    }

    private Map<String, MutationReport> readReports() {
        Map<String, MutationReport> reports = new HashMap<>();

        try {
            FilePath[] files = new FilePath(owner.getRootDir()).list("mutation-report-*/mutations.xml");

            if (files.length < 1) {
                log.warn("Could not find mutation-report-*/mutations.xml in " + owner.getRootDir());
            }

            for (int i = 0; i < files.length; i++) {
                log.warn("Creating report for file: " + files[i].getRemote());

                String name;
                Matcher m = MUTATION_REPORT_PATTERN.matcher(files[i].getRemote());
                if (m.find()) {
                    name = m.group(1);
                } else {
                    name = String.valueOf(i);
                }
                reports.put(name, new MutationReport(files[i].read()));
            }
        } catch (IOException | InterruptedException | SAXException e) {
            e.printStackTrace();
        }
        return reports;
    }

    /**
     * Getter for property 'previousResult'.
     *
     * @return Value for property 'previousResult'.
     */
    public PitBuildAction getPreviousResult() {
        return getPreviousResult(owner);
    }

    /**
     * Gets the previous {@link PitBuildAction} of the given build.
     */
    static PitBuildAction getPreviousResult(Run<?, ?> start) {
        Run<?, ?> b = start;
        while (true) {
            b = b.getPreviousNotFailedBuild();
            if (b == null) {
                return null;
            }
            assert b.getResult() != FAILURE : "We asked for the previous not failed build";
            PitBuildAction r = b.getAction(PitBuildAction.class);
            if (r != null) {
                return r;
            }
        }
    }

    @Override
    public HealthReport getBuildHealth() {
        return new HealthReport((int) getReport().getMutationStats().getKillPercent(),
                                Messages._BuildAction_Description(getReport().getMutationStats().getKillPercent()));
    }

    @Override
    public String getIconFileName() {
        return "/plugin/pitmutation/pitest.png";
    }

    @Override
    public String getDisplayName() {
        return Messages.BuildAction_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "pitmutation";
    }

    /**
     * Generates the graph that shows the coverage trend up to this report.
     *
     * @param req {@link StaplerRequest}
     * @param rsp {@link StaplerResponse}
     * @throws IOException if sending the redirect or generating graph fails
     */
    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }

        Calendar t = owner.getTimestamp();

        if (req.checkIfModified(t, rsp)) {
            return; // up to date
        }
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<>();


        final JFreeChart chart = ChartFactory.createLineChart(null, // chart title
                                                              null, // unused
                                                              "%", // range axis label
                                                              dsb.build(), // data
                                                              PlotOrientation.VERTICAL, // orientation
                                                              true, // include legend
                                                              true, // tooltips
                                                              false // urls
                                                             );//    JFreeChart chart = new MutationChart(this).createChart();
        ChartUtil.generateGraph(req, rsp, chart, 500, 200);
    }
}
