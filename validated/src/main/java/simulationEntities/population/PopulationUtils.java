package simulationEntities.population;


import dateModel.dateImplementations.AdvancableDate;
import dateModel.timeSteps.CompoundTimeUnit;
import simulationEntities.person.IPerson;
import dateModel.Date;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface PopulationUtils {

//    Collection<IPerson> getByYear(Date year);

//    Collection<IPerson> getByYearAndSex(char sex, Date year);

    Collection<IPerson> forceGetAllPersonsByTimePeriod(AdvancableDate firstDate, CompoundTimeUnit timePeriod);

    Collection<IPerson> forceGetAllPersonsByTimePeriodAndSex(AdvancableDate firstDate, CompoundTimeUnit timePeriod, char sex);

}
