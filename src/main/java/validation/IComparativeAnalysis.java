package validation;

import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.simulationLogic.StatisticalManipulationCalculationError;
import utils.time.UnsupportedDateConversion;

/**
 * The IComparativeAnalysis interface provides statistical tests to verify the simulated population against a given
 * population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface IComparativeAnalysis {


    /**
     * Runs Kaplan-Meier analysis, see the provided {@link IKaplanMeierAnalysis} class.
     *
     * @param expectedEvents the expected events
     * @param observedEvents the observed events
     * @return the km analysis
     */
    IKaplanMeierAnalysis runKaplanMeier(OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents) throws StatisticalManipulationCalculationError;


    /**
     * If all comparisons pass then return true, else return fail.
     *
     * @return have all comparative analyses passed
     */
    boolean passed();


    void runAnalysis() throws UnsupportedDateConversion, StatisticalManipulationCalculationError;

}
