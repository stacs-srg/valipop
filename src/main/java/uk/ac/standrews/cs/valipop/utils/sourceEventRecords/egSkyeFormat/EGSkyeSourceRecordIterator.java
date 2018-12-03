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
package uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat;

import uk.ac.standrews.cs.utilities.FilteredIterator;
import uk.ac.standrews.cs.utilities.MappedIterator;
import uk.ac.standrews.cs.utilities.Mapper;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPopulation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by graham on 10/07/2014.
 */
public class EGSkyeSourceRecordIterator {

    public static Iterable<EGSkyeBirthSourceRecord> getBirthRecordIterator(final IPopulation population, LocalDate startDate) {

        return () -> {

            Predicate<IPerson> check_in_sim_dates = person -> startDate.isBefore( person.getBirthDate());

            Iterator<IPerson> person_iterator = new FilteredIterator<>(population.getPeople().iterator(), check_in_sim_dates);

            Mapper<IPerson, EGSkyeBirthSourceRecord> person_to_birth_record_mapper = person -> new EGSkyeBirthSourceRecord(person, population);

            return new MappedIterator<>(person_iterator, person_to_birth_record_mapper);
        };
    }

    public static Iterable<EGSkyeDeathSourceRecord> getDeathRecordIterator(final IPopulation population, LocalDate startDate) {

        return () -> {

            Predicate<IPerson> check_dead_and_in_sim_dates = person -> person.getDeathDate() != null && startDate.isBefore( person.getDeathDate());

            Iterator<IPerson> dead_person_iterator = new FilteredIterator<>(population.getPeople().iterator(), check_dead_and_in_sim_dates);

            Mapper<IPerson, EGSkyeDeathSourceRecord> person_to_death_record_mapper = person -> new EGSkyeDeathSourceRecord(person, population);

            return new MappedIterator<>(dead_person_iterator, person_to_death_record_mapper);
        };
    }

    public static Iterable<EGSkyeMarriageSourceRecord> getMarriageRecordIterator(final IPopulation population, LocalDate startDate) {

        return () -> {

            Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

            List<IPartnership> l = new ArrayList<>();

            while(partnership_iterator.hasNext()) {
                IPartnership p = partnership_iterator.next();
                if(p.getMarriageDate() != null && startDate.isBefore( p.getMarriageDate()))
                    l.add(p);
            }

            Mapper<IPartnership, EGSkyeMarriageSourceRecord> person_to_marriage_record_mapper = partnership -> new EGSkyeMarriageSourceRecord(partnership, population);

            return new MappedIterator<>(l.iterator(), person_to_marriage_record_mapper);
        };
    }
}
