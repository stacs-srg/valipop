package uk.ac.standrews.cs.digitising_scotland.verisim.events.partnering;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ProposedPartnership {

    IPerson male;
    IntegerRange malesRange;

    IPerson female;

    public ProposedPartnership(IPerson male, IPerson female, IntegerRange malesRange) {
        this.male = male;
        this.female = female;
        this.malesRange = malesRange;
    }

    public void setMale(IPerson newMale, IntegerRange newMalesRange) {
        this.male = newMale;
        this.malesRange = newMalesRange;
    }

    public IntegerRange getMalesRange() {
        return malesRange;
    }

    public IPerson getFemale() {
        return female;
    }

    public IPerson getMale() {
        return male;
    }
}
