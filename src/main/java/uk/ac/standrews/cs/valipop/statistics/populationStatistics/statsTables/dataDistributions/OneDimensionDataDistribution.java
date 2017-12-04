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
package uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.InvalidRangeException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OneDimensionDataDistribution implements InputMetaData, Cloneable {


    public static Logger log = LogManager.getLogger(OneDimensionDataDistribution.class);

    private final YearDate year;
    private final String sourcePopulation;
    private final String sourceOrganisation;

    protected final Map<IntegerRange, Double> targetRates;

    public OneDimensionDataDistribution(YearDate year,
                                        String sourcePopulation,
                                        String sourceOrganisation,
                                        Map<IntegerRange, Double> tableData) {

        this.year = year;
        this.sourcePopulation = sourcePopulation;
        this.sourceOrganisation = sourceOrganisation;
        this.targetRates = tableData;
    }

    public void updateValue(Integer row, double newValue) {

        IntegerRange rowRange = resolveRowValue(row);

        targetRates.replace(rowRange, newValue);
    }

    @Override
    public YearDate getYear() {
        return year;
    }

    @Override
    public String getSourcePopulation() {
        return sourcePopulation;
    }

    @Override
    public String getSourceOrganisation() {
        return sourceOrganisation;
    }

    @Override
    public IntegerRange getSmallestLabel() {
        int min = Integer.MAX_VALUE;
        IntegerRange minRange = null;
        for (IntegerRange iR : targetRates.keySet()) {
            int v = iR.getMin();
            if (v < min) {
                min = v;
                minRange = iR;
            }
        }
        return minRange;
    }

    @Override
    public IntegerRange getLargestLabel() {
        IntegerRange max = null;
        int maxV = Integer.MIN_VALUE;
        for (IntegerRange iR : targetRates.keySet()) {
            int v = iR.getMax();
            if (v > maxV) {
                max = iR;
                maxV = v;
            }
        }
        return max;

    }

    public double getRate(Integer rowValue) throws InvalidRangeException {

        IntegerRange row = resolveRowValue(rowValue);

        return targetRates.get(row);
    }

    public IntegerRange resolveRowValue(Integer rowValue) {

        for (IntegerRange iR : targetRates.keySet()) {
            if (iR.contains(rowValue)) {
                return iR;
            }
        }

        throw new InvalidRangeException("Given value not covered by rows - value " + rowValue);
    }

    public Map<IntegerRange, Double> getRate() {
        return targetRates;
    }

    public Map<IntegerRange, Double> cloneData() {
        Map<IntegerRange, Double> map = new HashMap<IntegerRange, Double>();

        for (Map.Entry<IntegerRange, Double> iR : targetRates.entrySet()) {
            map.put(iR.getKey(), iR.getValue());
        }

        return map;

    }

    public OneDimensionDataDistribution clone() {

        return new OneDimensionDataDistribution(year, sourcePopulation, sourceOrganisation, cloneData());

    }

    public void print(PrintStream out) {

        IntegerRange[] orderedKeys = getRate().keySet().toArray(new IntegerRange[getRate().keySet().size()]);
        Arrays.sort(orderedKeys, IntegerRange::compareTo);

        out.println("YEAR\t" + year);
        out.println("POPULATION\t" + sourcePopulation);
        out.println("SOURCE\t" + sourceOrganisation);
        out.println("DATA");

        for(IntegerRange iR : orderedKeys) {
            out.println(iR.getValue() + "\t" + targetRates.get(iR));
        }

        out.println();

    }

    public Set<IntegerRange> getLabels() {
        return targetRates.keySet();
    }

}
