package datastructure.population;

import model.IPerson;
import model.Person;
import model.IPartnership;
import utils.time.*;
import utils.time.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection extends PersonCollection {

    private static Logger log = LogManager.getLogger(FemaleCollection.class);
    Map<YearDate, Map<Integer, Collection<IPerson>>> byYearAndNumberOfChildren = new HashMap<YearDate, Map<Integer, Collection<IPerson>>>();

    public FemaleCollection(Date start, Date end) throws UnsupportedDateConversion {
        super(start, end);

        for (DateClock y = start.getDateClock(); DateUtils.dateBefore(y, end); y = y.advanceTime(1, TimeUnit.YEAR)) {
            byYearAndNumberOfChildren.put(y.getYearDate(), new HashMap<Integer, Collection<IPerson>>());
        }
    }

    @Override
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
    public Collection<IPerson> getByYear(Date year) {

        Collection<IPerson> people = new ArrayList<IPerson>();

        for (Integer i : byYearAndNumberOfChildren.get(year.getYearDate()).keySet()) {
            people.addAll(byYearAndNumberOfChildren.get(year.getYearDate()).get(i));
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
    public boolean removePerson(IPerson person) throws PersonNotFoundException {
        Collection<IPerson> people = byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person));

        if(people == null || !people.remove(person)) {
            throw new PersonNotFoundException("Specified person not found in datastructure");
        }

        return true;
    }

    @Override
    public int getNumberOfPersons() {
        return getAll().size();
    }

    public Map<Integer, Collection<IPerson>> getMapByYear(Date year) {
        Map<Integer, Collection<IPerson>> map = byYearAndNumberOfChildren.get(year.getYearDate());

        if (map == null) {
            Map<Integer, Collection<IPerson>> temp = new HashMap<Integer, Collection<IPerson>>();
            byYearAndNumberOfChildren.put(year.getYearDate(), temp);
            map = byYearAndNumberOfChildren.get(year.getYearDate());
        }
        return map;
    }

    public Collection<IPerson> getByNumberOfChildren(Date year, Integer numberOfChildren) {

        return byYearAndNumberOfChildren.get(year.getYearDate()).get(numberOfChildren);
    }

    public Collection<IPerson> removeNPersons(int numberToRemove, YearDate yearOfBirth, int withNChildren, DateClock currentDate) throws InsufficientNumberOfPeopleException {

        Collection<IPerson> people = new ArrayList<>();
        if (numberToRemove == 0) {
            return people;
        }

        Iterator<IPerson> iterator = byYearAndNumberOfChildren.get(yearOfBirth).get(withNChildren).iterator();

        for (int i = 0; i < numberToRemove; i++) {

            IPerson p;
            try {
                p = iterator.next();
            } catch (NoSuchElementException e) {
                System.out.println("CD " + currentDate.toString() + " |   YB " + yearOfBirth.toString() + " |   ORDER " + withNChildren + " |   " + i + "/" + numberToRemove + " | " + byYearAndNumberOfChildren.get(yearOfBirth).get(withNChildren).size());
                throw new InsufficientNumberOfPeopleException("Not enough females to remove specified number from collection");
            }


            // TODO NEXT this just broke things
            if(p.noRecentChildren(currentDate)) {
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