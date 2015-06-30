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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier;

import old.record_classification_old.datastructures.vectors.CustomVectorWriter;
import old.record_classification_old.tools.Utils;
import old.record_classification_old.tools.configuration.MachineLearningConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.mahout.classifier.ClassifierResult;
import org.apache.mahout.classifier.naivebayes.AbstractNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.BayesUtils;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.vectorizer.encoders.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenSet;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author jkc25
 */
public class NaiveBayesClassifier extends AbstractClassifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaiveBayesClassifier.class);

    /**
     * The {@link Configuration}.
     */
    private Configuration conf;

    /**
     * The {@link FileSystem}.
     */
    private FileSystem fs;

    /**
     * The properties.
     */
    private Properties properties = MachineLearningConfiguration.getDefaultProperties();

    /**
     * The model.
     */
    private NaiveBayesModel model = null;

    private static final double STATIC_CONFIDENCE = 0.1;

    VectorFactory vectorFactory;

    CodeIndexer index;

    /**
     * Create Naive Bayes classifier with default properties.
     */
    public NaiveBayesClassifier() {

        index = new CodeIndexer();
        vectorFactory = new VectorFactory();
        try {
            conf = new Configuration();
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Create Naive Bayes classifier with custom properties.
     *
     * @param customProperties Customised properties file.
     */
    public NaiveBayesClassifier(final String customProperties) {

        MachineLearningConfiguration mlc = new MachineLearningConfiguration();
        properties = mlc.extendDefaultProperties(customProperties);
    }

    public void train(final Bucket trainingBucket) {

        index = new CodeIndexer(trainingBucket);
        vectorFactory = new VectorFactory(trainingBucket, index);
        File trainingVectorsDirectory = new File(properties.getProperty("trainingVectorsDirectory"));
        String trainingVectorFile = trainingVectorsDirectory.getAbsolutePath() + "/part-m-00000";
        String naiveBayesModelPath = properties.getProperty("naiveBayesModelPath");

        CustomVectorWriter vectorWriter = null;
        try { // temporary hack.
            vectorWriter = createVectorWriter(trainingVectorFile);
            writeTrainingVectorsToDisk(trainingBucket, vectorWriter);
            vectorWriter.close();
            writeLabelIndex(trainingBucket);
            model = trainNaiveBayes(trainingVectorFile, naiveBayesModelPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Classification classify(String data) {

        TokenSet tokenSet = new TokenSet(data);
        NamedVector vector = vectorFactory.createNamedVectorFromString(tokenSet.toString(), "unknown");
        int classificationID = 0;
        try {
            classificationID = getClassifier().classifyFull(vector).maxValueIndex();
        } catch (IOException e) {
            e.printStackTrace(); // temporary hack.
        }
        String code = vectorFactory.getCodeIndexer().getCode(classificationID);
        // double confidence =
        // Math.exp(getClassifier().logLikelihood(classificationID, vector));
        // TODO THIS WONT WORK - Baysian classifier don't give real confidence measures - Need to fudge it

        return new Classification(code, tokenSet, STATIC_CONFIDENCE);
    }

    /**
     * Gets the classification.
     *
     * @param record      the record
     * @param namedVector the named vector
     * @return the classification
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Classification getClassification(final Record record, final NamedVector namedVector) throws IOException {

        Dictionary dictionary = buildDictionaryFromLabelMap(properties.getProperty("labelindex"));

        AbstractNaiveBayesClassifier classifier = getClassifier();

        Vector resultVector = classifier.classifyFull(namedVector);

        ClassifierResult cr = getClassifierResult(resultVector, dictionary);

        String code = getCode(resultVector.maxValueIndex());

        double confidence = getConfidence(cr.getLogLikelihood());

        return new Classification(code, new TokenSet(record.getData()), confidence);
    }

    /**
     * Write label index.
     *
     * @param trainingBucket the training bucket
     */
    private void writeLabelIndex(final Bucket trainingBucket) {

        Collection<String> seen = new HashSet<String>();

        for (Record record : trainingBucket) {
            Classification codeTriple2 = record.getClassification();
            String theLabel = codeTriple2.getCode();
            if (!seen.contains(theLabel)) {
                Utils.writeToFile(theLabel + ", ", "labelindex.csv", true);
                seen.add(theLabel);
            }
        }
    }

    /**
     * Writes each vector in the bucket using {@link CustomVectorWriter}
     * vectorWriter.
     *
     * @param bucket       {@link Bucket} containing the vectors you want to write.
     * @param vectorWriter {@link CustomVectorWriter} instantiated with output path and
     *                     types.
     * @throws IOException IO Error
     */
    private void writeTrainingVectorsToDisk(final Bucket bucket, final CustomVectorWriter vectorWriter) throws IOException {

        for (Record record : bucket) {

            List<NamedVector> vectors = vectorFactory.generateVectorsFromRecord(record);
            for (Vector vector : vectors) {
                vectorWriter.write(vector);
            }
        }
    }

    /**
     * Creates a {@link CustomVectorWriter} to write vectors to disk.
     * {@link CustomVectorWriter} writes out {@link Text},
     * {@link VectorWritable} pairs into the file specified.
     *
     * @param trainingVectorLocation location to write vectors to.
     * @return {@link Writer} with trainingVectorLocation as it's output path
     * and {@link Text}, {@link VectorWritable} as its types.
     * @throws IOException I/O Error
     */
    private CustomVectorWriter createVectorWriter(final String trainingVectorLocation) throws IOException {

        Writer writer = new Writer(fs, conf, new Path(trainingVectorLocation), Text.class, VectorWritable.class);
        return new CustomVectorWriter(writer);
    }

    /**
     * Builds a {@link ClassifierResult} from a result vector and the dictionary
     * of interger/classifications.
     *
     * @param result     result vector from the Naive Bayes Classifier.
     * @param dictionary Dictionary containing the mapping of Integers to
     *                   classifications (labels)
     * @return {@link ClassifierResult} containing the details of the
     * classification
     */
    private ClassifierResult getClassifierResult(final Vector result, final Dictionary dictionary) {

        int categoryOfClassification = result.maxValueIndex();
        return new ClassifierResult(dictionary.values().get(categoryOfClassification));
    }

    /**
     * Builds a {@link StandardNaiveBayesClassifier}.
     *
     * @return StandardNaiveBayesClassifier built from the model stored in
     * "target/naiveBayesModelPath"
     * @throws IOException if the model cannot be read
     */
    private AbstractNaiveBayesClassifier getClassifier() throws IOException {

        if (model == null) {
            model = getModel();
        }

        return new StandardNaiveBayesClassifier(model);
    }

    /**
     * Reads and returns the Naive Bayes model from the disk.
     *
     * @return {@link NaiveBayesModel} from disk
     * @throws IOException if the model cannot be read
     */
    private NaiveBayesModel getModel() throws IOException {

        Configuration configuration = new Configuration();
        String modelLocation = "target/naiveBayesModelPath";
        return NaiveBayesModel.materialize(new Path(modelLocation), configuration);
    }

    /**
     * Builds a {@link Dictionary} from a label map. Usually used to read the
     * labelindex written by Mahout {@link BayesUtils}.
     *
     * @param labelMapPath location of the label index written by the mahout
     *                     trainNaiveByaes job return dictionary {@link Dictionary}
     *                     containing all label/class mappings
     * @return the dictionary
     */
    private Dictionary buildDictionaryFromLabelMap(final String labelMapPath) {

        Configuration configuration = new Configuration();
        Dictionary dictionary = new Dictionary();
        Map<Integer, String> labelMap = BayesUtils.readLabelIndex(configuration, new Path(labelMapPath));

        for (int i = 0; i < labelMap.size(); i++) {
            dictionary.intern(labelMap.get(i).trim());
        }

        return dictionary;
    }

    /**
     * TODO - finish writing this method Generates a confidence value from a log
     * likelihood.
     *
     * @param logLikelihood the log likelihood
     * @return confidence score
     */
    private double getConfidence(final double logLikelihood) {

        // TODO FIXME return real confidence... might be hard as this is Bayes
        // double confidence = Math.pow(logLikelihood, 2);
        // confidence = Math.sqrt(confidence);
        //return STATIC_CONFIDENCE;
        return Math.exp(logLikelihood);
    }

    /**
     * Gets an Occ Code from the {@link CodeIndexer}.
     *
     * @param resultOfClassification the string representation of the classification code
     * @return Code code representation of the resultOfClassification
     * @throws IOException if resultOfClassification is not a valid code or CodeFactory
     *                     cannot read the code list.
     */
    private String getCode(final int resultOfClassification) throws IOException {

        return vectorFactory.getCodeIndexer().getCode(resultOfClassification);

    }

    /**
     * Trains a Naive Bayes model with the vectors supplied.
     *
     * @param trainingVectorLocation the training vector location
     * @param naiveBayesModelPath    the naive bayes model path
     * @return model {@link NaiveBayesModel} the trained model
     * @throws Exception the exception
     */
    private NaiveBayesModel trainNaiveBayes(final String trainingVectorLocation, final String naiveBayesModelPath) throws Exception {

        // -i = training vector location, -el = extract label index, -li- place
        // to store labelindex
        // -o = model output path, -ow = overwrite existing vectors
        String[] args = new String[]{"-i", trainingVectorLocation, "-o", naiveBayesModelPath, "-el", "-li", properties.getProperty("labelindex"), "-ow"};

        TrainNaiveBayesJob.main(args);

        return getModel();
    }
}
