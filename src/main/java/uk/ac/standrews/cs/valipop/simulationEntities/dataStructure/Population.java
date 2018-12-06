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
package uk.ac.standrews.cs.valipop.simulationEntities.dataStructure;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.PopulationCounts;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dates.DateUtils;

import java.time.LocalDate;
import java.time.Period;
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

        return makePeopleCollection(livingPeople, deadPeople);
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

    public PeopleCollection getAllPeople(LocalDate first, LocalDate last, Period maxAge) {

        Period tp = Period.ofMonths(Math.abs((int)Period.between(first, last).toTotalMonths()));

        Collection<IPerson> l = livingPeople.getAllPersonsAliveInTimePeriod(first, tp, maxAge);
        Collection<IPerson> d = deadPeople.getAllPersonsAliveInTimePeriod(first, tp, maxAge);

        PeopleCollection pC = new PeopleCollection(first, last, Period.ofYears(1));
        pC.setDescription("combined");

        for(IPerson p : l) {
            pC.addPerson(p);
        }

        for(IPerson p : d) {
            pC.addPerson(p);
        }

        return pC;
    }

    private PeopleCollection makePeopleCollection(PeopleCollection col1, PeopleCollection col2) {

        LocalDate start = DateUtils.earlierOf(col1.getStartDate(), col2.getStartDate());
        LocalDate end = DateUtils.laterOf(col1.getStartDate(), col2.getStartDate());

        PeopleCollection cloneCol1 = col1.clone();
        PeopleCollection cloneCol2 = col2.clone();

        cloneCol1.setStartDate(start);
        cloneCol1.setEndDate(end);

        for (IPerson p : cloneCol2.getPeople()) {
            cloneCol1.addPerson(p);
        }

        for (IPartnership p : cloneCol2.getPartnerships()) {
            cloneCol1.addPartnershipToIndex(p);
        }

        return cloneCol1;
    }
}
