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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExactMatchClassifier2  implements Classifier2 {

    private Map<String, Classification2> known_classifications;

    public ExactMatchClassifier2() {

        known_classifications = new HashMap<>();
    }

    public void train(final Bucket2 bucket) throws Exception {

        for (Record2 record : bucket) {
            loadRecord(record);
        }
    }

    public Classification2 classify(final String data) {

        return known_classifications.get(data);
    }

    public Bucket2 classify(final Bucket2 bucket) throws IOException {

        Bucket2 classified = new Bucket2();

        for (Record2 record : bucket) {

            final String data = record.getData();
            final Classification2 result = classify(data);

            classified.add(new Record2(record.getId(), data, result));
        }

        return classified;
    }

    private void loadRecord(final Record2 record) {

        known_classifications.put(record.getData(), record.getClassification());
    }
}
