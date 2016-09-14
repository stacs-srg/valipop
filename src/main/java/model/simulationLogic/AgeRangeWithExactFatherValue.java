package model.simulationLogic;

import datastructure.summativeStatistics.structure.DataKey;
import datastructure.summativeStatistics.structure.IntegerRange;
import model.IPerson;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AgeRangeWithExactFatherValue {

    private IntegerRange ageRange;
    private double value;
    private DataKey key;
    private Collection<IPerson> fathers = new ArrayList<>();
    private int carriedFathers = 0;


    public AgeRangeWithExactFatherValue(DataKey key, IntegerRange ageRange, double value) {
        this.ageRange = ageRange;
        this.value = value;
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public IntegerRange getAgeRange() {
        return ageRange;
    }

    public double getRemainder() {
        return value - (int) value;
    }

    public DataKey getKey() {
        return key;
    }

    public void addFathers(Collection<IPerson> fathersToAdd) {
        fathers.addAll(fathersToAdd);
    }

    public Collection<IPerson> getFathers() {
        return fathers;
    }

    public int getCarriedFathers() {
        return carriedFathers;
    }

    public void incrementCarriedFathers(int fathers) {
        carriedFathers += fathers;
    }

    public void updateCarriedFathers() {
        carriedFathers = fathers.size();
    }

}
