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
package uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.person.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationCounts;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.utils.AggregatePersonCollectionFactory;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvanceableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Population {

    private PeopleCollection livingPeople;
    private PeopleCollection deadPeople;

    private PopulationCounts populationCounts;

    public Population(Config config) {

        Person.resetIds();
        Partnership.resetIds();

        livingPeople = new PeopleCollection(config.getTS(), config.getTE(), config.getSimulationTimeStep());
        livingPeople.setDescription("living");

        deadPeople = new PeopleCollection(config.getTS(), config.getTE(), config.getSimulationTimeStep());
        deadPeople.setDescription(("dead"));

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

    public PeopleCollection getAllPeople(AdvanceableDate first, ValipopDate last, CompoundTimeUnit maxAge) {

        CompoundTimeUnit tp = DateUtils.differenceInMonths(first, last);

        Collection<IPerson> l = livingPeople.getAllPersonsAliveInTimePeriod(first, tp, maxAge);
        Collection<IPerson> d = deadPeople.getAllPersonsAliveInTimePeriod(first, tp, maxAge);

        PeopleCollection pC = new PeopleCollection(first, last, new CompoundTimeUnit(1, TimeUnit.YEAR));
        pC.setDescription("combined");

        for(IPerson p : l) {
            pC.addPerson(p);
        }

        for(IPerson p : d) {
            pC.addPerson(p);
        }

        return pC;
    }
}
