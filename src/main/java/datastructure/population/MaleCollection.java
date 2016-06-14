package datastructure.population;

import model.IPerson;
import model.Person;
import utils.time.*;
import utils.time.Date;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection extends PersonCollection {

    private Map<YearDate, Collection<IPerson>> byYear = new HashMap<YearDate, Collection<IPerson>>();

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
    public Collection<IPerson> getByYear(Date year) {

        Collection<IPerson> c = byYear.get(year.getYearDate());

        if (c == null) {
            Collection<IPerson> temp = new ArrayList<IPerson>();
            byYear.put(year.getYearDate(), temp);
            c = byYear.get(year.getYearDate());
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
    public boolean removePerson(IPerson person) throws PersonNotFoundException {
        Collection<IPerson> people = byYear.get(person.getBirthDate().getYearDate());

        if (people == null || !people.remove(person)) {
            throw new PersonNotFoundException("Specified person not found in datastructure");
        }

        return true;
    }

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }

}
