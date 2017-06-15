package uk.ac.standrews.cs.digitising_scotland.verisim.events;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface EventLogic {

    void handleEvent(Config config,
                            AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                            Population population, PopulationStatistics desiredPopulationStatistics) throws InsufficientNumberOfPeopleException;

}
