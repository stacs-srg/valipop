package datastructure.population;

import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.population.exceptions.PersonNotFoundException;
import model.IPerson;
import model.IPartnership;
import org.apache.commons.lang.ObjectUtils;
import utils.MapUtils;
import utils.time.*;
import utils.time.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * The FemaleCollection is a specialised concrete implementation of a PersonCollection. The implementation offers an
 * additional layer of division below the year of birth level which divides females out into seperate collections based
 * on how many children they have had.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection extends PersonCollection {

    private static Logger log = LogManager.getLogger(FemaleCollection.class);
    private Map<YearDate, Map<Integer, Collection<IPerson>>> byBirthYearAndNumberOfChildren = new HashMap<YearDate, Map<Integer, Collection<IPerson>>>();

    /**
     * Instantiates a new FemaleCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the FemaleCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the FemaleCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     * @throws UnsupportedDateConversion the unsupported date conversion
     */
    public FemaleCollection(Date start, Date end) throws UnsupportedDateConversion {
        super(start, end);

        for (DateClock y = start.getDateClock(); DateUtils.dateBefore(y, end); y = y.advanceTime(1, TimeUnit.YEAR)) {
            byBirthYearAndNumberOfChildren.put(y.getYearDate(), new HashMap<Integer, Collection<IPerson>>());
        }
    }

    @Override
    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (YearDate t : byBirthYearAndNumberOfChildren.keySet()) {
            for (Integer i : byBirthYearAndNumberOfChildren.get(t).keySet()) {
                people.addAll(byBirthYearAndNumberOfChildren.get(t).get(i));
            }
        }

        return people;
    }

    @Override
    public Collection<IPerson> getByYear(Date yearOfBirth) {

        Collection<IPerson> people = new ArrayList<IPerson>();

        try {
            for (Integer i : byBirthYearAndNumberOfChildren.get(yearOfBirth.getYearDate()).keySet()) {
                people.addAll(byBirthYearAndNumberOfChildren.get(yearOfBirth.getYearDate()).get(i));
            }
        } catch (NullPointerException e) {
        }

        return people;
    }

    @Override
    public void addPerson(IPerson person) {
        try {
            byBirthYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
        } catch (NullPointerException e) {
            try {
                byBirthYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).put(countChildren(person), new ArrayList<IPerson>());
                byBirthYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
            } catch (NullPointerException e1) {
                Map<Integer, Collection<IPerson>> temp = new HashMap<Integer, Collection<IPerson>>();
                temp.put(countChildren(person), new ArrayList<IPerson>());
                temp.get(countChildren(person)).add(person);
                byBirthYearAndNumberOfChildren.put(person.getBirthDate().getYearDate(), temp);
            }
        }
    }

    @Override
    public void removePerson(IPerson person) throws PersonNotFoundException {
        Collection<IPerson> people = byBirthYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person));

        if (people == null || !people.remove(person)) {
            throw new PersonNotFoundException("Specified person not found in data structure");
        }
    }

    @Override
    public int getNumberOfPersons() {
        // TODO optomise me
        return getAll().size();
    }

    @Override
    public int getNumberOfPersons(Date yearOfBirth) {

        Map<Integer, Collection<IPerson>> temp = byBirthYearAndNumberOfChildren.get(yearOfBirth.getYearDate());

        if(temp == null) {
            return 0;
        } else {
            return MapUtils.countObjectsInCollectionsInMap(temp);
        }
    }

    /**
     * Returns the highest birth order (number of children) among women in the specified year of birth.
     *
     * @param yearOfBirth the year of birth of the mothers in question
     * @return the highest birth order value
     */
    public int getHighestBirthOrder(Date yearOfBirth) {

        Map<Integer, Collection<IPerson>> temp = byBirthYearAndNumberOfChildren.get(yearOfBirth.getYearDate());

        if(temp == null) {
            return 0;
        } else{
            return MapUtils.getMax(temp.keySet());
        }

    }

    /**
     * Gets map by year.
     *
     * @param year the year
     * @return the map by year
     */
    public Map<Integer, Collection<IPerson>> getMapByYear(Date year) {
        Map<Integer, Collection<IPerson>> map = byBirthYearAndNumberOfChildren.get(year.getYearDate());

        if (map == null) {
            Map<Integer, Collection<IPerson>> temp = new HashMap<Integer, Collection<IPerson>>();
            byBirthYearAndNumberOfChildren.put(year.getYearDate(), temp);
            map = byBirthYearAndNumberOfChildren.get(year.getYearDate());
        }
        return map;
    }

    /**
     * Gets by number of children.
     *
     * @param year             the year
     * @param numberOfChildren the number of children
     * @return the by number of children
     */
    public Collection<IPerson> getByNumberOfChildren(Date year, Integer numberOfChildren) {

        try {
            return byBirthYearAndNumberOfChildren.get(year.getYearDate()).get(numberOfChildren);
        } catch(NullPointerException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Remove n persons collection.
     *
     * @param numberToRemove the number to remove
     * @param yearOfBirth    the year of birth
     * @param withNChildren  the with n children
     * @param currentDate    the current date
     * @return the collection
     * @throws InsufficientNumberOfPeopleException the insufficient number of people exception
     */
    public Collection<IPerson> removeNPersons(int numberToRemove, YearDate yearOfBirth, int withNChildren, DateClock currentDate) throws InsufficientNumberOfPeopleException {

        Collection<IPerson> people = new ArrayList<>();
        if (numberToRemove == 0) {
            return people;
        }

        Iterator<IPerson> iterator = byBirthYearAndNumberOfChildren.get(yearOfBirth).get(withNChildren).iterator();

        for (int i = 0; i < numberToRemove; i++) {

            IPerson p;
            try {
                p = iterator.next();
            } catch (NoSuchElementException e) {
                System.out.println("CD " + currentDate.toString() + " |   YB " + yearOfBirth.toString() + " |   ORDER " + withNChildren + " |   " + i + "/" + numberToRemove + " | " + byBirthYearAndNumberOfChildren.get(yearOfBirth).get(withNChildren).size());
                throw new InsufficientNumberOfPeopleException("Not enough females to remove specified number from collection");
            }

            if (p.noRecentChildren(currentDate)) {
                people.add(p);
            } else {
                i--;
            }
        }

        for (IPerson p : people) {
            try {
                removePerson(p);
            } catch (PersonNotFoundException e) {
                throw new ConcurrentModificationException("The People reference list has become out of sync with the " +
                        "relevant Collection in the underlying map");
            }
        }

        return people;

    }

    private Integer countChildren(IPerson person) {

        int count = 0;

        for (IPartnership partnership : person.getPartnerships()) {
            count += partnership.getChildren().size();
        }

        return count;

    }

}