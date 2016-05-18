package datastructure;

import model.Person;
import model.interfaces.populationModel.IPartnership;
import model.interfaces.populationModel.IPerson;
import model.time.Date;
import model.time.YearDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FemaleCollection implements PersonCollection {

    private static Logger log = LogManager.getLogger(FemaleCollection.class);
    Map<YearDate, Map<Integer, Collection<model.Person>>> byYearAndNumberOfChildren = new HashMap<YearDate, Map<Integer, Collection<model.Person>>>();

    public Collection<Person> getAll() {

        Collection<Person> people = new ArrayList<Person>();

        for (YearDate t : byYearAndNumberOfChildren.keySet()) {
            for (Integer i : byYearAndNumberOfChildren.get(t).keySet()) {
                people.addAll(byYearAndNumberOfChildren.get(t).get(i));
            }
        }

        return people;
    }

    @Override
    public void addPerson(Person person) {
        try {
            byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
        } catch (NullPointerException e) {
            try {
                byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).put(countChildren(person), new ArrayList<model.Person>());
                byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person)).add(person);
            } catch (NullPointerException e1) {
                Map<Integer, Collection<model.Person>> temp = new HashMap<Integer, Collection<model.Person>>();
                temp.put(countChildren(person), new ArrayList<model.Person>());
                temp.get(countChildren(person)).add(person);
                byYearAndNumberOfChildren.put(person.getBirthDate().getYearDate(), temp);
            }
        }
    }

    @Override
    public boolean removePerson(Person person) {
        Collection<Person> people = byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(countChildren(person));
        return people.remove(person);
    }

    public Person removeRandomPerson(YearDate yearOfBirth, int withNChildren) {

        Person p = byYearAndNumberOfChildren.get(yearOfBirth).get(withNChildren).iterator().next();
        removePerson(p);

        return p;

    }

    public Collection<Person> removeRandomPersons(int numberToRemove, YearDate yearOfBirth, int withNChildren) {

        Collection<Person> people = new ArrayList<>(numberToRemove);
        Iterator<Person> iterator = byYearAndNumberOfChildren.get(yearOfBirth).get(withNChildren).iterator();

        for(int i = 0; i < numberToRemove; i++) {
            Person p = iterator.next();
            people.add(p);
            removePerson(p);
        }

        return people;

    }

    public Collection<Person> getByYear(Date year) {

        Collection<Person> people = new ArrayList<Person>();

        for (Integer i : byYearAndNumberOfChildren.get(year.getYearDate()).keySet()) {
            people.addAll(byYearAndNumberOfChildren.get(year.getYearDate()).get(i));
        }

        return people;
    }

    public Map<Integer, Collection<Person>> getMapByYear(Date year) {
        return byYearAndNumberOfChildren.get(year.getYearDate());
    }

    public Collection<Person> getByNumberOfChildren(Date year, Integer numberOfChildren) {

        return byYearAndNumberOfChildren.get(year.getYearDate()).get(numberOfChildren);
    }

    public void updatePerson(Person person, int numberOfChildrenInMostRecentMaternity) {

        int previousNumberOfChildren = countChildren(person) - numberOfChildrenInMostRecentMaternity;

        Collection<Person> people = byYearAndNumberOfChildren.get(person.getBirthDate().getYearDate()).get(previousNumberOfChildren);

        boolean found = false;

        for (Person p : people) {
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

    public Collection<Person> removeRandomPersons(int numberToRemove, YearDate yearOfBirth) {
        Collection<Person> people = new ArrayList<>(numberToRemove);
        Iterator<Person> iterator = getByYear(yearOfBirth).iterator();

        for(int i = 0; i < numberToRemove; i++) {
            Person p = iterator.next();
            people.add(p);
            removePerson(p);
        }

        return people;
    }

    private Integer countChildren(Person person) {

        int count = 0;

        for (IPartnership partnership : person.getPartnerships()) {
            count += partnership.getChildren().size();
        }

        return count;

    }

}