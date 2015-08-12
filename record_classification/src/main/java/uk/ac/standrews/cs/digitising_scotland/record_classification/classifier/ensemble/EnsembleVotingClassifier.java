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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ensemble;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.composite.StringSimilarityGroupWithSharedState;

import java.util.*;

/**
 * @author Graham Kirby
 */
public class EnsembleVotingClassifier extends EnsembleClassifier {

    private static final long serialVersionUID = 6432371860467757296L;

    /**
     * Needed for JSON deserialization.
     */
    public EnsembleVotingClassifier() {
    }

    /**
     * Instantiates a new ensemble classifier.
     *
     * @param classifiers the classifiers
     */
    public EnsembleVotingClassifier(Collection<Classifier> classifiers) {

        super(classifiers, new VotingResolutionStrategy());
    }

    public EnsembleVotingClassifier(Collection<Classifier> classifiers, StringSimilarityGroupWithSharedState group) {

        super(classifiers, group, new VotingResolutionStrategy());
    }
}
