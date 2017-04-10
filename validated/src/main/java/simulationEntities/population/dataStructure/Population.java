package simulationEntities.population.dataStructure;

import config.Config;
import simulationEntities.population.PopulationCounts;
import simulationEntities.population.dataStructure.utils.AggregatePersonCollectionFactory;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Population {

    private PeopleCollection livingPeople;
    private PeopleCollection deadPeople;

    private PopulationCounts populationCounts;

    public Population(Config config) {
        livingPeople = new PeopleCollection(config.getTS(), config.getTE(), config.getSimulationTimeStep());
        deadPeople = new PeopleCollection(config.getTS(), config.getTE(), config.getSimulationTimeStep());
        populationCounts = new PopulationCounts();
    }

    public PeopleCollection getAllPeople() {
        return AggregatePersonCollectionFactory.makePeopleCollection(livingPeople, deadPeople);
    }

    public PeopleCollection getLivingPeople() {
        return livingPeople;
    }

    public PeopleCollection getDeadPeople() {
        return deadPeople;
    }

    public PopulationCounts getPopulationCounts() {
        return populationCounts;
    }

}
