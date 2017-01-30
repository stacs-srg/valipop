package simulationEntities.population;


import simulationEntities.IPerson;
import dateModel.Date;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationUtils {

    Collection<IPerson> getByYear(Date year);

    Collection<IPerson> getByYearAndSex(char sex, Date year);

}
