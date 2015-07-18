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

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public VectorFactory() {}

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
    public void updateDictionary(final Bucket bucket) {

        for (Record record : bucket) {

            // TODO replace with lower case cleaner.

            for (String token : record.getClassification().getTokenSet()) {

                String descriptionLower = token.toLowerCase();
                vectorEncoder.updateDictionary(descriptionLower);
            }
        }

//        OLR.setDefaultNumberOfFeatures(vectorEncoder.getDictionarySize());
    }

    protected int dictionarySize() {

        return vectorEncoder.getDictionarySize();
    }

    protected int codeMapSize() {

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
    public List<NamedVector> generateVectorsFromRecord(final Record record) {

        List<NamedVector> vectors = new ArrayList<>();

        if (record.getClassification() != Classification.UNCLASSIFIED) {
            vectors.addAll(createNamedVectorsWithGoldStandardCodes(record));
        } else {
            vectors.addAll(createUnNamedVectorsFromDescription(new TokenList(record.getData())));
        }
        return vectors;
    }

    /**
     * Creates a new Vector object.
     *
     * @param description the description
     * @return the collection<? extends named vector>
     */
    private Collection<? extends NamedVector> createUnNamedVectorsFromDescription(final TokenList description) {

        List<NamedVector> vectorList = new ArrayList<>();

        vectorList.add(new NamedVector(createVectorFromString(description), "noGoldStandard"));

        return vectorList;
    }

    /**
     * Creates a new Vector object.
     *
     * @param record the record
     * @return the list< named vector>
     */
    private List<NamedVector> createNamedVectorsWithGoldStandardCodes(final Record record) {

        List<NamedVector> vectors = new ArrayList<>();

        Classification codeTriple = record.getClassification();
        Integer id = index.getID(codeTriple.getCode());
        vectors.add(createNamedVectorFromString(codeTriple.getTokenSet(), id.toString()));

        return vectors;
    }

    /**
     * Creates a vector from a string using {@link SimpleVectorEncoder}
     * to encode the tokens and StandardTokenizerIterable to
     * tokenize the string.
     *
     * @param description the string to vectorize
     * @return a vector encoding of the string
     */
    public Vector createVectorFromString(final TokenList description) {

        Vector vector = new RandomAccessSparseVector(getNumberOfFeatures());
        addFeaturesToVector(vector, description);
        return vector;
    }

    /**
     * Creates a named vector from a string using {@link SimpleVectorEncoder}
     * to encode the tokens and {StandardTokenizerIterable} to
     * tokenize the string.
     *
     * @param token_list the string to vectorize
     * @param name       name
     * @return a vector encoding of the string
     */
    public NamedVector createNamedVectorFromString(final TokenList token_list, final String name) {

        Vector vector = createVectorFromString(token_list);
        return new NamedVector(vector, name);
    }

    /**
     * Adds the features to vector.
     *
     * @param vector     the vector
     * @param token_list the description
     */
    private void addFeaturesToVector(final Vector vector, final TokenList token_list) {

        for (String token : token_list) {
            vectorEncoder.addToVector(token, vector);
        }
    }

    /**
     * Gets the code indexer that was used to construct this vector factory.
     *
     * @return the code indexer
     */
    public CodeIndexer getCodeIndexer() {

        return index;
    }

    /**
     * Gets the number of features.
     *
     * @return the number of features
     */
    public int getNumberOfFeatures() {

        return vectorEncoder.getDictionarySize();
    }
}
