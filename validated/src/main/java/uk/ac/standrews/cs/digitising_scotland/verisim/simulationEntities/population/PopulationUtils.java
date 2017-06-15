package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population;


import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;

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
