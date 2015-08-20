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

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.SingleClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;

public class NaiveBayesClassifier extends SingleClassifier {

    NaiveBayes naive_bayes;

    public NaiveBayesClassifier() {

        clearModel();
    }

    public void clearModel() {

        naive_bayes = new NaiveBayes();
    }

    public void trainModel(final Bucket bucket) {

        try {
            Instances instances = getInstances(bucket);
            naive_bayes.buildClassifier(instances);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Instances getInstances(Bucket bucket) {

        try {
            File f = Files.createTempFile("naive_bayes_bucket", ".csv").toFile();
            OutputStreamWriter out = new FileWriter(f);
            bucket.toDataSet2(Arrays.asList("id", "data", "code")).print(out);
            out.flush();

            CSVLoader loader = new CSVLoader();
            loader.setSource(f);

            return loader.getDataSet();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Classification doClassify(final String data) {

        try {
            double[] probabilities = naive_bayes.distributionForInstance(makeInstance(data));
            return getClassification(probabilities);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    private Classification getClassification(double[] probabilities) {
        return null;
    }

    private Instance makeInstance(String data) {
        return null;
    }
}
