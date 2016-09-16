package utils;

import model.IPerson;

import java.util.Collection;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PartnershipUtils {

    void addChildren(Collection<IPerson> children);

    void setFather(IPerson father);

}
