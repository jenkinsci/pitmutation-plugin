package org.jenkinsci.plugins.pitmutation;

import hudson.model.Result;
import org.jenkinsci.plugins.pitmutation.targets.MutationStats;
import org.jenkinsci.plugins.pitmutation.targets.ProjectMutations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Ed Kimber Date: 17/03/13 Time: 17:55
 */
public class PitPublisherTest {

    private static final float MINIMUM_KILL_RATIO = 0.25f;

    @Mock
    private PitBuildAction action;
    @Mock
    private ProjectMutations report;
    @Mock
    private MutationStats stats;

    private PitPublisher publisher;
    private PitPublisherTSS publisherTSS;

    static class PitPublisherTSS extends PitPublisher {
        boolean infoImproveLogged = false;
        boolean infoPercentLogged = false;

        public PitPublisherTSS(String mutationStatsFile, float minimumKillRatio, boolean killRatioMustImprove) {
            super(mutationStatsFile, minimumKillRatio, killRatioMustImprove);
        }

        class MustImproveConditionTSS extends MustImproveCondition {
            void logInfo(final PitBuildAction action, MutationStats stats) {
                infoImproveLogged = true;
            }
        }

        Condition mustImprove() {
            return new MustImproveConditionTSS();
        }

        class PercentageThresholdConditionTSS extends PercentageThresholdCondition {
            PercentageThresholdConditionTSS(float percentage) {
                super(percentage);
            }

            void dologging(MutationStats stats) {
                infoPercentLogged = true;
            }
        }

        Condition percentageThreshold(final float percentage) {
            return new PercentageThresholdConditionTSS(percentage);
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDecideBuildResultFailsIfKillRatioTooLow() {
        publisherTSS = createPitPublisher(false);
        publisher = publisherTSS;
        final float TOO_LOW_RATIO = 0.2f;
        setupBoilderPlateMocks();
        Mockito.when(stats.getKillPercent()).thenReturn(TOO_LOW_RATIO);
        assertEquals(Result.FAILURE, publisher.decideBuildResult(action));
        assertFalse(publisherTSS.infoImproveLogged);
        assertTrue(publisherTSS.infoPercentLogged);
    }

    @Test
    public void testDecideBuildResultSuccessIfKillRatioSame() {
        publisher = createPitPublisher(false);
        final float BIGGER_RATIO = MINIMUM_KILL_RATIO + 0.2f;
        setupBoilderPlateMocks();
        Mockito.when(stats.getKillPercent()).thenReturn(MINIMUM_KILL_RATIO, BIGGER_RATIO);
        assertEquals(Result.SUCCESS, publisher.decideBuildResult(action));
        assertEquals(Result.SUCCESS, publisher.decideBuildResult(action));
    }

    @Test
    public void testMustImproveConditionSucceedsWhenPreviousActionIsNull() {
        publisher = createPitPublisher(true);
        final float BIGGER_RATIO = MINIMUM_KILL_RATIO + 0.2f;
        setupBoilderPlateMocks();
        Mockito.when(stats.getKillPercent()).thenReturn(BIGGER_RATIO);
        assertEquals(Result.SUCCESS, publisher.decideBuildResult(action));
    }

    @Test
    public void testMustImproveConditionFailsWhenWhenPreviousActionKillRatioIsBetter() {
        publisher = createPitPublisher(true);
        setupBoilderPlateMocks();
        setupPercentKillRatioOK();
        PitBuildAction prevAction = mock(PitBuildAction.class);
        ProjectMutations prevReport_ = Mockito.mock(ProjectMutations.class);
        MutationStats prevStats_ = Mockito.mock(MutationStats.class);
        when(action.getPreviousAction()).thenReturn(prevAction);
        when(prevAction.getReport()).thenReturn(prevReport_);
        when(prevReport_.getMutationStats()).thenReturn(prevStats_);
        when(stats.getKillPercent()).thenReturn(MINIMUM_KILL_RATIO + 0.5f);
        when(prevStats_.getKillPercent()).thenReturn(MINIMUM_KILL_RATIO + 0.6f, // previous is better
            MINIMUM_KILL_RATIO + 0.5f, // equal
            MINIMUM_KILL_RATIO + 0.4f); //previous is worse

        assertEquals(Result.UNSTABLE, publisher.decideBuildResult(action)); //previous is better
        assertEquals(Result.SUCCESS, publisher.decideBuildResult(action)); // previous is equal
        assertEquals(Result.SUCCESS, publisher.decideBuildResult(action)); //previous is worse
    }

    private void setupPercentKillRatioOK() {
        final float BIGGER_RATIO = MINIMUM_KILL_RATIO + 0.2f;
        when(stats.getKillPercent()).thenReturn(BIGGER_RATIO);
    }

    private PitPublisherTSS createPitPublisher(boolean mustImprove) {
        return new PitPublisherTSS("**/mutations.xml", MINIMUM_KILL_RATIO, mustImprove);
    }

    private void setupBoilderPlateMocks() {
        Mockito.when(action.getReport()).thenReturn(report);
        Mockito.when(report.getMutationStats()).thenReturn(stats);
    }

}
