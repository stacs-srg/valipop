package model.simulationLogic;

import datastructure.summativeStatistics.structure.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeRangeToValue {

    private IntegerRange ageRange;
    private double value;

    public AgeRangeToValue(IntegerRange ageRange, double value) {
        this.ageRange = ageRange;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public IntegerRange getAgeRange() {
        return ageRange;
    }

    public double getRemainder() {
        return value - (int) value;
    }
}
