package datastructure;

import model.interfaces.populationModel.IPerson;
import model.time.Date;
import model.time.DateClock;
import model.time.YearDate;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection implements PersonCollection {

    private Map<YearDate, Collection<IPerson>> byYear = new HashMap<YearDate, Collection<IPerson>>();

    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (YearDate t : byYear.keySet()) {
            people.addAll(byYear.get(t));
        }

        return people;
    }

    public Collection<IPerson> getByYear(Date year) {

        return byYear.get(year.getYearDate());
    }

    public void addPerson(IPerson person) {
        try {
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        } catch (NullPointerException e) {
            byYear.put(person.getBirthDate().getYearDate(), new ArrayList<IPerson>());
            byYear.get(person.getBirthDate().getYearDate()).add(person);
        }

    }

    @Override
    public boolean removePerson(IPerson person) {
        Collection<IPerson> people = byYear.get(person.getBirthDate().getYearDate());
        return people.remove(person);
    }

    @Override
    public void updatePerson(IPerson person, int numberOfChildrenInMostRecentMaternity) {
        return;
    }
}
