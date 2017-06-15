package uk.ac.standrews.cs.digitising_scotland.verisim.annotations.names;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface NameGenerator {

    String getName(IPerson personToBeNamed);

}
