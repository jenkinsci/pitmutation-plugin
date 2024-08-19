package org.jenkinsci.plugins.pitmutation.portlets;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.pitmutation.PitBuildAction;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author ybroeker
 */
public class PitColumn extends ListViewColumn {

    @DataBoundConstructor
    public PitColumn() {
    }

    public PitBuildAction getLastBuildAction(final Job<?, ?> job) {
        final Run<?, ?> lastSuccessfulBuild = job.getLastSuccessfulBuild();
        return getLastBuildAction(lastSuccessfulBuild);
    }

    public PitBuildAction getLastBuildAction(final Run<?, ?> lastSuccessfulBuild) {
        if (lastSuccessfulBuild == null) {
            return null;
        }

        return lastSuccessfulBuild.getAction(PitBuildAction.class);
    }

    public boolean hasCoverage(final Job<?, ?> job) {
        return getLastBuildAction(job) != null;
    }

    public BigDecimal getLineCoverage(final Job<?, ?> job) {
        final Run<?, ?> lastSuccessfulBuild = job.getLastSuccessfulBuild();
        return BigDecimal.valueOf(getLinePercent(lastSuccessfulBuild));
    }

    private Double getLinePercent(final Run<?, ?> lastSuccessfulBuild) {
        final Float percentageFloat = getPercentageFloat(lastSuccessfulBuild);
        final double doubleValue = percentageFloat.doubleValue();

        final int decimalPlaces = 2;
        BigDecimal bigDecimal = BigDecimal.valueOf(doubleValue);

        // setScale is immutable
        bigDecimal = bigDecimal.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    private Float getPercentageFloat(final Run<?, ?> lastSuccessfulBuild) {
        final PitBuildAction action = getLastBuildAction(lastSuccessfulBuild);

        if (action == null) {
            return 0f;
        }

        return action.getTarget().getMutationStats().getKillPercent();
    }

    public String getLineColor(final Job<?, ?> job, final BigDecimal amount) {
        if (amount == null) {
            return null;
        }

        if (job != null && !hasCoverage(job)) {
            return CoverageRange.NA.getLineHexString();
        }

        return CoverageRange.valueOf(amount.doubleValue()).getLineHexString();
    }

    public String getFillColor(final Job<?, ?> job, final BigDecimal amount) {
        if (amount == null) {
            return null;
        }

        if (job != null && !hasCoverage(job)) {
            return CoverageRange.NA.getFillHexString();
        }

        final Color c = CoverageRange.fillColorOf(amount.doubleValue());
        return CoverageRange.colorAsHexString(c);
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {
        @Override
        public ListViewColumn newInstance(@Nullable final StaplerRequest req, @Nonnull final JSONObject formData) {
            return new PitColumn();
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Pit Mutation Coverage";
        }
    }
}
