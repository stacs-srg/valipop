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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting;


import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.LabelValueDataRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.kaplanMeier.utils.FailureTimeRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.InvalidRangeException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TableTranformationUtils {

    public static Collection<FailureTimeRow> transformSurvivorTableToTableOfOrderedIndividualFailureTime(OneDimensionDataDistribution survivorTable, String denoteGroupAs) {

        Collection<FailureTimeRow> rows = new ArrayList<>();

        int timeLimit = survivorTable.getLargestLabel().getMax();

        double prevSurvivors = survivorTable.getRate(0);

        try {

            for (int i = 1; i <= timeLimit; i++) {

                double currentSurvivors = survivorTable.getRate(i);
                double dead = prevSurvivors - currentSurvivors;

                int d = (int) dead;

                prevSurvivors -= d;

                for (int r = 0; r < d; r++) {
                    rows.add(new FailureTimeRow(i, true, denoteGroupAs));
                }

            }

        } catch(InvalidRangeException e) {
            // No action needed
        }

        for(int s = 0; s < prevSurvivors; s++) {
            rows.add(new FailureTimeRow(timeLimit, false, denoteGroupAs));
        }

        return rows;
    }

    public static Collection<LabelValueDataRow> transform1DDDToCollectionOfLabelValueDataRow(OneDimensionDataDistribution data, String denoteGroupAs) {

        Collection<LabelValueDataRow> rows = new ArrayList<>();

        for(IntegerRange iR : data.getRate().keySet()) {

            double value;

            try {
                value = data.getRate(iR.getValue());
                rows.add(new LabelValueDataRow(iR.getValue(), value, denoteGroupAs));
            } catch (NullPointerException e) {
                rows.add(new LabelValueDataRow(iR.getValue(), 0, denoteGroupAs));
            }



        }

        return rows;

    }


}
