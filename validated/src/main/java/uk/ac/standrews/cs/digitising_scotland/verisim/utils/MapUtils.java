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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils;


import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsTables.dataDistributions.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MapUtils {


    public static int getMax(Set<Integer> integers) {

        int max = Integer.MIN_VALUE;

        for (Integer i : integers) {
            if (i > max) {
                max = i;
            }
        }
        return max;

    }

    public static <U> int countObjectsInCollectionsInMap(Map<Integer, Collection<U>> map) {

        int count = 0;

        for (Map.Entry<Integer, Collection<U>> i : map.entrySet()) {
            count += i.getValue().size();
        }

        return count;

    }

    public static int sumOfFlooredValues(Map<IntegerRange, Double> map) {

        int sum = 0;

        for (Map.Entry<IntegerRange, Double> iR : map.entrySet()) {
            double d = iR.getValue();
            sum += (int) d;
        }

        return sum;

    }

    public static Map<Integer, Integer> floorAllValuesInMap(Map<IntegerRange, Double> map) {

        Map<Integer, Integer> temp = new HashMap<>();

        for (Map.Entry<IntegerRange, Double> iR : map.entrySet()) {

            temp.put(iR.getKey().getValue(), iR.getValue().intValue());

        }

        return temp;

    }

    public static void print(String label, Map<IntegerRange, ?> temp, int s, int interval, int e) {
        System.out.print(label + " | ");
        for (int i = s; i <= e; i += interval) {
            IntegerRange iR = null;
            for (IntegerRange r : temp.keySet()) {
                if (r.contains(i)) {
                    iR = r;
                    break;
                }
            }

            System.out.print(temp.get(iR) + " | ");
        }
        System.out.println();
    }

    public static Map<IntegerRange, OneDimensionDataDistribution> clone(Map<IntegerRange, OneDimensionDataDistribution> tableData) {

        Map<IntegerRange, OneDimensionDataDistribution> clone = new HashMap<IntegerRange, OneDimensionDataDistribution>();

        for (Map.Entry<IntegerRange, OneDimensionDataDistribution> iR : tableData.entrySet()) {
            clone.put(iR.getKey(), iR.getValue().clone());
        }

        return clone;

    }

    public static Map<IntegerRange, Double> cloneODM(Map<IntegerRange, Double> tableData) {

        Map<IntegerRange, Double> clone = new HashMap<>();

        for(Map.Entry<IntegerRange, Double> iR : tableData.entrySet()) {
            clone.put(iR.getKey(), iR.getValue());
        }


        return clone;
    }
}
