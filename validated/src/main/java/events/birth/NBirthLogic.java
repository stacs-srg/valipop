package events.birth;

import config.Config;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventLogic;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.population.dataStructure.Population;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NBirthLogic implements EventLogic {


    @Override
    public void handleEvent(Config config, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                            Population population, PopulationStatistics desiredPopulationStatistics) {



    }
}
