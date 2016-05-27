package datastructure.population;

import model.Person;
import model.IPartnership;
import utils.time.*;
import utils.time.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection extends PersonCollection {

    private static Logger log = LogManager.getLogger(FemaleCollection.class);
    Map<YearDate, Map<Integer, Collection<Person>>> byYearAndNumberOfChildren = new HashMap<YearDate, Map<Integer, Collection<model.Person>>>();

    public FemaleCollection(DateClock start, DateClock end) {
        for (DateClock y = start; DateUtils.dateBefore(y, end); y = y.advanceTime(1, TimeUnit.YEAR)) {
            byYearAndNumberOfChildren.put(y.getYearDate(), new HashMap<Integer, Collection<Person>>());
        }
    }

    @Override
    public Collection<Person> getAll() {

        Collection<Person> people = new ArrayList<Person>();

        for (YearDate t : byYearAndNumberOfChildren.keySet()) {
            for (Integer i : byYearAndNumberOfChildren.get(t).keySet()) {
                people.addAll(byYearAndNumberOfChildren.get(t).get(i));
            }
        }

        return people;
    }

    @Override
    public Collection<Person> getByYear(Date year) {

        Collection<Person> people = new ArrayList<Person>();

        for (Integer i : byYearAndNumberOfChildren.get(year.getYearDate()).keySet()) {
            people.addAll(byYearAndNumberOfChildren.get(year.getYearDate()).get(i));
        }

        return people;
    }

    @Override
    public void addPerson(Person person) {
        try {
            byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
        } catch (NullPointerException e) {
            try {
                byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).put(countChildren(person), new ArrayList<Person>());
                byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
            } catch (NullPointerException e1) {
                Map<Integer, Collection<model.Person>> temp = new HashMap<Integer, Collection<Person>>();
                temp.put(countChildren(person), new ArrayList<Person>());
                temp.get(countChildren(person)).add(person);
                byYearAndNumberOfChildren.put(person.getBirthDate().getYearDate(), temp);
            }
        }
    }

    @Override
    public boolean removePerson(Person person) throws PersonNotFoundException {
        Collection<Person> people = byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person));

        if(people == null || !people.remove(person)) {
            throw new PersonNotFoundException("Specified person not found in datastructure");
        }

        return true;
    }

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }

    public Map<Integer, Collection<Person>> getMapByYear(Date year) {
        Map<Integer, Collection<Person>> map = byYearAndNumberOfChildren.get(year.getYearDate());

        if (map == null) {
            Map<Integer, Collection<Person>> temp = new HashMap<Integer, Collection<Person>>();
            byYearAndNumberOfChildren.put(year.getYearDate(), temp);
            map = byYearAndNumberOfChildren.get(year.getYearDate());
        }
        return map;
    }

    public Collection<Person> getByNumberOfChildren(Date year, Integer numberOfChildren) {

        return byYearAndNumberOfChildren.get(year.getYearDate()).get(numberOfChildren);
    }

    public Collection<Person> removeNPersons(int numberToRemove, YearDate yearOfBirth, int withNChildren, DateClock currentDate) {

        Collection<Person> people = new ArrayList<>();
        if (numberToRemove == 0) {
            return people;
        }

        Iterator<Person> iterator = byYearAndNumberOfChildren.get(yearOfBirth).get(withNChildren).iterator();

        for (int i = 0; i < numberToRemove; i++) {
            Person p = iterator.next();


            // TODO NEXT this just broke things
            if(p.noRecentChildren(currentDate)) {
                people.add(p);
            }
            i--;
        }

        for (Person p : people) {
            try {
                removePerson(p);
            } catch (PersonNotFoundException e) {
                throw new ConcurrentModificationException("The People reference list has become out of sync with the " +
                        "relevant Collection in the underlying map");
            }
        }

        return people;

    }

    private Integer countChildren(Person person) {

        int count = 0;

        for (IPartnership partnership : person.getPartnerships()) {
            count += partnership.getChildren().size();
        }

        return count;

    }

}