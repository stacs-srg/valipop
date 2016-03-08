package model.interfaces.analysis.population;

import model.implementation.analysis.PopulationAnalysis;
import model.interfaces.populationModel.Population;

/**
 * The PopulationAnalysisFactory creates an instance of a {@link PopulationAnalysis} given a {@link Population}.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationAnalysisFactory {

    PopulationAnalysis createPopulationAnalysis(Population population);

}
