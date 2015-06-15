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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InconsistentCodingException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;

import java.util.*;

public enum ConsistentCodingCleaner implements Cleaner {

    NONE {
        @Override
        public Bucket clean(Bucket bucket) {
            return bucket;
        }
    },

    CHECK {
        @Override
        public Bucket clean(Bucket bucket) throws InconsistentCodingException {

            Map<String, String> classifications = new HashMap<>();

            for (Record record : bucket) {

                String data = record.getData();
                Classification classification = record.getClassification();

                if (classification != null) {
                    String code = classification.getCode();

                    if (classifications.containsKey(data)) {
                        if (!code.equals(classifications.get(data))) {
                            throw new InconsistentCodingException("data: " + data + " classified as both " + code + " and " + classifications.get(data));
                        }
                    } else {
                        classifications.put(data, code);
                    }
                }
            }

            return bucket;
        }
    },

    REMOVE {
        @Override
        public Bucket clean(Bucket bucket) throws Exception {

            Set<String> inconsistently_coded_data = getInconsistentlyCodedStrings(bucket);

            Bucket cleaned_bucket = new Bucket();
            for (Record record : bucket) {

                String data = record.getData();

                if (!inconsistently_coded_data.contains(data)) {
                    cleaned_bucket.add(record);
                }
            }

            return cleaned_bucket;

        }
    },

    CORRECT {
        @Override
        public Bucket clean(Bucket bucket) throws Exception {

            Map<String, Map<String, Integer>> classification_details = getClassifications(bucket);

            Bucket cleaned_bucket = new Bucket();

            for (Record record : bucket) {

                Map<String ,Integer> details = classification_details.get(record.getData());
                String most_popular_code = getMostPopular(details);

                if (record.getClassification().getCode().equals(most_popular_code)) {
                    cleaned_bucket.add(record);
                }
                else {
                    cleaned_bucket.add(makeCorrectedRecord(record, most_popular_code));
                }
            }

            return cleaned_bucket;
        }
    };

    private static Record makeCorrectedRecord(Record record, String most_popular_code) {

        Classification classification = record.getClassification();
        return new Record(record.getId(), record.getData(), new Classification(most_popular_code, classification.getTokenSet(), classification.getConfidence()));
    }

    private static String getMostPopular(Map<String, Integer> details) {

        String most_popular = "";
        int highest_count = 0;

        for (String code : details.keySet()) {
            int count = details.get(code);
            if (count > highest_count) {
                highest_count = count;
                most_popular = code;
            }
        }

        return most_popular;
    }

    private static Map<String, Map<String, Integer>> getClassifications(Bucket bucket) {

        Map<String, Map<String, Integer>> classifications = new HashMap<>();

        for (Record record : bucket) {

            String data = record.getData();
            if (!classifications.containsKey(data)) {
                classifications.put(data, new HashMap<String, Integer>());
            }

            Map<String, Integer> classifications_for_this_data = classifications.get(data);

            String code = record.getClassification().getCode();
            if (!classifications_for_this_data.containsKey(code)) {
                classifications_for_this_data.put(code, 0);
            }

            classifications_for_this_data.put(code, classifications_for_this_data.get(code) + 1);
        }

        return classifications;
    }

    private static Set<String> getInconsistentlyCodedStrings(Bucket bucket) {

        Set<String> inconsistently_coded_data = new HashSet<>();
        Map<String, String> classifications = new HashMap<>();


        for (Record record : bucket) {

            String data = record.getData();
            Classification classification = record.getClassification();

            if (classification != null) {
                String code = classification.getCode();

                if (classifications.containsKey(data)) {
                    if (!code.equals(classifications.get(data))) {
                        inconsistently_coded_data.add(data);
                    }
                } else {
                    classifications.put(data, code);
                }
            }
        }
        return inconsistently_coded_data;
    }
}
