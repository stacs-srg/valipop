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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.string_similarity;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenList;

import java.util.HashMap;
import java.util.Map;

/**
 * Classifies records based on the string similarity of the training data to unseen data.
 * This class is not thread-safe.
 *
 * @author Masih Hajiarab Derkani
 * @author Graham Kirby
 */
public class StringSimilarityClassifier implements Classifier {

    private static final long serialVersionUID = -6159276459112698341L;

    private SimilarityMetric similarity_metric;
    private Map<String, Classification> known_classifications;

    /**
     * @param similarity_metric the metric by which to calculate similarity between training and unseen data
     */
    public StringSimilarityClassifier(SimilarityMetric similarity_metric) {

        this.similarity_metric = similarity_metric;
        known_classifications = new HashMap<>();
    }

    /**
     * Needed for JSON deserialization.
     */
    public StringSimilarityClassifier() {
    }

    @Override
    public void train(Bucket bucket) {

        for (Record record : bucket) {
            Classification classification = record.getClassification();
            known_classifications.put(record.getData(), new Classification(classification.getCode(), classification.getTokenList(), similarity_metric.getStaticConfidence(), classification.getDetail()));
        }
    }

    @Override
    public Classification classify(final String data) {

        float highest_similarity_found = -1;
        Classification classification = null;

        for (Map.Entry<String, Classification> known_entry : known_classifications.entrySet()) {

            final float known_to_data_similarity = similarity_metric.getSimilarity(known_entry.getKey(), data);
            if (known_to_data_similarity > highest_similarity_found) {
                classification = known_entry.getValue();
                highest_similarity_found = known_to_data_similarity;
            }
        }
        return classification == null ? Classification.UNCLASSIFIED : new Classification(classification.getCode(), new TokenList(data), classification.getConfidence(), classification.getDetail());
    }

    @Override
    public String getName() {

        return getClass().getSimpleName() + "[" + similarity_metric.getName() + "]";
    }

    @Override
    public String getDescription() {

        return "Classifies based on similarity of the string to the training data, using " + similarity_metric.getDescription() + " similarity metric";
    }

    public String toString() {

        return getName();
    }

    public Map<String, Classification> readState() {

        return known_classifications;
    }

    public void writeState(Map<String, Classification> known_classifications) {

        this.known_classifications = known_classifications;
    }
}
