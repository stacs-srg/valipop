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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.ValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for function which gets a List of all valid Sets of values from a MultiValueMap
 * given a ValidityCriterion. Values associated with the same key are not considered allowable combinations.
 * Created by fraserdunlop on 06/10/2014 at 09:45.
 */
public class ValidCombinationGetter {

    private final ValidityAssessor<Multiset<Classification>, TokenSet> validityAssessor;

    public ValidCombinationGetter(final ValidityAssessor<Multiset<Classification>, TokenSet> validityAssessor) {

        this.validityAssessor = validityAssessor;
    }

    /**
     * Gets a List of all valid Sets of values from a MultiValueMap given a ValidityCriterion and the
     * assumption that values associated with the same key will not be a valid combination. Values from the
     * same key will not be considered as combinations.
     * @param map MultiValueMap to extract valid sets from
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @return a list of all valid sets of values from the MultiValueMap
     */
    public List<Multiset<Classification>> getValidSets(final MultiValueMap<Code, Classification> map, final TokenSet validityCriterion) {

        return calculateValidSets(map, validityCriterion);
    }

    /**
     * Sets up the recursive function for calculating all valid sets.
     * @param map MultiValueMap to extract valid sets from
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @return a list of all valid sets of values from the MultiValueMap
     */
    private List<Multiset<Classification>> calculateValidSets(final MultiValueMap<Code, Classification> map, final TokenSet validityCriterion) {

        List<Multiset<Classification>> validSets = new ArrayList<>();
        validSets.add(null);
        validSets = recursiveMerge(validSets, map, map.iterator(), validityCriterion);
        validSets.remove(null);
        return validSets;
    }

    /**
     *
     * @param validSets List of known valid sets
     * @param map MultiValueMap to extract valid sets from
     * @param iterator iterator over keys in MultiValueMap incremented during recursion
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @return a list of all valid sets of values from the MultiValueMap
     */
    private List<Multiset<Classification>> recursiveMerge(final List<Multiset<Classification>> validSets, final MultiValueMap<Code, Classification> map, final Iterator<Code> iterator, final TokenSet validityCriterion) {

        if (iterator.hasNext()) {
            Code k = iterator.next();
            mergeStep(validSets, map, validityCriterion, k);
            recursiveMerge(validSets, map, iterator, validityCriterion);
        }
        return validSets;
    }

    /**
     * A 'merge step' in the recursion. We check all unions of values associated with K and sets in validSets.
     * If the union of a set from validSets and a value from k is valid according to the ValidityAssessor and
     * ValidityCriterion then it is added to validSets.
     * @param validSets List of known valid sets
     * @param map MultiValueMap to extract valid sets from
     * @param validityCriterion a validity criterion for assessing the validity of a combination given a condition
     * @param k the key whose values are being 'merged' into the sets of validSets
     */
    private void mergeStep(final List<Multiset<Classification>> validSets, final MultiValueMap<Code, Classification> map, final TokenSet validityCriterion, final Code k) {

        List<Classification> kValues = map.get(k);
        List<Multiset<Classification>> tempList = new ArrayList<>();
        for (Multiset<Classification> set : validSets) {
            for (Classification kValue : kValues) {
                Multiset<Classification> tempSet = copyOfUnion(set, kValue);
                if (tempSetIsValid(validityCriterion, tempSet)) {
                    tempList.add(tempSet);
                }
            }
        }
        validSets.addAll(tempList);
    }

    /**
     * Purely for readability of mergeStep method.
     */
    private boolean tempSetIsValid(final TokenSet validityCriterion, final Multiset<Classification> tempSet) {

        return validityAssessor.assess(tempSet, validityCriterion);
    }

    /**
     * Returns a new Set<V> containing union of set and v.
     * @param set set
     * @param v value
     * @return new set - union of set and v
     */
    private Multiset<Classification> copyOfUnion(final Multiset<Classification> set, final Classification v) {

        Multiset<Classification> tempSet = HashMultiset.create();
        if (set != null) {
            tempSet.addAll(set);
        }
        tempSet.add(v);
        return tempSet;
    }
}
