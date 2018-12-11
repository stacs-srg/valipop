package uk.ac.standrews.cs.valipop.utils.addressLookup;

import uk.ac.standrews.cs.valipop.simulationEntities.Person;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Address {

    final long number;
    final Area area;

    ArrayList<Person> inhabitants = new ArrayList<>();

    public Address(long number, Area area) {
        this.number = number;
        this.area = area;
    }

    public void addInhabitant(Person person) {
        inhabitants.add(person);
    }

    public boolean removeInhabitant(Person person) {
        return inhabitants.remove(person);
    }

    public boolean isInhabited() {
        return inhabitants.size() == 0;
    }

    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append("--- Address ---\n");

        s.append("Number: " + number + "\n");
        s.append(area.toString());

        s.append("--- EO-Address ---\n");

        return s.toString();

    }

}
