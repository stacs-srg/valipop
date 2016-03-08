package model.interfaces.analysis.population;

import model.implementation.analysis.PopulationOccurrences;
import model.interfaces.populationModel.Population;

/**
 * The PopulationOccurrencesFactory creates an instance of a {@link PopulationOccurrences} given a {@link Population}.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationOccurrencesFactory {

    PopulationOccurrences createPopulationAnalysis(Population population);

}
