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
package uk.ac.standrews.cs.digitising_scotland.verisim.model.simulationLogic.stochastic;



import java.util.*;

import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SharedNewLogic {

//    public static ArrayList<IntegerRange> getIntegerRangesInOrder(SelfCorrectingOneDimensionDataDistribution tableRow) {
//        ArrayList<IntegerRange> integerRanges = new ArrayList<>(tableRow.getData().keySet());
//        Collections.sort(integerRanges);
//        return integerRanges;
//    }

    public static <V> ArrayList<IntegerRange> getIntegerRangesInOrder(Map<IntegerRange, V> map) {
        ArrayList<IntegerRange> integerRanges = new ArrayList<>(map.keySet());
        Collections.sort(integerRanges);
        return integerRanges;
    }

    public static ArrayList<IntegerRange> getIntegerRangesInOrder(SelfCorrectingTwoDimensionDataDistribution table) {
        ArrayList<IntegerRange> integerRanges = new ArrayList<>(table.getRowLabels());
        Collections.sort(integerRanges);
        return integerRanges;
    }

    public static Collection<IPersonExtended> chooseNFromCollection(Integer n, Collection<IPersonExtended> collection, Random random, Logger log) throws InsufficientNumberOfPeopleException {

        ArrayList<IPersonExtended> collectionAL = new ArrayList<>(collection);
        Collection<IPersonExtended> chosen = new ArrayList<>();

        try {
            for (int i = 0; i < n; i++) {
                chosen.add(collectionAL.remove(random.nextInt(collectionAL.size())));
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            log.info("Insufficient number of people in collection");
            throw new InsufficientNumberOfPeopleException("Shortage of people in collection", chosen);
        }

        return chosen;

    }

}
