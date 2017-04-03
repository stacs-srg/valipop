package model.simulationLogic.stochastic;

import datastructure.population.exceptions.InsufficientNumberOfPeopleException;
import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.SelfCorrectingOneDimensionDataDistribution;
import datastructure.summativeStatistics.structure.SelfCorrectingTwoDimensionDataDistribution;
import model.simulationEntities.IPerson;

import java.util.*;

import org.apache.logging.log4j.Logger;
/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SharedNewLogic {

    public static ArrayList<IntegerRange> getIntegerRangesInOrder(SelfCorrectingOneDimensionDataDistribution tableRow) {
        ArrayList<IntegerRange> integerRanges = new ArrayList<>(tableRow.getData().keySet());
        Collections.sort(integerRanges);
        return integerRanges;
    }

    public static <V> ArrayList<IntegerRange> getIntegerRangesInOrder(Map<IntegerRange, V> map) {
        ArrayList<IntegerRange> integerRanges = new ArrayList<>(map.keySet());
        Collections.sort(integerRanges);
        return integerRanges;
    }

    public static ArrayList<IntegerRange> getIntegerRangesInOrder(SelfCorrectingTwoDimensionDataDistribution table) {
        ArrayList<IntegerRange> integerRanges = new ArrayList<>(table.getRowKeys());
        Collections.sort(integerRanges);
        return integerRanges;
    }

    public static Collection<IPerson> chooseNFromCollection(Integer n, Collection<IPerson> collection, Random random, Logger log) throws InsufficientNumberOfPeopleException {

        ArrayList<IPerson> collectionAL = new ArrayList<>(collection);
        Collection<IPerson> chosen = new ArrayList<>();

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
