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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ensemble.EnsembleClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;

/**
 * Resolves any given unresolved classifications to {@link Classification#UNCLASSIFIED}.
 * This class is used for testing purposes.
 *
 * @author Masih Hajiarab Derkani
 */
class DummyResolutionStrategy implements EnsembleClassifier.ResolutionStrategy {

    private static final long serialVersionUID = 8868271992781552957L;

    @Override
    public Classification resolve(Map<Classifier, Classification> candidate_classifications) {

        for (Classification classification : candidate_classifications.values()) {
            if (!classification.equals(Classification.UNCLASSIFIED)) {
                return classification;
            }
        }

        return Classification.UNCLASSIFIED;
    }

    @Override
    public boolean equals(final Object o) {

        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {

        return 23;
    }
}
