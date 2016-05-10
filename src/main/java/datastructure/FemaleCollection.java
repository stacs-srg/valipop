package datastructure;

import model.implementation.populationStatistics.IntegerRange;
import model.interfaces.populationModel.IPartnership;
import model.interfaces.populationModel.IPerson;
import model.time.Date;
import model.time.DateClock;
import model.time.YearDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection implements PersonCollection {

    private static Logger log = LogManager.getLogger(FemaleCollection.class);
    Map<YearDate, Map<Integer, Collection<IPerson>>> byYearAndNumberOfChildren = new HashMap<YearDate, Map<Integer, Collection<IPerson>>>();

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
        try {
            byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
        } catch (NullPointerException e) {
            try {
                byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).put(countChildren(person), new ArrayList<IPerson>());
                byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
            } catch (NullPointerException e1) {
                Map<Integer, Collection<IPerson>> temp = new HashMap<Integer, Collection<IPerson>>();
                temp.put(countChildren(person), new ArrayList<IPerson>());
                temp.get(countChildren(person)).add(person);
                byYearAndNumberOfChildren.put(person.getBirthDate().getYearDate(), temp);
            }
        }
    }

    @Override
    public boolean removePerson(IPerson person) {
        Collection<IPerson> people = byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person));
        return people.remove(person);
    }

    public Collection<IPerson> getByYear(Date year) {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (Integer i : byYearAndNumberOfChildren.get(year.getYearDate()).keySet()) {
            people.addAll(byYearAndNumberOfChildren.get(year.getYearDate()).get(i));
        }

        return people;
    }

    public Map<Integer, Collection<IPerson>> getMapByYear(Date year) {
        return byYearAndNumberOfChildren.get(year.getYearDate());
    }

    public Collection<IPerson> getByNumberOfChildren(Date year, Integer numberOfChildren) {

        return byYearAndNumberOfChildren.get(year.getYearDate()).get(numberOfChildren);
    }

    public void updatePerson(IPerson person, int numberOfChildrenInMostRecentMaternity) {

        int previousNumberOfChildren = countChildren(person) - numberOfChildrenInMostRecentMaternity;

        Collection<IPerson> people = byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(previousNumberOfChildren);

        boolean found = false;

        for (IPerson p : people) {
            if (person.compareTo(p) == 0) {
                people.remove(person);
                addPerson(person);
                found = true;
                break;
            }
        }

        if(!found) {
            log.fatal("Failed to find female to be moved");
            System.exit(302);
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