package datastructure.population;

import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.population.exceptions.PersonNotFoundException;
import utils.time.DateBounds;
import model.IPerson;
import utils.time.Date;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A PersonCollection contains a set of collections of people where the collections are organised by the year of birth
 * of the person.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class PersonCollection implements DateBounds {

    private Date startDate;
    private Date endDate;

    /**
     * Instantiates a new PersonCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the PersonCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the PersonCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param startDate the start date
     * @param endDate   the end date
     */
    public PersonCollection(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Gets all the people that exist in the underlying sub-structure of this PersonCollection. Likely to be expensive,
     * if needing a count of people in collection then check to see if the instance has an index and take a count using
     * the size of the index.
     *
     * @return All people in the PersonCollection
     */
    abstract Collection<IPerson> getAll();

    /**
     * Gets all the people in the PersonCollection who were born in the given year.
     *
     * @param yearOfBirth the year of birth of the desired cohort
     * @return the desired cohort
     */
    abstract Collection<IPerson> getByYear(Date yearOfBirth);

    /**
     * Adds the given person to the PersonCollection.
     *
     * @param person the person to be added
     */
    abstract void addPerson(IPerson person);

    /**
     * Removes the specified person from this PersonCollection.
     *
     * @param person the person to be removed
     * @throws PersonNotFoundException If the specified person is not found then an exception is thrown
     */
    abstract void removePerson(IPerson person) throws PersonNotFoundException;

    /**
     * Counts and returns the number of people in the PersonCollection. This may be very expensive as it involves
     * combining the counts of many under-lying Collection objects. If the instance contains an index it will likely be
     * more efficient to take the size of the index as the count.
     *
     * @return the number of persons in the PersonCollection
     */
    abstract int getNumberOfPersons();

    /**
     * Counts and returns the number of people born in the given year in the PersonCollection. This may be very
     * expensive as it may involve combining the counts of many under-lying Collection objects. If the instance contains
     * an index it will likely be more efficient to take the size of the index as the count.
     *
     * @return the number of persons in the PersonCollection
     */
    abstract int getNumberOfPersons(Date yearOfBirth);

    /**
     * Removes n people with the specified year of birth from the PersonCollection. If there are not enough people then
     * an exception is thrown.
     *
     * @param numberToRemove the number of people to remove
     * @param yearOfBirth    the year of birth of those to remove
     * @return the random Collection of people who have been removed
     * @throws InsufficientNumberOfPeopleException If there are less people alive for the given year of birth than
     */
    public Collection<IPerson> removeNPersons(int numberToRemove, Date yearOfBirth) throws InsufficientNumberOfPeopleException {
        Collection<IPerson> people = new ArrayList<>(numberToRemove);
        Iterator<IPerson> iterator = getByYear(yearOfBirth).iterator();

        for (int i = 0; i < numberToRemove; i++) {
            try {
                IPerson p = iterator.next();
                people.add(p);
            } catch(NoSuchElementException e) {
                throw new InsufficientNumberOfPeopleException("Not enough people in collection to remove desired number");
            }
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
