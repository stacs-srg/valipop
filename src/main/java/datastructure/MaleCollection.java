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
        byYear.get(person.getBirthDate().getYearDate()).add(person);
    }

}
