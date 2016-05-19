package datastructure;

import model.Person;
import model.interfaces.populationModel.IPerson;
import model.time.Date;
import model.time.YearDate;

import java.util.*;

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

        Collection<Person> c = byYear.get(year.getYearDate());

        if(c == null) {
            Collection<Person> temp = new ArrayList<Person>();
            byYear.put(year.getYearDate(), temp);
            c = byYear.get(year.getYearDate());
        }

        return c;
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

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }


    public Collection<Person> removeRandomPersons(int numberToRemove, YearDate yearOfBirth) {
        Collection<Person> people = new ArrayList<>(numberToRemove);
        Iterator<Person> iterator = getByYear(yearOfBirth).iterator();

        for(int i = 0; i < numberToRemove; i++) {
            Person p = iterator.next();
            people.add(p);
            removePerson(p);
        }

        return people;
    }
}
