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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.nb2;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import weka.classifiers.bayes.*;
import weka.core.*;
import weka.gui.beans.*;

public class NaiveBayesClassifier2 implements Classifier {

    NaiveBayes naive_bayes;

    public NaiveBayesClassifier2() {

        naive_bayes = new NaiveBayes();
    }

    public void train(final Bucket bucket) throws Exception {

        naive_bayes.buildClassifier(getInstances(bucket));
    }

    private Instances getInstances(Bucket bucket) {
        return null;
    }

    public Classification classify(final String data) throws Exception {

        double[] probabilities = naive_bayes.distributionForInstance(makeInstance(data));
        return getClassification(probabilities);
    }

    private Classification getClassification(double[] probabilities) {
        return null;
    }

    private Instance makeInstance(String data) {
        return null;
    }

    class DataSource2 extends AbstractDataSource {

    }

}
