package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.entityExtensions;


import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PartnershipUtils {

    void addChildren(Collection<IPerson> children);

    void setFather(IPerson father);

}
