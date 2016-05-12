package datastructure;

import model.Person;
import model.interfaces.populationModel.IPerson;
import model.time.Date;
import model.time.YearDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection implements PersonCollection {

    private Map<YearDate, Collection<Person>> byYear = new HashMap<YearDate, Collection<Person>>();

    public Collection<Person> getAll() {

        Collection<Person> people = new ArrayList<Person>();

        for (YearDate t : byYear.keySet()) {
            people.addAll(byYear.get(t));
        }

        return people;
    }

    public Collection<Person> getByYear(Date year) {

        return byYear.get(year.getYearDate());
    }

    public void addPerson(Person person) {
        try {
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        } catch (NullPointerException e) {
            byYear.put(person.getBirthDate().getYearDate(), new ArrayList<Person>());
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        }

    }

    @Override
    public boolean removePerson(Person person) {
        Collection<Person> people = byYear.get(person.getBirthDate().getYearDate());
        return people.remove(person);
    }

    @Override
    public void updatePerson(Person person, int numberOfChildrenInMostRecentMaternity) {
        return;
    }
}
