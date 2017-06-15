package uk.ac.standrews.cs.digitising_scotland.verisim.verify;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;

import java.io.PrintStream;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Verify {

    private static PrintStream out = System.out;
    private static boolean print = false;

    public static boolean checkNoNullParentsInParentsPartnership(PeopleCollection population, int allowableDiscrepancy) {

        boolean passed = true;
        int failureCount = 0;

        if(allowableDiscrepancy < 0) {
            allowableDiscrepancy = 0;
        }

        String text = "";

        for(IPartnership partnership : population.getPartnerships()) {
            if(partnership.getMalePartner() == null || partnership.getFemalePartner() == null) {

                failureCount++;

//                if(print) {
                    text += "-S-checkNoNullParentsInParentsPartnership---\n\n";
                    text +=  partnership.toString() + "\n";
//                }

            }
        }

        if(failureCount > allowableDiscrepancy) {
            passed = false;
        }

        if(print && !passed) {
            text += "-E-checkNoNullParentsInParentsPartnership---\n";
            out.print(text);
        }

        return passed;

    }

    public boolean verifyNoZeroChildPartnerships(PeopleCollection population) {

        for(IPerson p : population.getMales().getAll()) {
            for(IPartnership part : p.getPartnerships()) {
                if(part.getChildren().size() == 0) {
                    return false;
                }
            }
        }

        for(IPerson p : population.getFemales().getAll()) {
            for(IPartnership part : p.getPartnerships()) {
                if(part.getChildren().size() == 0) {
                    return false;
                }
            }
        }

        return true;
    }

}
