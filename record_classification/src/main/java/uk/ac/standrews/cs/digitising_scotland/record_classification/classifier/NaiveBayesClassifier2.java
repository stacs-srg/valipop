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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;

import java.util.HashMap;
import java.util.Objects;

public class NaiveBayesClassifier2 extends AbstractClassifier {

    private static final long serialVersionUID = 7439350806549465200L;

    private final HashMap<String, Classification> known_classifications;

    public NaiveBayesClassifier2() {

        known_classifications = new HashMap<>();
    }

    public void train(final Bucket bucket) {

        for (Record record : bucket) {
            loadRecord(record);
        }
    }

    public Classification classify(final String data) {

        final Classification exact_classification = known_classifications.get(data);
        return exact_classification != null ? exact_classification : Classification.UNCLASSIFIED;
    }

    private void loadRecord(final Record record) {

        known_classifications.put(record.getData(), record.getClassification());
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final NaiveBayesClassifier2 that = (NaiveBayesClassifier2) o;
        return Objects.equals(known_classifications, that.known_classifications);
    }

    @Override
    public int hashCode() {

        return Objects.hash(known_classifications);
    }
}
