package uk.ac.standrews.cs.digitising_scotland.record_classification.process;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InvalidArgException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;

import java.io.IOException;
import java.io.InputStreamReader;

public class ExactMatchClassificationProcess extends AbstractClassificationProcess {

    public ExactMatchClassificationProcess() {

        super();
    }

    public ExactMatchClassificationProcess(String[] args) throws IOException, InvalidArgException {

        super(args);
    }

    public ExactMatchClassificationProcess(InputStreamReader gold_standard_data_reader, double training_ratio) {

        super(gold_standard_data_reader, training_ratio);
    }

    public static void main(final String[] args) throws Exception {

        ExactMatchClassificationProcess classification_process = new ExactMatchClassificationProcess(args);

        classification_process.setInfoLevel(InfoLevel.SUMMARY);
        classification_process.repeatedlyTrainClassifyAndEvaluate();
    }

    @Override
    public Classifier getClassifier() {

        return new ExactMatchClassifier();
    }
}
