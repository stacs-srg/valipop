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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Fraser Dunlop
 * @author Graham Kirby
 */
public abstract class AbstractConfusionMatrix2 implements ConfusionMatrix {

    protected final Map<String, Integer> classification_counts;
    protected final Map<String, Integer> true_positive_counts;
    protected final Map<String, Integer> false_positive_counts;
    protected final Map<String, Integer> true_negative_counts;
    protected final Map<String, Integer> false_negative_counts;

    protected final Bucket2 classified_records;
    protected final Bucket2 gold_standard_records;

    public AbstractConfusionMatrix2(final Bucket2 classified_records, final Bucket2 gold_standard_records) throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        this.classified_records = classified_records;
        this.gold_standard_records = gold_standard_records;

        classification_counts = new HashMap<>();
        true_positive_counts = new HashMap<>();
        true_negative_counts = new HashMap<>();
        false_positive_counts = new HashMap<>();
        false_negative_counts = new HashMap<>();

        checkGoldStandardDataIsClassified();
        checkClassifiedDataIsInGoldStandard();
        checkClassifiedToValidCodes();
        checkConsistentClassification(classified_records);
        checkConsistentClassification(gold_standard_records);

        calculateCounts();
    }

    protected abstract boolean classificationsMatch(String asserted_code, String real_code);

    @Override
    public Map<String, Integer> getClassificationCounts() {

        return classification_counts;
    }

    @Override
    public Map<String, Integer> getTruePositiveCounts() {

        return true_positive_counts;
    }

    @Override
    public Map<String, Integer> getFalsePositiveCounts() {

        return false_positive_counts;
    }

    @Override
    public Map<String, Integer> getFalseNegativeCounts() {

        return false_negative_counts;
    }

    @Override
    public Map<String, Integer> getTrueNegativeCounts() {

        return true_negative_counts;
    }

    @Override
    public int getNumberOfClassifications() {

        int total = 0;
        for (Record2 record : classified_records) {

            if (record.getClassification() != null) total++;
        }
        return total;
    }

    /**
     * To be valid, all records in the gold standard records must be classified.
     */
    private void checkGoldStandardDataIsClassified() throws UnclassifiedGoldStandardRecordException {

        for (Record2 record : gold_standard_records) {
            if (record.getClassification() == null) throw new UnclassifiedGoldStandardRecordException();
        }
    }

    /**
     * To be valid, all data in the classified records must appear in the gold standard records.
     */
    private void checkClassifiedDataIsInGoldStandard() throws UnknownDataException {

        Set<String> known_data = new HashSet<>();

        for (Record2 record : gold_standard_records) {

            known_data.add(record.getData());
        }

        for (Record2 record : classified_records) {

            if (!known_data.contains(record.getData())) throw new UnknownDataException();
        }
    }

    /**
     * To be valid, all codes in the classified records must appear in the gold standard records.
     */
    private void checkClassifiedToValidCodes() throws InvalidCodeException {

        Set<String> valid_codes = new HashSet<>();

        for (Record2 record : gold_standard_records) {

            valid_codes.add(record.getClassification().getCode());
        }

        for (Record2 record : classified_records) {

            Classification2 classification = record.getClassification();
            if (classification != null && !valid_codes.contains(classification.getCode()))
                throw new InvalidCodeException();
        }
    }

    /**
     * To be valid, any string appearing in multiple records must be classified the same in each.
     */
    private void checkConsistentClassification(Bucket2 records) throws InconsistentCodingException {

        Map<String, String> classifications = new HashMap<>();

        for (Record2 record : records) {

            String data = record.getData();
            Classification2 classification = record.getClassification();

            if (classification != null) {
                String code = classification.getCode();

                if (classifications.containsKey(data)) {
                    if (!code.equals(classifications.get(data))) {
                        throw new InconsistentCodingException();
                    }
                } else {
                    classifications.put(data, code);
                }
            }
        }
    }

    /**
     * Calculates the true/false positive/negative counts for the bucket.
     */
    private void calculateCounts() throws UnclassifiedGoldStandardRecordException, UnknownDataException {

        for (Record2 record : gold_standard_records) {

            String code = record.getClassification().getCode();

            initCount(code, classification_counts);
            initCount(code, true_positive_counts);
            initCount(code, true_negative_counts);
            initCount(code, false_positive_counts);
            initCount(code, false_negative_counts);
        }

        for (Record2 record : classified_records) {
            updateCountsForRecord(record);
        }
    }

    private void updateCountsForRecord(Record2 record) throws UnknownDataException {

        Classification2 classification = record.getClassification();

        if (classification != null) {

            String asserted_code = classification.getCode();
            String real_code = findGoldStandardCode(record.getData());

            incrementCount(asserted_code, classification_counts);

            for (String this_code : classification_counts.keySet()) {

                if (truePositive(this_code, asserted_code, real_code)) {
                    incrementCount(this_code, true_positive_counts);
                }

                if (trueNegative(this_code, asserted_code, real_code)) {
                    incrementCount(this_code, true_negative_counts);
                }

                if (falsePositive(this_code, asserted_code, real_code)) {
                    incrementCount(this_code, false_positive_counts);
                }

                if (falseNegative(this_code, asserted_code, real_code)) {
                    incrementCount(this_code, false_negative_counts);
                }
            }
        }
    }

    private String findGoldStandardCode(String data) throws UnknownDataException {

        for (Record2 record : gold_standard_records) {

            if (record.getData().equals(data)) {
                return record.getClassification().getCode();
            }
        }

        throw new UnknownDataException();
    }

    private boolean truePositive(String this_code, String asserted_code, String real_code) {

        // True positive for this code if the record should have been classified as this, and it was.
        return classificationsMatch(this_code, real_code) && classificationsMatch(asserted_code, real_code);
    }

    private boolean trueNegative(String this_code, String asserted_code, String real_code) {

        // True negative for this code if the record shouldn't have been classified as this, and it wasn't.
        return !classificationsMatch(this_code, real_code) && !classificationsMatch(asserted_code, this_code);
    }

    private boolean falsePositive(String this_code, String asserted_code, String real_code) {

        // False positive for this code if the record shouldn't have been classified as this, but it was.
        return !classificationsMatch(this_code, real_code) && classificationsMatch(asserted_code, this_code);
    }

    private boolean falseNegative(String this_code, String asserted_code, String real_code) {

        // False negative for this code if the record should have been classified as this, but it wasn't.
        return classificationsMatch(real_code, this_code) && !classificationsMatch(asserted_code, this_code);
    }

    private void initCount(String code, Map<String, Integer> counts) {

        if (!counts.containsKey(code)) {
            counts.put(code, 0);
        }
    }

    private void incrementCount(String code, Map<String, Integer> counts) {

        counts.put(code, counts.get(code) + 1);
    }
}
