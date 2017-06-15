package uk.ac.standrews.cs.digitising_scotland.verisim.events.birth;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MotherSet {

    private Collection<IPerson> havePartners;
    private Collection<IPerson> needPartners;
    private int newlyProducedChildren;

    public MotherSet(Collection<IPerson> havePartners, Collection<IPerson> needPartners) {
        this.havePartners = havePartners;
        this.needPartners = needPartners;
    }

    public MotherSet(Collection<IPerson> havePartners, Collection<IPerson> needPartners, int newlyProducedChildren) {
        this(havePartners, needPartners);
        this.newlyProducedChildren = newlyProducedChildren;
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

    public int getNewlyProducedChildren() {
        return newlyProducedChildren;
    }
}
