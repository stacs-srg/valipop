package validation;

import datastructure.summativeStatistics.generated.EventType;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class KaplanMeierAnalysis implements IKaplanMeierAnalysis {

    @Override
    public EventType getVariable() {
        return null;
    }

    @Override
    public int getYear() {
        return 0;
    }

    @Override
    public double getLogRank() {
        return 0;
    }

    @Override
    public boolean significantLogRankDifference() {
        return false;
    }

    @Override
    public double getHazardRatio() {
        return 0;
    }

    @Override
    public double getHazardRatioConfidenceIntervals() {
        return 0;
    }

    @Override
    public boolean significantHazardRatioSimilarity() {
        return false;
    }

    @Override
    public void setConfidenceLevel(int confidenceLevel) {

    }
}
