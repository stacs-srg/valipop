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

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Uses a lookup table to return matches as classifications.
 *
 * @author frjd2, jkc25
 */
public class ExactMatchClassifier2  implements Classifier {

    private Map<TokenSet, Set<Classification>> known_classifications;

    public ExactMatchClassifier2() {

        known_classifications = new HashMap<>();
    }

    public void train(final Bucket bucket) throws Exception {

        for (Record record : bucket) {
            loadRecord(record);
        }
    }

    private void loadRecord(final Record record) {

        String description = record.getDescription();

        final Set<Classification> classifications = record.getOriginalData().getGoldStandardClassifications();

        known_classifications.put(new TokenSet(description), classifications);
    }

    public Set<Classification> classify(final TokenSet tokenSet) throws IOException {

        return known_classifications.get(tokenSet);
    }
}
