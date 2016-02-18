package model.interfacesnew.analysis.statistical;

import model.enums.VariableType;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface KMAnalysis {

    VariableType getVariable();

    int getYear();

    double getLogRank();

    boolean signifcantLogRankDifference();

    double getHazardRatio();

    double getHazardRatioConfidenceIntervals();

    boolean significantHazardRatioSimilarity();

    void setConfidenceLevel(int confidenceLevel);

}
