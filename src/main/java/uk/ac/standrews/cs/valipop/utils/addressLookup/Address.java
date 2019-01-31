package uk.ac.standrews.cs.valipop.utils.addressLookup;

import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Address {

    final long number;
    final Area area;

    ArrayList<IPerson> inhabitants = new ArrayList<>();

    public Address(long number, Area area) {
        this.number = number;
        this.area = area;
    }

    public void addInhabitant(IPerson person) {
        inhabitants.add(person);
    }

    public boolean removeInhabitant(IPerson person) {
        return inhabitants.remove(person);
    }

    public List<IPerson> getInhabitants() {
        return inhabitants;
    }

    public boolean isInhabited() {
        return inhabitants.size() != 0;
    }

    public Area getArea() {
        return area;
    }

    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append(number + " ");
        s.append(area.toString());

        return s.toString();

    }

    public String toShortForm() {

        StringBuilder s = new StringBuilder();

        int count = 2;

        if(area.getSuburb() != null && count-- > 0)
            s.append(area.getSuburb());

        if(area.getTown() != null && count-- > 0)
            s.append(area.getTown());

        if(area.getCounty() != null && count > 0)
            s.append(area.getCounty());

        return s.toString();

    }
}
