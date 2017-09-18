/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure;

import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.PopulationCounts;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.utils.AggregatePersonCollectionFactory;

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
