package model.simulationLogic.stochastic;

import datastructure.summativeStatistics.structure.IntegerRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BinomialApproach {

    public static Map<IntegerRange, Integer> chooseValues(int size, Map<IntegerRange, Double> rates, Random random) {

        Map<IntegerRange, Integer> values = new HashMap<IntegerRange, Integer>();

        ArrayList<IntegerRange> orderedRanges = SharedNewLogic.getIntegerRangesInOrder(rates);

        double[] cumalativeRates = calculateCumalativeRates(rates, orderedRanges);

        try {
            for (int i = 0; i < size; i++) {
                double r = random.nextDouble();
                IntegerRange chosenRange = resolveToRange(r, cumalativeRates, orderedRanges);
                Integer t = values.get(chosenRange);
                values.put(chosenRange, t != null ? t + 1 : 1);
            }
        } catch (ValueOutOfRateBounds e) {
            throw new Error(e);
        }

        return values;

    }

    private static IntegerRange resolveToRange(double value, double[] cumalativeRates, ArrayList<IntegerRange> orderedRanges) throws ValueOutOfRateBounds {

        for(int i = 0; i < cumalativeRates.length; i++) {
            if(value < cumalativeRates[i]) {
                return orderedRanges.get(i);
            }
        }

        throw new ValueOutOfRateBounds("Given value does not fall in expected cumalative rate bounds");
    }

    private static double[] calculateCumalativeRates(Map<IntegerRange, Double> rates, ArrayList<IntegerRange> orderedRanges) {

        double[] cumalativeRates = new double[orderedRanges.size()];

        int i = 0;
        for(IntegerRange iR : orderedRanges) {
            double r = rates.get(iR);
            if(i == 0) {
                cumalativeRates[i] = r;
            } else {
                cumalativeRates[i] = cumalativeRates[i-1] + r;
            }
            i++;
        }

        if(cumalativeRates[cumalativeRates.length - 1] != 1) {
            double offBy = Math.abs(cumalativeRates[cumalativeRates.length - 1] - 1);
            if(offBy < 0.00001) {
                cumalativeRates[cumalativeRates.length - 1] = 1;
            } else {
                System.out.println("Should be 1 rather is: " + cumalativeRates[cumalativeRates.length - 1]);
            }
        }

        return cumalativeRates;
    }

}
