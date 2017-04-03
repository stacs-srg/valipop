package validation.utils;

import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.InvalidRangeException;
import datastructure.summativeStatistics.structure.LabelValueDataRow;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import utils.time.UnsupportedDateConversion;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TableTranformationUtils {

    public static Collection<FailureTimeRow> transformSurvivorTableToTableOfOrderedIndividualFailureTime(OneDimensionDataDistribution survivorTable, String denoteGroupAs) throws UnsupportedDateConversion {

        Collection<FailureTimeRow> rows = new ArrayList<>();

        int timeLimit = survivorTable.getLargestLabel().getMax();

        double prevSurvivors = survivorTable.getData(0);

        try {

            for (int i = 1; i <= timeLimit; i++) {

                double currentSurvivors = survivorTable.getData(i);
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

        for(IntegerRange iR : data.getData().keySet()) {

            double value;

            try {
                value = data.getData(iR.getValue());
                rows.add(new LabelValueDataRow(iR.getValue(), value, denoteGroupAs));
            } catch (NullPointerException e) {
                rows.add(new LabelValueDataRow(iR.getValue(), 0, denoteGroupAs));
            }



        }

        return rows;

    }


}
