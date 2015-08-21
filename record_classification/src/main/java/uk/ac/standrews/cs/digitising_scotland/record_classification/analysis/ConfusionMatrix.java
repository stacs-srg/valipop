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

import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Checker;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InvalidCodeException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.UnclassifiedGoldStandardRecordException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.UnknownDataException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tools.Formatting;
import uk.ac.standrews.cs.util.tools.InfoLevel;
import uk.ac.standrews.cs.util.tools.Logging;

import java.util.*;

/**
 * General implementation of confusion matrix representing the effectiveness of a classification process.
 * The details of whether a classification result is considered to be correct is devolved to subclasses.
 *
 * @author Fraser Dunlop
 * @author Graham Kirby
 */
public abstract class ConfusionMatrix {

    private static final long serialVersionUID = -818147039422422286L;
    private final Map<String, Integer> classification_counts;
    private final Map<String, Integer> true_positive_counts;
    private final Map<String, Integer> false_positive_counts;
    private final Map<String, Integer> true_negative_counts;
    private final Map<String, Integer> false_negative_counts;

    private Bucket classified_records;
    private Bucket gold_standard_records;

    private int number_of_records;
    private int total_number_of_classifications = 0;
    private int total_number_of_gold_standard_classifications = 0;
    private int total_number_of_records_with_correct_number_of_classifications = 0;


    /**
     * Creates a confusion matrix representing the effectiveness of a classification process.
     *
     * @param classified_records    the records that have been classified
     * @param gold_standard_records the gold standard records against which the classified records should be checked
     * @param checker               checker for consistent coding
     * @throws InvalidCodeException                    if a code in the classified records does not appear in the gold standard records
     * @throws UnknownDataException                    if a record in the classified records contains data that does not appear in the gold standard records
     * @throws UnclassifiedGoldStandardRecordException if a record in the gold standard records is not classified
     */
    ConfusionMatrix(final Bucket classified_records, final Bucket gold_standard_records, Checker checker) {

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

        if (checker != null && !checker.test(Arrays.asList(gold_standard_records))) {
            throw new RuntimeException("check failed");
        }

        calculateCounts();
    }


    /**
     * Creates a confusion matrix representing the effectiveness of a classification process.
     * This version considers multiple classifications.
     *
     * @param classified_records    the records that have been classified
     * @param gold_standard_records the gold standard records against which the classified records should be checked
     * @param checker               checker for consistent coding
     * @throws InvalidCodeException                    if a code in the classified records does not appear in the gold standard records
     * @throws UnknownDataException                    if a record in the classified records contains data that does not appear in the gold standard records
     * @throws UnclassifiedGoldStandardRecordException if a record in the gold standard records is not classified
     */
    public ConfusionMatrix(DataSet classified_records, DataSet gold_standard_records, Checker checker) {

        classification_counts = new HashMap<>();
        true_positive_counts = new HashMap<>();
        true_negative_counts = new HashMap<>();
        false_positive_counts = new HashMap<>();
        false_negative_counts = new HashMap<>();

        calculateCounts(classified_records, gold_standard_records);
    }

    /**
     * Checks whether a classification result is considered to be correct.
     *
     * @param asserted_code the code asserted by the classifier
     * @param real_code     the real code as defined in the gold standard records
     * @return true if the asserted code is considered to be correct
     */
    protected abstract boolean classificationsMatch(String asserted_code, String real_code);

    /**
     * Returns a map from classification class to the number of records classified as that class.
     *
     * @return the map
     */
    public Map<String, Integer> getClassificationCounts() {

        return classification_counts;
    }

    /**
     * Returns a map from classification class to the number of true positives for that class.
     * That is, the number of records that were classified as that class, and really were of that class.
     *
     * @return the map
     */
    public Map<String, Integer> getTruePositiveCounts() {

        return true_positive_counts;
    }

    /**
     * Returns a map from classification class to the number of false positives for that class.
     * That is, the number of records that were classified as that class, but were not of that class.
     *
     * @return the map
     */
    public Map<String, Integer> getFalsePositiveCounts() {

        return false_positive_counts;
    }

    /**
     * Returns a map from classification class to the number of false negatives for that class.
     * That is, the number of records that were not classified as that class, but actually were of that class.
     *
     * @return the map
     */
    public Map<String, Integer> getFalseNegativeCounts() {

        return false_negative_counts;
    }

    /**
     * Returns a map from classification class to the number of true negatives for that class.
     * That is, the number of records that were not classified as that class, and really were not of that class.
     *
     * @return the map
     */
    public Map<String, Integer> getTrueNegativeCounts() {

        return true_negative_counts;
    }

    /**
     * Returns the total number of classifications.
     *
     * @return the number of classifications
     */
    public int getNumberOfClassifications() {

        return classified_records.size();
    }

    /**
     * Returns the total number of true positives.
     *
     * @return the number of true positives
     */
    public int getNumberOfTruePositives() {

        return sum(true_positive_counts);
    }

    /**
     * Returns the total number of false positives.
     *
     * @return the number of false positives
     */
    public int getNumberOfFalsePositives() {

        return sum(false_positive_counts);
    }

    /**
     * Returns the total number of true negatives.
     *
     * @return the number of true negatives
     */
    public int getNumberOfTrueNegatives() {

        // Don't count unclassified decisions in true negatives.

        return sum(true_negative_counts) - true_negative_counts.get(Classification.UNCLASSIFIED.getCode());
    }

    /**
     * Returns the total number of false negatives.
     *
     * @return the number of false negatives
     */
    public int getNumberOfFalseNegatives() {

        return sum(false_negative_counts);
    }

    /**
     * Returns the total number of classes present in the classified data.
     *
     * @return the number of classifications
     */
    public int getNumberOfClasses() {

        return getClassificationCounts().size();
    }

    private int sum(Map<String, Integer> counts) {

        int sum = 0;
        for (int i : counts.values()) {
            sum += i;
        }
        return sum;
    }

    /**
     * Checks whether all records in the gold standard records are classified.
     *
     * @throws UnclassifiedGoldStandardRecordException if they are not
     */
    private void checkGoldStandardDataIsClassified() {

        for (Record record : gold_standard_records) {
            if (record.getClassification().isUnclassified()) {
                throw new UnclassifiedGoldStandardRecordException();
            }
        }
    }

    /**
     * Checks whether all data in the classified records appears in the gold standard records.
     *
     * @throws UnknownDataException if it does not
     */
    private void checkClassifiedDataIsInGoldStandard() {

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
    private void checkClassifiedToValidCodes() {

        Set<String> valid_codes = new HashSet<>();
        valid_codes.add(Classification.UNCLASSIFIED.getCode());

        for (Record record : gold_standard_records) {

            valid_codes.add(record.getClassification().getCode());
        }

        for (Record record : classified_records) {

            Classification classification = record.getClassification();

            if (!valid_codes.contains(classification.getCode())) {
                throw new InvalidCodeException("unknown code: " + classification.getCode());
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

    private void calculateCounts(DataSet classified_records, DataSet gold_standard_records) {

        for (List<String> record : gold_standard_records.getRecords()) {
            initCounts(record);
        }
        initCounts(Classification.UNCLASSIFIED.getCode());

        for (List<String> record : classified_records.getRecords()) {

            updateCountsForRecord(record, gold_standard_records);
        }

        number_of_records = classified_records.getRecords().size();
    }

    private void initCounts() {

        for (Record record : gold_standard_records) {
            initCounts(record.getClassification().getCode());
        }
        initCounts(Classification.UNCLASSIFIED.getCode());
    }

    private void initCounts(String code) {

        initCount(code, classification_counts);
        initCount(code, true_positive_counts);
        initCount(code, true_negative_counts);
        initCount(code, false_positive_counts);
        initCount(code, false_negative_counts);
    }

    private void initCounts(List<String> record) {

        for (int i = 2; i < record.size(); i++) {
            String code = record.get(i);
            if (code.length() > 0) {
                initCounts(code);
            }
        }
    }

    private void updateCounts() throws UnknownDataException {

        for (Record record : classified_records) {

            Classification classification = record.getClassification();

            String asserted_code = classification.getCode();
            String real_code = findGoldStandardCode(record.getData());

            updateCountsForRecord(asserted_code, real_code);

            Logging.output(InfoLevel.VERBOSE, record.getOriginalData() + "\t" + real_code + "\t" + classification.getCode() + "\t" + Formatting.format(classification.getConfidence(), 2) + "\t" + classification.getDetail());
        }
    }

    private void updateCountsForRecord(List<String> classified_record, DataSet gold_standard_records) {

        List<String> classifier_codes = extractCodes(classified_record);
        List<String> gold_standard_codes = findGoldStandardCodes(classified_record, gold_standard_records);

        for (String possible_code : classification_counts.keySet()) {

            if (classifier_codes.contains(possible_code)) {

                incrementCount(possible_code, classification_counts);

                if (gold_standard_codes.contains(possible_code)) {

                    incrementCount(possible_code, true_positive_counts);

                } else {

                    incrementCount(possible_code, false_positive_counts);

                }
            } else {

                if (gold_standard_codes.contains(possible_code)) {

                    incrementCount(possible_code, false_negative_counts);

                } else {

                    incrementCount(possible_code, true_negative_counts);
                }
            }
        }

        int number_of_classifications = classifier_codes.size();
        if (classifier_codes.contains(Classification.UNCLASSIFIED.getCode())) {
            number_of_classifications--;
        }

        total_number_of_classifications += number_of_classifications;

        total_number_of_gold_standard_classifications += gold_standard_codes.size();

        if (number_of_classifications == gold_standard_codes.size()) {
            total_number_of_records_with_correct_number_of_classifications++;
        }
    }

    private List<String> findGoldStandardCodes(List<String> classified_record, DataSet gold_standard_records) {

        String data = classified_record.get(1);
        for (List<String> gold_standard_record : gold_standard_records.getRecords()) {
            if (gold_standard_record.get(1).equals(data)) return extractCodes(gold_standard_record);
        }
        return new ArrayList<>();
    }

    private List<String> extractCodes(List<String> classified_record) {

        List<String> codes = new ArrayList<>();
        boolean found_code = false;
        for (String code : classified_record.subList(2, classified_record.size())) {
            if (code.length() > 0) {
                codes.add(code);
                found_code = true;
            }
        }
        if (!found_code) codes.add(Classification.UNCLASSIFIED.getCode());
        return codes;
    }

    private void updateCountsForRecord(String asserted_code, String real_code) throws UnknownDataException {

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

    public double averageClassificationsPerRecord() {

        return ((double) total_number_of_classifications) / number_of_records;
    }

    public double actualAverageClassificationsPerRecord() {

        return ((double) total_number_of_gold_standard_classifications) / number_of_records;
    }

    public double proportionOfRecordsWithCorrectNumberOfClassifications() {

        return ((double) total_number_of_records_with_correct_number_of_classifications) / number_of_records;
    }
}
