package model.datastructure;

import model.implementation.populationStatistics.IntegerRange;
import model.interfaces.populationModel.IPerson;
import model.time.TimeInstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection implements PersonCollection {

    Map<TimeInstant, Map<IntegerRange, Collection<IPerson>>> byYearAndNumberOfChildren;

    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (TimeInstant t : byYearAndNumberOfChildren.keySet()){
            for (IntegerRange iR : byYearAndNumberOfChildren.get(t).keySet()) {
                people.addAll(byYearAndNumberOfChildren.get(t).get(iR));
            }
        }

        return people;
    }

    public Collection<IPerson> getByYear(TimeInstant year) {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (IntegerRange iR : byYearAndNumberOfChildren.get(year).keySet()) {
            people.addAll(byYearAndNumberOfChildren.get(year).get(iR));
        }

        return people;
    }

    public Collection<IPerson> getByNumberOfChildren(TimeInstant year, IntegerRange numberOfChildren) {

        return byYearAndNumberOfChildren.get(year).get(numberOfChildren);
    }

}