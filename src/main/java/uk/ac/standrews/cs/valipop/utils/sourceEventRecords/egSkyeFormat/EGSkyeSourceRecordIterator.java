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

import uk.ac.standrews.cs.basic_model.model.IPartnership;
import uk.ac.standrews.cs.basic_model.model.IPerson;
import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.utilities.FilteredIterator;
import uk.ac.standrews.cs.utilities.MappedIterator;
import uk.ac.standrews.cs.utilities.Mapper;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.population.IPopulationExtended;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.BirthSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.DeathSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.MarriageSourceRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by graham on 10/07/2014.
 */
public class EGSkyeSourceRecordIterator {

    public static Iterable<EGSkyeBirthSourceRecord> getBirthRecordIterator(final IPopulationExtended population) {

        return () -> {

            Iterator<IPersonExtended> person_iterator = population.getPeople_ex().iterator();

            Mapper<IPersonExtended, EGSkyeBirthSourceRecord> person_to_birth_record_mapper = person -> new EGSkyeBirthSourceRecord(person, population);

            return new MappedIterator<>(person_iterator, person_to_birth_record_mapper);
        };
    }

    public static Iterable<EGSkyeDeathSourceRecord> getDeathRecordIterator(final IPopulationExtended population) {

        return () -> {

            Predicate<IPersonExtended> check_dead = person -> person.getDeathDate() != null;

            Iterator<IPersonExtended> dead_person_iterator = new FilteredIterator<>(population.getPeople_ex().iterator(), check_dead);

            Mapper<IPersonExtended, EGSkyeDeathSourceRecord> person_to_death_record_mapper = person -> new EGSkyeDeathSourceRecord(person, population);

            return new MappedIterator<>(dead_person_iterator, person_to_death_record_mapper);
        };
    }

    public static Iterable<EGSkyeMarriageSourceRecord> getMarriageRecordIterator(final IPopulationExtended population) {

        return () -> {

            Iterator<IPartnershipExtended> partnership_iterator = population.getPartnerships_ex().iterator();

            List<IPartnershipExtended> l = new ArrayList<>();

            while(partnership_iterator.hasNext()) {
                IPartnershipExtended p = partnership_iterator.next();
                if(p.getMarriageDate() != null)
                    l.add(p);
            }


            Mapper<IPartnershipExtended, EGSkyeMarriageSourceRecord> person_to_marriage_record_mapper = new Mapper<IPartnershipExtended, EGSkyeMarriageSourceRecord>() {
                @Override
                public EGSkyeMarriageSourceRecord map(IPartnershipExtended partnership) {
                    return new EGSkyeMarriageSourceRecord(partnership, population);
                }
            };

            return new MappedIterator<>(l.iterator(), person_to_marriage_record_mapper);
        };
    }
}
