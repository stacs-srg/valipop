package validation;

import datastructure.summativeStatistics.generated.EventType;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import utils.time.CompoundTimeUnit;
import utils.time.Date;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class KaplanMeierAnalysis implements IKaplanMeierAnalysis {

    private final Date year;
    private final EventType event;
    private double logRankValue;

    private double p = 0.05;

    public KaplanMeierAnalysis(EventType event, Date year, double logRankValue) {
        this.event = event;
        this.year = year;
        this.logRankValue = logRankValue;
    }

    @Override
    public EventType getVariable() {
        return event;
    }

    @Override
    public Date getYear() {
        return year;
    }

    @Override
    public double getLogRankValue() {
        return logRankValue;
    }

    public void setLogRankValue(double logRankValue) {
        this.logRankValue = logRankValue;
    }

    @Override
    public double getPValue() {
        ChiSquaredDistribution cSD = new ChiSquaredDistribution(1.0);
        return 1 - cSD.cumulativeProbability(logRankValue);
    }

    @Override
    public boolean significantDifferenceBetweenGroups() {
        ChiSquaredDistribution cSD = new ChiSquaredDistribution(1.0);
        double p = 1 - cSD.cumulativeProbability(logRankValue);

        if(Double.isNaN(p)) {
            return true;
        }

        return this.p >= p;
    }

    @Override
    public void setConfidenceLevel(double confidenceLevel) {
        if (confidenceLevel <= 1) {
            this.p = confidenceLevel;
        }
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


}
