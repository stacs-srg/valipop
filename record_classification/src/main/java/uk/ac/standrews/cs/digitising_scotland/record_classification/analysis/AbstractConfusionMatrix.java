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

import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;

/**
 * General implementation of confusion matrix representing the effectiveness of a classification process.
 * The details of whether a classification result is considered to be correct is devolved to subclasses.
 *
 * @author Fraser Dunlop
 * @author Graham Kirby
 */
public abstract class AbstractConfusionMatrix implements ConfusionMatrix {

    private static final long serialVersionUID = -818147039422422286L;
    private final Map<String, Integer> classification_counts;
    private final Map<String, Integer> true_positive_counts;
    private final Map<String, Integer> false_positive_counts;
    private final Map<String, Integer> true_negative_counts;
    private final Map<String, Integer> false_negative_counts;

    private final Bucket classified_records;
    private final Bucket gold_standard_records;

    /**
     * Creates a confusion matrix representing the effectiveness of a classification process.
     *
     * @param classified_records the records that have been classified
     * @param gold_standard_records the gold standard records against which the classified records should be checked
     * @param consistent_coding_checker checker for consistent coding
     * @throws InvalidCodeException if a code in the classified records does not appear in the gold standard records
     * @throws UnknownDataException if a record in the classified records contains data that does not appear in the gold standard records
     * @throws InconsistentCodingException if there exist multiple gold standard records containing the same data and different classifications
     * @throws UnclassifiedGoldStandardRecordException if a record in the gold standard records is not classified
     */
    AbstractConfusionMatrix(final Bucket classified_records, final Bucket gold_standard_records, ConsistentCodingCleaner consistent_coding_checker) throws Exception {

        // TODO name ConsistentCodingCleaner isn't quite right when used as checker rather than cleaner

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

        consistent_coding_checker.clean(gold_standard_records);

        calculateCounts();
    }

    /**
     * Checks whether a classification result is considered to be correct.
     *
     * @param asserted_code the code asserted by the classifier
     * @param real_code the real code as defined in the gold standard records
     * @return true if the asserted code is considered to be correct
     */
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
        for (Record record : classified_records) {

            if (record.getClassification() != null)
                total++;
        }
        return total;
    }

    @Override
    public int getNumberOfTruePositives() {

        return sum(true_positive_counts);
    }

    @Override
    public int getNumberOfFalsePositives() {

        return sum(false_positive_counts);
    }

    @Override
    public int getNumberOfTrueNegatives() {

        return sum(true_negative_counts);
    }

    @Override
    public int getNumberOfFalseNegatives() {

        return sum(false_negative_counts);
    }

    @Override
    public int getNumberOfClasses() {

        return getClassificationCounts().size();
    }

    private int sum(Map<String, Integer> counts) {

        int sum = 0;
        for (int i : counts.values())
            sum += i;
        return sum;
    }

    /**
     * Checks whether all records in the gold standard records are classified.
     *
     * @throws UnclassifiedGoldStandardRecordException if they are not
     */
    private void checkGoldStandardDataIsClassified() throws UnclassifiedGoldStandardRecordException {

        for (Record record : gold_standard_records) {
            if (record.getClassification().equals(Classification.UNCLASSIFIED))
                throw new UnclassifiedGoldStandardRecordException();
        }
    }

    /**
     * Checks whether all data in the classified records appears in the gold standard records.
     *
     * @throws UnknownDataException if it does not
     */
    private void checkClassifiedDataIsInGoldStandard() throws UnknownDataException {

        Set<String> known_data = new HashSet<>();

        for (Record record : gold_standard_records) {

            known_data.add(record.getData());
        }

        for (Record record : classified_records) {

            String data = record.getData();
            if (!known_data.contains(data))
                throw new UnknownDataException("data: " + data + " is not in the gold standard data");
        }
    }

    /**
     * Checks whether all codes in the classified records appears in the gold standard records.
     *
     * @throws InvalidCodeException if they do not
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
     * Calculates the true/false positive/negative counts for the bucket.
     */
    private void calculateCounts() throws UnknownDataException {

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

        String asserted_code = classification.getCode();
        String real_code = findGoldStandardCode(record.getData());

        if (classification != Classification.UNCLASSIFIED)
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

        //        System.out.println("\nchecking false negative for: " + this_code + ", " + real_code + ", " + asserted_code);
        //        System.out.println("result: " + (classificationsMatch(real_code, this_code) && !classificationsMatch(asserted_code, this_code)));

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
