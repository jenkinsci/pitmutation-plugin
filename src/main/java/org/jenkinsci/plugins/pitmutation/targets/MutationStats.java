package org.jenkinsci.plugins.pitmutation.targets;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jenkinsci.plugins.pitmutation.portlets.CoverageRange;

/**
 * @author Ed Kimber
 */
public abstract class MutationStats {

    public abstract String getTitle();

    public abstract int getUndetected();

    public abstract int getTotalMutations();

    public int getKillCount() {
        return getTotalMutations() - getUndetected();
    }

    public float getKillPercent() {
        return round(100f * (float) (getTotalMutations() - getUndetected()) / (float) getTotalMutations());
    }

    public String getKillPercentFillColor() {
        return getCoverageColors().getFillHexString();
    }

    public String getKillPercentTextColor() {
        return getCoverageColors().getLineHexString();
    }

    private CoverageRange getCoverageColors() {
        return CoverageRange.valueOf(getKillPercent());
    }

    private float round(float ratio) {
        //TODO NaN mutation test
        if (Float.isNaN(ratio) || Float.isInfinite(ratio)) {return ratio;}
        BigDecimal bd = new BigDecimal(ratio);
        BigDecimal rounded = bd.setScale(3, RoundingMode.HALF_UP);
        return rounded.floatValue();
    }

    public MutationStats aggregate(final MutationStats other) {
        return new MutationStats() {
            @Override
            public String getTitle() {
                return MutationStats.this.getTitle() + ", " + other.getTitle();
            }

            @Override
            public int getUndetected() {
                return MutationStats.this.getUndetected() + other.getUndetected();
            }

            @Override
            public int getTotalMutations() {
                return MutationStats.this.getTotalMutations() + other.getTotalMutations();
            }
        };
    }

    public MutationStats delta(final MutationStats other) {
        return new MutationStats() {
            @Override
            public String getTitle() {
                return MutationStats.this.getTitle();
            }

            @Override
            public int getUndetected() {
                return MutationStats.this.getUndetected() - other.getUndetected();
            }

            @Override
            public int getTotalMutations() {
                return MutationStats.this.getTotalMutations() - other.getTotalMutations();
            }

            public float getKillPercent() {
                return round(MutationStats.this.getKillPercent() - other.getKillPercent());
            }
        };
    }
}
