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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Uses a lookup table to return matches as classifications.
 *
 * @author frjd2, jkc25
 */
public class ExactMatchClassifier implements Classifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactMatchClassifier.class);
    private Map<String, Set<Classification>> lookupTable;
    private String modelFileName = "target/lookupTable";
    ObjectMapper mapper;

    /**
     * Creates a new {@link ExactMatchClassifier} and creates an empty lookup table.
     */
    public ExactMatchClassifier() {

        this.lookupTable = new HashMap<>();
        mapper = new ObjectMapper();
    }

    public void train(final Bucket bucket) throws Exception {

        for (Record record : bucket) {
            addRecordToLookupTable(record);
        }

        writeModel(modelFileName);
    }

    /**
     * Can be used to overwrite the default file path for model writing.
     *
     * @param modelFileName new model path
     */
    public void setModelFileName(final String modelFileName) {

        this.modelFileName = modelFileName;
    }

    /**
     * Adds each gold standard {@link Classification} in the records to the lookupTable.
     *
     * @param record to add
     */
    private void addRecordToLookupTable(final Record record) {

        final Set<Classification> goldStandardCodes = record.getOriginalData().getGoldStandardClassifications();
        String concatDescription = getConcatenatedDescription(goldStandardCodes);
        List<String> blacklist = new ArrayList<>();
        addToLookup(lookupTable, goldStandardCodes, concatDescription, blacklist);
    }

    protected void addToLookup(final Map<String, Set<Classification>> lookup, final Set<Classification> goldStandardCodes, final String concatDescription, final List<String> blacklist) {

        // Not clear what blacklist is for. Can't see how it works with passed in list always being empty.

        if (!blacklist.contains(concatDescription)) {
            if (!lookup.containsKey(concatDescription)) {

                Set<Classification> editClassification = changeConfidences(goldStandardCodes);
                lookup.put(concatDescription, editClassification);
            } else if (!goldStandardCodes.equals(lookup.get(concatDescription))) {
                blacklist.add(concatDescription);
                lookup.remove(concatDescription);
                LOGGER.info(concatDescription + " removed");
            }
        }
    }

    private Set<Classification> changeConfidences(final Set<Classification> goldStandardCodes) {

        Set<Classification> editedSet = new HashSet<>();
        for (Classification classification : goldStandardCodes) {
            // Make new code witj -1 as confidence so we can tell where classifications came from later.
            // -2 means exact match, -1 means cache classifier
            Classification editClassification = new Classification(classification.getCode(), classification.getTokenSet(), -2.0);
            editedSet.add(editClassification);
        }
        return editedSet;
    }

    private String getConcatenatedDescription(final Set<Classification> goldStandardCodes) {

        boolean isFirst = true;
        String concat = "";
        for (Classification classification : goldStandardCodes) {
            if (isFirst) {
                concat += classification.getTokenSet().toString();
                isFirst = false;
            } else {
                concat += ", " + classification.getTokenSet().toString();
            }
        }

        return concat;
    }

    /**
     * Writes model to file. File name is fileName.ser
     *
     * @param fileName name of file to write model to
     * @throws IOException if model location cannot be read
     */
    public void writeModel(final String fileName) throws IOException {

        mapper.writeValue(new File(fileName + ".json"), lookupTable);
    }

    protected void readModel(final String fileName) throws IOException {

        lookupTable = mapper.readValue(new File(fileName + ".json"), new TypeReference<Map<String, Set<Classification>>>() {
        });
    }

    public void loadModelFromFile() throws IOException {

        readModel(modelFileName);
    }

    /**
     * Classifies a {@link TokenSet} to a set of {@link Classification}s using the classifiers lookup table.
     *
     * @param tokenSet to classify
     * @return Set<CodeTripe> code triples from lookup table
     * @throws IOException Indicates an I/O error
     */
    public Set<Classification> classify(final TokenSet tokenSet) throws IOException {

        return lookupTable.get(tokenSet);
    }

    private Set<Classification> setConfidenceLevels(final Set<Classification> result, final double i) {

        Set<Classification> newResults = new HashSet<>();
        for (Classification codeTriple : result) {
            Classification newCodeT = new Classification(codeTriple.getCode(), codeTriple.getTokenSet(), i);
            newResults.add(newCodeT);
        }
        return newResults;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((lookupTable == null) ? 0 : lookupTable.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExactMatchClassifier other = (ExactMatchClassifier) obj;
        if (lookupTable == null) {
            if (other.lookupTable != null) {
                return false;
            }
        } else if (!lookupTable.equals(other.lookupTable)) {
            return false;
        }
        return true;
    }
}
