package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonCharacteristicsIdentifier {

    public static IPartnershipExtended getActivePartnership(IPersonExtended person, Date currentDate) {

        ArrayList<IPartnershipExtended> partnershipsInYear = new ArrayList<>(person.getPartnershipsActiveInYear(currentDate.getYearDate()));

        if(partnershipsInYear.size() > 1) {
            throw new UnsupportedOperationException("Lots of partners in year - likely for a female to get this error");
        } else if (partnershipsInYear.size() == 0) {
            return null;
        } else {
            return partnershipsInYear.get(0);
        }

    }

    public static Integer getChildrenBirthedInYear(IPartnershipExtended activePartnership, YearDate year) {

        if(activePartnership == null) {
            return 0;
        }

        Collection<IPersonExtended> children = activePartnership.getChildren();

        int c = 0;

        for(IPersonExtended child : children) {
            if(child.bornInYear(year)) {
                c++;
            }
        }

        return c;

    }


    public static Integer getChildrenBirthedBeforeDate(IPartnershipExtended activePartnership, Date year) {

        if(activePartnership == null) {
            return 0;
        }

        Collection<IPersonExtended> children = activePartnership.getChildren();

        int c = 0;

        for(IPersonExtended child : children) {
            if(child.bornBefore(year)) {
                c++;
            }
        }

        return c;

    }


    public static Boolean toSeparate(IPartnershipExtended activePartnership, YearDate y) {

        if(activePartnership == null) {
            return null;
        }

        IPersonExtended lastChild = activePartnership.getLastChild();

        if (!lastChild.bornInYear(y)) {
            return false;
        } else if (activePartnership.getSeparationDate() != null) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean startedInYear(IPartnershipExtended activePartnership, YearDate y) {

        Date startDate = activePartnership.getPartnershipDate();

        return !DateUtils.dateBefore(startDate, y) && DateUtils.dateBefore(startDate, y.advanceTime(1, TimeUnit.YEAR));
    }

}
