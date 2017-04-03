package utils;

import model.simulationEntities.IPerson;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PartnershipUtils {

    void addChildren(Collection<IPerson> children);

    void setFather(IPerson father);

}
