package model.interfaces.analysis.population;

import model.implementation.analysis.GeneratedPopulationComposition;
import model.interfaces.populationModel.Population;

/**
 * The GeneratedPopulationCompositionFactory creates an instance of a {@link GeneratedPopulationComposition} given a {@link Population}.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface GeneratedPopulationCompositionFactory {

    GeneratedPopulationComposition createGeneratedPopulationComposition(Population population);

}
