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
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.SubsetEnumerator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.ValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.io.IOException;
import java.util.*;

/**
 * Resolver Pipeline Classifier. Splits FeatureSet into subsets, classifies the subsets,
 * puts the classifications in a MultiValueMap with Codes as keys, MultiValueMap then
 * gets handed through the pipeline which removes Classifications below a specified
 * Threshold, resolves ancestral hierarchies (as they are assumed to be mutually exclusive),
 * if the multipleClassifications flag is set to false then the MultiValueMap is flattened
 * rather than hierarchies being resolved, prunes the map until its complexity is within a specified bound,
 * finds the valid combinations of Classifications from the map (this is expensive for high
 * complexity MultiValueMaps hence the bound) and finally returns the combination of codes which
 * maximises the loss function.
 * Created by fraserdunlop on 08/10/2014 at 09:50.
 */
public class ResolverPipeline {

    private final boolean multipleClassifications;
    private final boolean resolveHierarchies;
    private IClassifier classifier;
    private BelowThresholdRemover belowThresholdRemover;
    private HierarchyResolver hierarchyResolver;
    private Flattener flattener;
    private MultiValueMapPruner mapPruner;
    private ValidCombinationGetter validCombinationGetter;
    private LossFunctionApplier lossFunctionApplier;
    private SubsetEnumerator<TokenSet> subsetEnumerator;

    public ResolverPipeline(final IClassifier classifier, final boolean multipleClassifications, final Comparator<Classification> classificationComparator, final ValidityAssessor<Multiset<Classification>, TokenSet> classificationSetValidityAssessor, final LossFunction<Multiset<Classification>, Double> lengthWeightedLossFunction,
                            final SubsetEnumerator<TokenSet> subsetEnumerator, final double threshold, final boolean resolveHierarchies) {

        this.classifier = classifier;
        this.multipleClassifications = multipleClassifications;
        this.resolveHierarchies = resolveHierarchies;
        belowThresholdRemover = new BelowThresholdRemover(threshold);
        hierarchyResolver = new HierarchyResolver();
        flattener = new Flattener();
        mapPruner = new MultiValueMapPruner(classificationComparator);
        validCombinationGetter = new ValidCombinationGetter(classificationSetValidityAssessor);
        lossFunctionApplier = new LossFunctionApplier(lengthWeightedLossFunction);
        this.subsetEnumerator = subsetEnumerator;
    }

    public Set<Classification> classify(final TokenSet featureSet) throws IOException, ClassNotFoundException {

        MultiValueMap<Code, Classification> multiValueMap = classifySubsets(featureSet);
        return resolverPipeline(multiValueMap, featureSet);
    }

    private Set<Classification> resolverPipeline(MultiValueMap<Code, Classification> multiValueMap, final TokenSet featureSet) throws IOException, ClassNotFoundException {

        List<Multiset<Classification>> validSets = new ArrayList<>();

        multiValueMap = belowThresholdRemover.removeBelowThreshold(multiValueMap);
        if (!multiValueMap.isEmpty()) {
            if (multipleClassifications && resolveHierarchies) {
                multiValueMap = hierarchyResolver.moveAncestorsToDescendantKeys(multiValueMap);
            } else {
                multiValueMap = flattener.moveAllIntoKey(multiValueMap, multiValueMap.iterator().next());
            }
            multiValueMap = mapPruner.pruneUntilComplexityWithinBound(multiValueMap);
            if (multipleClassifications) {
                validSets = validCombinationGetter.getValidSets(multiValueMap, featureSet);
            } else {
                validSets = addAllFromMultiValueMap(multiValueMap);
            }
        }
        return lossFunctionApplier.getBest(validSets);
    }

    private List<Multiset<Classification>> addAllFromMultiValueMap(MultiValueMap<Code, Classification> multiValueMap) {

        List<Multiset<Classification>> list = new ArrayList<>();

        for (Code code : multiValueMap) {

            List<Classification> l = multiValueMap.get(code);
            for (Classification classification : l) {
                HashMultiset<Classification> temp = HashMultiset.create();
                temp.add(classification);
                list.add(temp);
            }

        }
        return list;
    }

    private MultiValueMap<Code, Classification> classifySubsets(final TokenSet tokenSet) throws IOException, ClassNotFoundException {

        MultiValueMap<Code, Classification> multiValueMap = new MultiValueMap<>(new HashMap<Code, List<Classification>>());
        Multiset<TokenSet> subsets = subsetEnumerator.enumerate(tokenSet);
        for (TokenSet set : subsets) {
            Classification classification = classifier.classify(set);
            multiValueMap.add(classification.getProperty(), classification);
        }
        return multiValueMap;
    }
}
