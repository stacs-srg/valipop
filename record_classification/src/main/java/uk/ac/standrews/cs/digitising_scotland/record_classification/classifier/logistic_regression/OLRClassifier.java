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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.logistic_regression;

import org.apache.mahout.classifier.*;
import org.apache.mahout.classifier.sgd.*;
import org.apache.mahout.math.*;
import org.apache.mahout.math.Vector;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * @author masih
 */
public abstract class OLRClassifier extends SingleClassifier {

    private static final long serialVersionUID = 5972187130211865595L;

    // why intercept is used: http://statistiksoftware.blogspot.nl/2013/01/why-we-need-intercept.html
    private static final int INTERCEPT_OFFSET = 1;
    private static final int INTERCEPT_VECTOR_INDEX = 0;
    private static final int INTERCEPT_INITIAL_VALUE = 1;

    private static final Logger LOGGER = Logger.getLogger(OLRClassifier.class.getName());

    private final AtomicInteger next_token_index = new AtomicInteger(INTERCEPT_OFFSET);
    private final AtomicInteger next_classification_index = new AtomicInteger();
    private final AtomicLong next_training_record_id = new AtomicLong();
    private final ConcurrentHashMap<String, Integer> classification_to_index;
    private final ConcurrentHashMap<Integer, String> index_to_classification;
    private final ConcurrentHashMap<String, Integer> token_to_index;
    private transient AbstractVectorClassifier classifier;

    public OLRClassifier() {

        index_to_classification = new ConcurrentHashMap<>();
        classification_to_index = new ConcurrentHashMap<>();
        token_to_index = new ConcurrentHashMap<>();
    }

    @Override
    protected Classification doClassify(final String unclassified) {

        final Classification classification;

        if (!isTrained()) {
            classification = Classification.UNCLASSIFIED;
        }
        else {

            final TokenList tokens = new TokenList(unclassified);
            final Vector vector = toFeatureVector(tokens);
            final Vector classification_probability_vector = classifier.classifyFull(vector);
            final int most_probable_classification_index = classification_probability_vector.maxValueIndex();

            if (index_to_classification.containsKey(most_probable_classification_index)) {
                final String classification_code = index_to_classification.get(most_probable_classification_index);
                final double probability = classification_probability_vector.get(most_probable_classification_index);
                classification = new Classification(classification_code, tokens, probability, null);
            }
            else {
                classification = Classification.UNCLASSIFIED;
            }
        }
        return classification;
    }

    private boolean isTrained() { return classifier != null; }

    @Override
    public void trainModel(final Bucket training_records) {

        requireUntrainedModel();
        index(training_records);
        classifier = train(toOnlineLainingRecords(training_records));
    }

    protected List<OnlineTrainingRecord> toOnlineLainingRecords(final Bucket training_records) {

        return training_records.parallelStream().map(this::toOnlineTrainingRecord).collect(Collectors.toList());
    }

    protected abstract AbstractVectorClassifier train(final List<OnlineTrainingRecord> training_records);

    protected void requireUntrainedModel() {

        if (isTrained()) { // FIXME allow extended training.
            throw new UnsupportedOperationException("already trained, further training upon existing trained model is not implemented yet");
        }
    }

    protected void index(final Bucket training_records) {

        training_records.parallelStream().map(Record::getClassification).forEach(classification -> {

            final String original_code = classification.getCode();
            indexClassificationCode(original_code);

            final TokenList tokens = classification.getTokenList();
            tokens.forEach(this::indexToken);
        });
    }

    private Integer indexClassificationCode(String code) {

        return classification_to_index.computeIfAbsent(code, key -> {
            final int value = next_classification_index.getAndIncrement();

            final String other_code = index_to_classification.putIfAbsent(value, key);

            if (other_code != null) {
                throw new IllegalStateException(String.format("inconsistent index for codes %s and %s", other_code, code));
            }

            return value;
        });
    }

    @Override
    protected void clearModel() {

        classifier = null;
        token_to_index.clear();
        classification_to_index.clear();
        index_to_classification.clear();
        next_token_index.set(INTERCEPT_OFFSET);
        next_classification_index.set(0);
        next_training_record_id.set(0);
    }

    protected Vector toFeatureVector(final TokenList tokens) {

        final Vector vector = new RandomAccessSparseVector(countFeatures());

        setIntercept(vector);

        tokens.forEach(token -> {
            if (isTokenIndexed(token)) {
                final Integer index = getIndexToken(token);
                vector.incrementQuick(index, 1);
            }
        });
        return vector;
    }

    int countFeatures() {return countUniqueTokens() + INTERCEPT_OFFSET;}

    protected int countUniqueTokens() {return token_to_index.size();}

    private void setIntercept(final Vector vector) {vector.setQuick(INTERCEPT_VECTOR_INDEX, INTERCEPT_INITIAL_VALUE);}

    private boolean isTokenIndexed(final String token) {

        return token_to_index.containsKey(token);
    }

    private Integer getIndexToken(String token) {

        return token_to_index.get(token);
    }

    protected int countCategories() {return classification_to_index.size();}

    private Integer indexToken(String token) {

        return token_to_index.computeIfAbsent(token, key -> next_token_index.getAndIncrement());
    }

    private OnlineTrainingRecord toOnlineTrainingRecord(Record record) {

        final Classification classification = record.getClassification();
        final TokenList tokens = classification.getTokenList();
        final String original_code = classification.getCode();

        final int code_index = classification_to_index.get(original_code);
        final Vector feature_vector = toFeatureVector(tokens);
        return new OnlineTrainingRecord(code_index, feature_vector);
    }

    @Override
    public String getDescription() {

        return "Classifies using Mahout Online Logistic Regression Classifier.";
    }

    protected class OnlineTrainingRecord {

        protected final long id;
        protected final int code;
        protected final Vector feature_vector;

        private OnlineTrainingRecord(int code, Vector feature_vector) {

            this.code = code;
            this.feature_vector = feature_vector;
            id = next_training_record_id.getAndIncrement();
        }
    }
}
