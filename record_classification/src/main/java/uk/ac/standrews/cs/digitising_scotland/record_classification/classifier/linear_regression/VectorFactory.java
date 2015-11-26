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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression;

import org.la4j.Vector;
import org.la4j.vector.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.util.*;

/**
 * @author Fraser Dunlop
 */
public class VectorFactory implements Serializable {

    private static final long serialVersionUID = 5369887941319861994L;

    private CodeIndexer index;

    private SimpleVectorEncoder vectorEncoder;

    /**
     * Needed for JSON deserialization.
     */
    public VectorFactory() {

    }

    public VectorFactory(final Bucket bucket) {

        index = new CodeIndexer(bucket);
        vectorEncoder = new SimpleVectorEncoder();
        updateDictionary(bucket);
    }

    /**
     * Updates the dictionary with the tokens for all the records in the given bucket.
     *
     * @param bucket the bucket
     */
    protected void updateDictionary(final Bucket bucket) {

        for (Record record : bucket) {

            record.getClassification().getTokenList().forEach(vectorEncoder::updateDictionary);
        }
    }

    protected int numberOfDistinctTokens() {

        return vectorEncoder.getDictionarySize();
    }

    protected int numberOfDistinctClassifications() {

        return index.codeMapSize();
    }

    /**
     * Creates a {@link NamedVector} from the cleaned description of a {@link Record}.
     * If a gold standard coding exists this will be used for the name of the vector.
     * If no gold standard then "noGoldStandard" will be used.
     * A List<NamedVector> is returned as the record may have more than one line in the original description.
     *
     * @param record Record to generate vector for.
     * @return List<NamedVector> List of {@link NamedVector} for this record
     */
    protected List<NamedVector> generateVectorsFromRecord(final Record record) {

        List<NamedVector> vectors = new ArrayList<>();

        if (record.getClassification() != Classification.UNCLASSIFIED) {
            vectors.addAll(createNamedVectorsWithGoldStandardCodes(record));
        }
        else {
            vectors.addAll(createUnNamedVectorsFromDescription(new TokenList(record.getData())));
        }
        return vectors;
    }

    protected NamedVector createNamedVectorFromString(final TokenList token_list, final String name) {

        Vector vector = createVectorFromString(token_list);
        return new NamedVector(vector, name);
    }

    protected CodeIndexer getCodeIndexer() {

        return index;
    }

    private Collection<? extends NamedVector> createUnNamedVectorsFromDescription(final TokenList description) {

        List<NamedVector> vectorList = new ArrayList<>();

        vectorList.add(new NamedVector(createVectorFromString(description), "noGoldStandard"));

        return vectorList;
    }

    private List<NamedVector> createNamedVectorsWithGoldStandardCodes(final Record record) {

        List<NamedVector> vectors = new ArrayList<>();

        Classification codeTriple = record.getClassification();
        Integer id = index.getID(codeTriple.getCode());
        vectors.add(createNamedVectorFromString(codeTriple.getTokenList(), id.toString()));

        return vectors;
    }

    private Vector createVectorFromString(final TokenList description) {

        Vector vector = SparseVector.zero(numberOfDistinctTokens());
        addFeaturesToVector(vector, description);
        return vector;
    }

    private void addFeaturesToVector(final Vector vector, final TokenList token_list) {

        for (String token : token_list) {
            vectorEncoder.addToVector(token, vector);
        }
    }

    public static class NamedVector implements Cloneable {

        protected final Vector vector;
        protected final String name;

        public NamedVector(Vector vector, String name) {

            this.vector = vector;
            this.name = name;
        }

        @Override
        protected NamedVector clone() {

            return new NamedVector(vector.copy(), name);
        }
    }
}
