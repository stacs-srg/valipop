package model.interfaces.analysis.statistical;

import model.enums.EventType;

/**
 * This interface is used as the return form for a Kaplan-Meier analysis
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface KaplanMeierAnalysis {

    /**
     * Returns the variable which this analysis was focused on.
     *
     * @return the variable
     */
    EventType getVariable();

    /**
     * Returns the intial year of this analysis. E.g. in the case of death the birth year of all individuals considered.
     *
     * @return the year
     */
    int getYear();

    /**
     * Returns the log rank value.
     *
     * @return the log rank value.
     */
    double getLogRank();

    /**
     * Considers if the difference in the compared data is significant.
     *
     * @return if true then significant then curves are different (not what we want).
     */
    boolean significantLogRankDifference();

    /**
     * Returns the hazard ratio. The hazard ratio is the hazard rate of the simulated population divided by the hazard
     * rate of the observed population. Is the ratio is 1 then the risk that the two populations experience are the
     * same and so we can say that for this variable that the populations are the same.
     *
     * @return the hazard ratio
     */
    double getHazardRatio();

    /**
     * Returns the the confidence interval around the hazard ratio. If the return value is R then this can be considered
     * as HR±R
     *
     * @return the hazard ratio confidence interval
     */
    double getHazardRatioConfidenceIntervals();

    /**
     * Returns true if the Hazard Ratio is significantly close to 1.
     *
     * @return true if the Hazard Ratio is significantly close to 1.
     */
    boolean significantHazardRatioSimilarity();

    /**
     * Sets confidence level. This is used to derive a z value. The confidence level should be given as a percentage
     * e.g. 90, 95, 99
     *
     * @param confidenceLevel the confidence level
     */
    void setConfidenceLevel(int confidenceLevel);

}
