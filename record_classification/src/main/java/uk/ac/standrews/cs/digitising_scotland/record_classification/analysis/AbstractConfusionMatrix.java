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
package uk.ac.standrews.cs.digitising_scotland.record_classification.analysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InconsistentCodingException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InvalidCodeException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.UnclassifiedGoldStandardRecordException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.UnknownDataException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ConfusionMatrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Fraser Dunlop
 * @author Graham Kirby
 */
public abstract class AbstractConfusionMatrix implements ConfusionMatrix {

    protected final Map<String, Integer> classification_counts;
    protected final Map<String, Integer> true_positive_counts;
    protected final Map<String, Integer> false_positive_counts;
    protected final Map<String, Integer> true_negative_counts;
    protected final Map<String, Integer> false_negative_counts;

    protected final Bucket classified_records;
    protected final Bucket gold_standard_records;

    public AbstractConfusionMatrix(final Bucket classified_records, final Bucket gold_standard_records, boolean check_classification_consistency) throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

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

        if (check_classification_consistency) {
            checkConsistentClassificationOfGoldStandardRecords();
        }

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
    public int getTotalNumberOfClassifications() {

        int total = 0;
        for (Record record : classified_records) {

            if (record.getClassification() != null) total++;
        }
        return total;
    }

    /**
     * To be valid, all records in the gold standard records must be classified.
     */
    private void checkGoldStandardDataIsClassified() throws UnclassifiedGoldStandardRecordException {

        for (Record record : gold_standard_records) {
            if (record.getClassification() == Classification.UNCLASSIFIED) throw new UnclassifiedGoldStandardRecordException();
        }
    }

    /**
     * To be valid, all data in the classified records must appear in the gold standard records.
     */
    private void checkClassifiedDataIsInGoldStandard() throws UnknownDataException {

        Set<String> known_data = new HashSet<>();

        for (Record record : gold_standard_records) {

            known_data.add(record.getData());
        }

        for (Record record : classified_records) {

            String data = record.getData();
            if (!known_data.contains(data)) throw new UnknownDataException("data: " + data + " is not in the gold standard data");
        }
    }

    /**
     * To be valid, all codes in the classified records must appear in the gold standard records.
     */
    private void checkClassifiedToValidCodes() throws InvalidCodeException {

        Set<String> valid_codes = new HashSet<>();
        valid_codes.add(Classification.UNCLASSIFIED.getCode());

        for (Record record : gold_standard_records) {

            valid_codes.add(record.getClassification().getCode());
        }

        for (Record record : classified_records) {

            Classification classification = record.getClassification();

            if (!valid_codes.contains(classification.getCode())) {
                throw new InvalidCodeException();
            }
        }
    }

    /**
     * To be valid, any string appearing in multiple records must be classified the same in each.
     */
    private void checkConsistentClassificationOfGoldStandardRecords() throws InconsistentCodingException {

        Map<String, String> classifications = new HashMap<>();

        for (Record record : gold_standard_records) {

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
    }

    /**
     * Calculates the true/false positive/negative counts for the bucket.
     */
    private void calculateCounts() throws UnclassifiedGoldStandardRecordException, UnknownDataException {

        initCounts();
        updateCounts();
    }

    private void initCounts() {

        for (Record record : gold_standard_records) {
            initCounts(record.getClassification().getCode());
        }
    }

    private void initCounts(String code) {

        initCount(code, classification_counts);
        initCount(code, true_positive_counts);
        initCount(code, true_negative_counts);
        initCount(code, false_positive_counts);
        initCount(code, false_negative_counts);
    }

    private void updateCounts() throws UnknownDataException {

        for (Record record : classified_records) {
            updateCountsForRecord(record);
        }
    }

    private void updateCountsForRecord(Record record) throws UnknownDataException {

        Classification classification = record.getClassification();

        if (classification != Classification.UNCLASSIFIED) {

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

        for (Record record : gold_standard_records) {

            if (record.getData().equals(data)) {
                return record.getClassification().getCode();
            }
        }

        throw new UnknownDataException("couldn't find gold standard code for data: " + data);
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
