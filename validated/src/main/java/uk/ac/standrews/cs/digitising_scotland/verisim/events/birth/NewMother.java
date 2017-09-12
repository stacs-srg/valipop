package uk.ac.standrews.cs.digitising_scotland.verisim.events.birth;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NewMother {

    private IPersonExtended newMother;
    private int numberOfChildrenInMaternity;

    public NewMother(IPersonExtended newMother, int numberOfChildrenInMaternity) {
        this.newMother = newMother;
        this.numberOfChildrenInMaternity = numberOfChildrenInMaternity;
    }

    public IPersonExtended getNewMother() {
        return newMother;
    }

    public int getNumberOfChildrenInMaternity() {
        return numberOfChildrenInMaternity;
    }
}
