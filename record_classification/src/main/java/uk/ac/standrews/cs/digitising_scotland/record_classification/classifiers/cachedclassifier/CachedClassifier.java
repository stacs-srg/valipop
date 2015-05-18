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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.cachedclassifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.AbstractClassifier;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class CachedClassifier extends AbstractClassifier {

    private Map<TokenSet, Classification> cache;
    private Classifier classifier;

    public CachedClassifier(final Classifier classifier, final Map<TokenSet, Classification> map) {

        this.classifier = classifier;
        this.cache = map;
    }

    @Override
    public Set<Classification> classify(final TokenSet k) throws IOException, ClassNotFoundException {

        Classification v = cache.get(k);
        if (v == null) {
            v = getSingleClassification(classifier.classify(k));
            cache.put(k, v);
        }
        return makeClassificationSet(v);
    }
}
