package datastructure.population;

import model.Person;
import utils.time.*;
import utils.time.Date;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection extends PersonCollection {

    private Map<YearDate, Collection<Person>> byYear = new HashMap<YearDate, Collection<Person>>();

    public MaleCollection(DateClock start, DateClock end) {
        for (DateClock y = start; DateUtils.dateBefore(y, end); y = y.advanceTime(1, TimeUnit.YEAR)) {
            byYear.put(y.getYearDate(), new ArrayList<Person>());
        }
    }

    @Override
    public Collection<Person> getAll() {

        Collection<Person> people = new ArrayList<Person>();

        for (YearDate t : byYear.keySet()) {
            people.addAll(byYear.get(t));
        }

        return people;
    }

    @Override
    public Collection<Person> getByYear(Date year) {

        Collection<Person> c = byYear.get(year.getYearDate());

        if (c == null) {
            Collection<Person> temp = new ArrayList<Person>();
            byYear.put(year.getYearDate(), temp);
            c = byYear.get(year.getYearDate());
        }

        return c;
    }

    @Override
    public void addPerson(Person person) {
        try {
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        } catch (NullPointerException e) {
            byYear.put(person.getBirthDate().getYearDate(), new ArrayList<Person>());
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        }

    }

    @Override
    public boolean removePerson(Person person) throws PersonNotFoundException {
        Collection<Person> people = byYear.get(person.getBirthDate().getYearDate());

        if(people == null || !people.remove(person)) {
            throw new PersonNotFoundException("Specified person not found in datastructure");
        }

        return true;
    }

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }

}
