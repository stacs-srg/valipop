package datastructure.population;

import datastructure.population.exceptions.PersonNotFoundException;
import model.IPerson;
import utils.time.*;
import utils.time.Date;

import java.util.*;

/**
 * The class MaleCollection is a concrete instantiation of the PersonCollection class.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection extends PersonCollection {

    private Map<YearDate, Collection<IPerson>> byYear = new HashMap<YearDate, Collection<IPerson>>();

    /**
     * Instantiates a new MaleCollection.
     *
     * @param start the start
     * @param end   the end
     * @throws UnsupportedDateConversion the unsupported date conversion
     */
    public MaleCollection(Date start, Date end) throws UnsupportedDateConversion {
        super(start, end);

        for (DateClock y = start.getDateClock(); DateUtils.dateBefore(y, end); y = y.advanceTime(1, TimeUnit.YEAR)) {
            byYear.put(y.getYearDate(), new ArrayList<IPerson>());
        }
    }

    @Override
    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (YearDate t : byYear.keySet()) {
            people.addAll(byYear.get(t));
        }

        return people;
    }

    @Override
    public Collection<IPerson> getByYear(Date yearOfBirth) {

        Collection<IPerson> c = byYear.get(yearOfBirth.getYearDate());

        if (c == null) {
            Collection<IPerson> temp = new ArrayList<IPerson>();
            byYear.put(yearOfBirth.getYearDate(), temp);
            c = byYear.get(yearOfBirth.getYearDate());
        }

        return c;
    }

    @Override
    public void addPerson(IPerson person) {
        try {
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        } catch (NullPointerException e) {
            byYear.put(person.getBirthDate().getYearDate(), new ArrayList<IPerson>());
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        }

    }

    @Override
    public void removePerson(IPerson person) throws PersonNotFoundException {
        Collection<IPerson> people = byYear.get(person.getBirthDate().getYearDate());

        if (people == null || !people.remove(person)) {
            throw new PersonNotFoundException("Specified person not found in datastructure");
        }
    }

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }

    @Override
    int getNumberOfPersons(Date yearOfBirth) {
        return byYear.get(yearOfBirth.getYearDate()).size();
    }

}
