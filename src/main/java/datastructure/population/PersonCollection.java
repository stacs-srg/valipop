package datastructure.population;

import datastructure.summativeStatistics.DateBounds;
import model.IPerson;
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

    protected Date startDate;
    protected Date endDate;

    public PersonCollection(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    abstract Collection<IPerson> getAll();

    abstract Collection<IPerson> getByYear(Date year);

    abstract void addPerson(IPerson person);

    abstract boolean removePerson(IPerson person) throws PersonNotFoundException;

    abstract int getNumberOfPersons();

    public Collection<IPerson> removeNPersons(int numberToRemove, Date yearOfBirth) {
        Collection<IPerson> people = new ArrayList<>(numberToRemove);
        Iterator<IPerson> iterator = getByYear(yearOfBirth).iterator();

        for (int i = 0; i < numberToRemove; i++) {
            IPerson p = iterator.next();
            people.add(p);
        }

        for (IPerson p : people) {
            try {
                removePerson(p);
            } catch (PersonNotFoundException e) {
                e.printStackTrace();
            }
        }

        return people;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }
}
