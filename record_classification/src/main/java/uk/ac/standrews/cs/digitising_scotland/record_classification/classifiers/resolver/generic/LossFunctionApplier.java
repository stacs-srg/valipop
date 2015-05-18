/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.tools.MapSorter;

import java.util.*;

/**
 * Helper function for applying a loss function to a collection
 * of Sets and finding set with the best loss.
 * Created by fraserdunlop on 07/10/2014 at 16:26.
 */
public class LossFunctionApplier {

    private MapSorter mapSorter = new MapSorter();
    private LossFunction<Multiset<Classification>, Double> lossFunction;

    public LossFunctionApplier(final LossFunction<Multiset<Classification>, Double> lossFunction) {

        this.lossFunction = lossFunction;
    }

    /**
     * Returns the Set with the best loss. Will pick the
     * set whose loss sorts to the head of a list of LossMetric
     * objects. If the comparator for your LossMetric is implemented
     * the wrong way round then getBest will return the worst set
     * rather than the best.
     * @param sets sets to rank based on loss
     * @return best according to lossFunction or null if sets is empty.
     */
    public Set<Classification> getBest(final Collection<Multiset<Classification>> sets) {

        Map<Multiset<Classification>, Double> map = mapValuesToLoss(sets);
        map = mapSorter.sortByValue(map);
        if (map.keySet().iterator().hasNext()) {
            return toSet(map);
        }
        return new HashSet<>();

    }

    private Set<Classification> toSet(Map<Multiset<Classification>, Double> map) {
        Set<Classification> tempSet = new HashSet<>();
        tempSet.addAll(map.keySet().iterator().next());
        return tempSet;
    }

    private Map<Multiset<Classification>, Double> mapValuesToLoss(final Collection<Multiset<Classification>> values) {

        Map<Multiset<Classification>, Double> map = new HashMap<>();
        for (Multiset<Classification> set : values) {
            map.put(set, lossFunction.calculate(set));
        }
        return map;
    }
}
