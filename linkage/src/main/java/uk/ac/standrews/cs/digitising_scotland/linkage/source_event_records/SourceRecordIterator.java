package uk.ac.standrews.cs.digitising_scotland.linkage.source_event_records;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.Condition;
import uk.ac.standrews.cs.digitising_scotland.util.FilteredIterator;
import uk.ac.standrews.cs.digitising_scotland.util.Map;
import uk.ac.standrews.cs.digitising_scotland.util.MappedIterator;

import java.util.Iterator;

/**
 * Created by graham on 10/07/2014.
 */
public class SourceRecordIterator {

    public static Iterable<BirthSourceRecord> getBirthRecordIterator(final IPopulation population) {

        return new Iterable<BirthSourceRecord>() {

            @Override
            public Iterator<BirthSourceRecord> iterator() {

                Iterator<IPerson> person_iterator = population.getPeople().iterator();

                Map<IPerson, BirthSourceRecord> person_to_birth_record_mapper = new Map<IPerson, BirthSourceRecord>() {
                    @Override
                    public BirthSourceRecord map(IPerson person) {
                        return new BirthSourceRecord(person, population);
                    }
                };

                return new MappedIterator<>(person_iterator, person_to_birth_record_mapper);
            }
        };
    }

    public static Iterable<DeathSourceRecord> getDeathRecordIterator(final IPopulation population) {

        return new Iterable<DeathSourceRecord>() {

            @Override
            public Iterator<DeathSourceRecord> iterator() {

                Condition<IPerson> check_dead = new Condition<IPerson>() {
                    @Override
                    public boolean test(final IPerson person) {
                        return person.getDeathDate() != null;
                    }
                };

                Iterator<IPerson> dead_person_iterator = new FilteredIterator<>(population.getPeople().iterator(), check_dead);

                Map<IPerson, DeathSourceRecord> person_to_death_record_mapper = new Map<IPerson, DeathSourceRecord>() {
                    @Override
                    public DeathSourceRecord map(IPerson person) {
                        return new DeathSourceRecord(person, population);
                    }
                };

                return new MappedIterator<>(dead_person_iterator, person_to_death_record_mapper);
            }
        };
    }

    public static Iterable<MarriageSourceRecord> getMarriageRecordIterator(final IPopulation population) {

        return new Iterable<MarriageSourceRecord>() {

            @Override
            public Iterator<MarriageSourceRecord> iterator() {

                Iterator<IPartnership> partnership_iterator = population.getPartnerships().iterator();

                Map<IPartnership, MarriageSourceRecord> person_to_marriage_record_mapper = new Map<IPartnership, MarriageSourceRecord>() {
                    @Override
                    public MarriageSourceRecord map(IPartnership partnership) {
                        return new MarriageSourceRecord(partnership, population);
                    }
                };

                return new MappedIterator<>(partnership_iterator, person_to_marriage_record_mapper);
            }
        };
    }
}
