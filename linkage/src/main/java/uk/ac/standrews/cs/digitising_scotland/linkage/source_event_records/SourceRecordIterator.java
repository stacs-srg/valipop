package uk.ac.standrews.cs.digitising_scotland.linkage.source_event_records;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.FilteredIterator;
import uk.ac.standrews.cs.digitising_scotland.util.Map2;
import uk.ac.standrews.cs.digitising_scotland.util.MappedIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by graham on 10/07/2014.
 */
public class SourceRecordIterator {

    public static Iterable<BirthSourceRecord> getBirthRecordIterator(final IPopulation population) {

        return () -> {

            Iterator<IPerson> person_iterator = population.getPeople().iterator();

            Map2<IPerson, BirthSourceRecord> person_to_birth_record_mapper = person -> new BirthSourceRecord(person, population);

            return new MappedIterator<>(person_iterator, person_to_birth_record_mapper);
        };
    }

    public static Iterable<DeathSourceRecord> getDeathRecordIterator(final IPopulation population) {

        return () -> {

            Predicate<IPerson> check_dead = person -> person.getDeathDate() != null;

            Iterator<IPerson> dead_person_iterator = new FilteredIterator<>(population.getPeople().iterator(), check_dead);

            Map2<IPerson, DeathSourceRecord> person_to_death_record_mapper = person -> new DeathSourceRecord(person, population);

            return new MappedIterator<>(dead_person_iterator, person_to_death_record_mapper);
        };
    }

    public static Iterable<MarriageSourceRecord> getMarriageRecordIterator(final IPopulation population) {

        return () -> {

            Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

            List<IPartnership> l = new ArrayList<>();

            while(partnership_iterator.hasNext()) {
                IPartnership p = partnership_iterator.next();
                if(p.getMarriageDate() != null)
                    l.add(p);
            }


            Map2<IPartnership, MarriageSourceRecord> person_to_marriage_record_mapper = new Map2<IPartnership, MarriageSourceRecord>() {
                @Override
                public MarriageSourceRecord map(IPartnership partnership) {
                    return new MarriageSourceRecord(partnership, population);
                }
            };

            return new MappedIterator<>(l.iterator(), person_to_marriage_record_mapper);
        };
    }
}
