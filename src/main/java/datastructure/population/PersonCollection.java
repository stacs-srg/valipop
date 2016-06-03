package datastructure.population;

import datastructure.summativeStatistics.DateBounds;
import model.Person;
import utils.time.Date;
import utils.time.YearDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class PersonCollection implements DateBounds {

    abstract Collection<Person> getAll();

    abstract Collection<Person> getByYear(Date year);

    abstract void addPerson(Person person);

    abstract boolean removePerson(Person person) throws PersonNotFoundException;

    abstract int getNumberOfPersons();

    public Collection<Person> removeNPersons(int numberToRemove, Date yearOfBirth) {
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

    protected Date startDate;
    protected Date endDate;

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }
}
