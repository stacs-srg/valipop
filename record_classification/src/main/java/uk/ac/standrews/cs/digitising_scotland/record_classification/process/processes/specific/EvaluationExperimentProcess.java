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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.specific;

import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.Cleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.processes.generic.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps.*;

import java.nio.file.Path;
import java.util.List;

public class EvaluationExperimentProcess extends ClassificationProcess {

    private List<Path> gold_standard_files;
    private List<Double> training_ratios;
    private List<Cleaner> cleaners;

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
