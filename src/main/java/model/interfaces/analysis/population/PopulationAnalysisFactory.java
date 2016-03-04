package model.interfaces.analysis.population;

import model.analysis.PopulationAnalysis;
import model.interfaces.populationModel.Population;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationAnalysisFactory {

    PopulationAnalysis createPopulationAnalysis(Population population);

}
