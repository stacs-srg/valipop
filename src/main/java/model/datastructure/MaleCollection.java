package model.datastructure;

import model.interfaces.populationModel.IPerson;
import model.time.TimeInstant;
import scale_testing.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MaleCollection implements PersonCollection {

    Map<TimeInstant, Collection<IPerson>> byYear = new HashMap<TimeInstant, Collection<IPerson>>();

    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (TimeInstant t : byYear.keySet()){
            people.addAll(byYear.get(t));
        }

        return people;
    }

    public Collection<IPerson> getByYear(TimeInstant year) {

        return byYear.get(year);
    }

}
