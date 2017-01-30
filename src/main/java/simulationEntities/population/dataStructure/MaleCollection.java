package simulationEntities.population.dataStructure;


import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.DateClock;
import dateModel.dateImplementations.YearDate;
import dateModel.exceptions.UnsupportedDateConversion;
import dateModel.timeSteps.TimeUnit;
import simulationEntities.IPerson;
import simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The class MaleCollection is a concrete instance of the PersonCollection class.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection extends PersonCollection {

    private final Map<YearDate, Collection<IPerson>> byYear = new HashMap<>();

    /**
     * Instantiates a new MaleCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the MaleCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the MaleCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     * @throws UnsupportedDateConversion the unsupported date conversion
     */
    public MaleCollection(Date start, Date end) throws UnsupportedDateConversion {
        super(start, end);

        for (DateClock y = start.getDateClock(); DateUtils.dateBefore(y, end); y = y.advanceTime(1, TimeUnit.YEAR)) {
            byYear.put(y.getYearDate(), new ArrayList<>());
        }
    }

    /*
    -------------------- PersonCollection abstract methods --------------------
     */

    @Override
    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<>();

        for (YearDate t : byYear.keySet()) {
            people.addAll(byYear.get(t));
        }

        return people;
    }

    @Override
    public Collection<IPerson> getByYear(Date yearOfBirth) {

        Collection<IPerson> people = new ArrayList<>();

        try {
            people.addAll(byYear.get(yearOfBirth.getYearDate()));
        } catch (NullPointerException e) {
            // No need to do anything - we allow the method to return an empty list as no one was born in the year
        }

        return people;
    }

    @Override
    public void addPerson(IPerson person) {
        try {
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        } catch (NullPointerException e) {
            // If the year didn't exist in the map
            byYear.put(person.getBirthDate().getYearDate(), new ArrayList<>());
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        }

    }

    @Override
    public void removePerson(IPerson person) throws PersonNotFoundException {
        Collection<IPerson> people = byYear.get(person.getBirthDate().getYearDate());

        // Removal of person AND test for removal (all in second clause of the if statement)
        if (people == null || !people.remove(person)) {
            throw new PersonNotFoundException("Specified person not found in data structure");
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
