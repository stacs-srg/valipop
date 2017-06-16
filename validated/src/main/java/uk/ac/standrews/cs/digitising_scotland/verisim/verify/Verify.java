/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
