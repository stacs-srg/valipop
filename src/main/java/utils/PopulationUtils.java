package utils;

import model.IPerson;
import utils.time.Date;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationUtils {

    Collection<IPerson> getByYear(Date year);

    Collection<IPerson> getByYearAndSex(char sex, Date year);

}
