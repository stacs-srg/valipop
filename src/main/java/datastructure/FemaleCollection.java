package datastructure;

import model.implementation.populationStatistics.IntegerRange;
import model.interfaces.populationModel.IPartnership;
import model.interfaces.populationModel.IPerson;
import model.time.Date;
import model.time.DateClock;
import model.time.YearDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection implements PersonCollection {

    private static Logger log = LogManager.getLogger(FemaleCollection.class);
    Map<YearDate, Map<Integer, Collection<IPerson>>> byYearAndNumberOfChildren;

    public Collection<IPerson> getAll() {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (YearDate t : byYearAndNumberOfChildren.keySet()) {
            for (Integer i : byYearAndNumberOfChildren.get(t).keySet()) {
                people.addAll(byYearAndNumberOfChildren.get(t).get(i));
            }
        }

        return people;
    }

    @Override
    public void addPerson(IPerson person) {
        byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
    }

    public Collection<IPerson> getByYear(Date year) {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (Integer i : byYearAndNumberOfChildren.get(year.getYearDate()).keySet()) {
            people.addAll(byYearAndNumberOfChildren.get(year.getYearDate()).get(i));
        }

        return people;
    }

    public Collection<IPerson> getByNumberOfChildren(Date year, Integer numberOfChildren) {

        return byYearAndNumberOfChildren.get(year.getYearDate()).get(numberOfChildren);
    }

    public void moveFemale(IPerson person, Integer previousNumberOfChildren) {

        Collection<IPerson> people = byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(previousNumberOfChildren);

        boolean found = false;

        for (IPerson p : people) {
            if (person.compareTo(p) == 0) {
                people.remove(person);
                byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
                found = true;
                break;
            }
        }

        if(!found) {
            log.warn("Failed to find female to be moved");
        }


    }

    private Integer countChildren(IPerson person) {

        int count = 0;

        for (IPartnership partnership : person.getPartnerships()) {
            count += partnership.getChildren().size();
        }

        return count;

    }

}