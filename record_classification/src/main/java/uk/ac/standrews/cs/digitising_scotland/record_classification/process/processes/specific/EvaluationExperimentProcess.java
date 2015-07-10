package uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.specific;

import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationProcessWithContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassifierFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class EvaluationExperimentProcess extends ClassificationProcessWithContext {

    private List<Path> gold_standard_files;
    private List<Double> training_ratios;
    private List<Cleaner> cleaners;

    public EvaluationExperimentProcess(ClassifierFactory factory, Random random) {

        super(factory, random);
    }

    public EvaluationExperimentProcess setGoldStandardFiles(List<Path> gold_standard_files){

        this.gold_standard_files = gold_standard_files;
        return this;
    }

    public EvaluationExperimentProcess setTrainingRatios(List<Double> training_ratios){

        this.training_ratios = training_ratios;
        return this;
    }

    public EvaluationExperimentProcess setCleaners(List<Cleaner> cleaners){

        this.cleaners = cleaners;
        return this;
    }

    public void configureSteps() {

        for (Path gold_standard_file : gold_standard_files) {
            addStep(new LoadGoldStandardFromFileStep(gold_standard_file));
        }

        for (Cleaner cleaner : cleaners) {
            addStep(new CleanGoldStandardStep(cleaner));
        }

        for (double training_ratio : training_ratios) {
            addStep(new AddTrainingAndEvaluationRecordsByRatioStep(training_ratio));
        }

        addStep(new TrainClassifierStep());
        addStep(new EvaluateClassifierStep());
    }
}
