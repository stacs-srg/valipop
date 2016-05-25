package datastructure.population;

import model.Person;
import utils.time.Date;
import utils.time.YearDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class PersonCollection {

    abstract Collection<Person> getAll();

    abstract Collection<Person> getByYear(Date year);

    abstract void addPerson(Person person);

    abstract boolean removePerson(Person person) throws PersonNotFoundException;

    abstract int getNumberOfPersons();

    public Collection<Person> removeNPersons(int numberToRemove, YearDate yearOfBirth) {
        Collection<Person> people = new ArrayList<>(numberToRemove);
        Iterator<Person> iterator = getByYear(yearOfBirth).iterator();

        for (int i = 0; i < numberToRemove; i++) {
            Person p = iterator.next();
            people.add(p);
        }

        for (Person p : people) {
            try {
                removePerson(p);
            } catch (PersonNotFoundException e) {
                e.printStackTrace();
            }
        }

        return people;
    }

}
