/*
 * Copyright 2015 Digitising Scotland project:
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
package old.record_classification_old.classifiers.resolver;

import com.google.common.collect.Multiset;
import old.record_classification_old.classifiers.Classifier;
import old.record_classification_old.classifiers.lookup.NGramSubstrings;
import old.record_classification_old.classifiers.resolver.Interfaces.LossFunction;
import old.record_classification_old.classifiers.resolver.generic.ResolverPipeline;
import old.record_classification_old.datastructures.classification.Classification;
import old.record_classification_old.datastructures.classification.ClassificationComparator;
import old.record_classification_old.datastructures.classification.ClassificationSetValidityAssessor;

public class RecordClassificationResolverPipeline extends ResolverPipeline {

    public RecordClassificationResolverPipeline(final Classifier classifier, final LossFunction<Multiset<Classification>, Double> lengthWeightedLossFunction, final Double confidenceThreshold, final boolean multipleClassifications, final boolean resolveHierarchies) {

        super(classifier, multipleClassifications, new ClassificationComparator(), new ClassificationSetValidityAssessor(), lengthWeightedLossFunction, new NGramSubstrings(), confidenceThreshold, resolveHierarchies);
    }
}
