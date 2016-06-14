package model.nameGeneration;

import model.IPerson;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface NameGenerator {

    String getName(IPerson personToBeNamed);

}
