package events.birth;

import simulationEntities.person.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MotherSet {

    private Collection<IPerson> havePartners;
    private Collection<IPerson> needPartners;

    public MotherSet(Collection<IPerson> havePartners, Collection<IPerson> needPartners) {
        this.havePartners = havePartners;
        this.needPartners = needPartners;
    }

    public Collection<IPerson> getHavePartners() {
        return havePartners;
    }

    public Collection<IPerson> getNeedPartners() {
        return needPartners;
    }

    public int size() {
        return havePartners.size() + needPartners.size();
    }
}
