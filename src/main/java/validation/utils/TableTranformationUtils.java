package validation.utils;

import datastructure.summativeStatistics.generated.EventType;
import datastructure.summativeStatistics.structure.FailureAgainstTimeTable.FailureTimeRow;
import datastructure.summativeStatistics.structure.InvalidRangeException;
import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPopulation;
import utils.time.Date;
import utils.time.UnsupportedDateConversion;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TableTranformationUtils {

    public static Collection<FailureTimeRow> transformSurvivorTableToTableOfOrderedIndividualFailureTime(OneDimensionDataDistribution survivorTable, int denoteGroupAs, int timeLimit) throws UnsupportedDateConversion {

        Collection<FailureTimeRow> rows = new ArrayList<>();

//        OneDimensionDataDistribution survivorTable = getCohortSurvivorTable(year, event, scalingFactor, timeLimit, generatedPopulation);


        double prevSurvivors = survivorTable.getData(0);

//        double prevSurvivors = scalingFactor + 1;

        try {

            for (int i = 1; i < timeLimit; i++) {

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
}
