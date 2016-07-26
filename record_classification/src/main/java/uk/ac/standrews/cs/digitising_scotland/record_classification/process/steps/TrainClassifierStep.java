/*
 * record_classification - Automatic record attribute classification.
 * Copyright © 2012-2016 Digitising Scotland project
 * (http://digitisingscotland.cs.st-andrews.ac.uk/record_classification/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.Classifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.Step;

import java.time.Duration;
import java.time.Instant;

/**
 * Trains a classifier in the context of a classification process.
 *
 * @author Masih Hajiarab Derkani
 */
public class TrainClassifierStep implements Step {

    private static final long serialVersionUID = 5825366701064269040L;

    private double internal_training_ratio;

    public TrainClassifierStep(double internal_training_ratio) {

        this.internal_training_ratio = internal_training_ratio;
    }

    @Override
    public void perform(final ClassificationContext context) {

        final Classifier classifier = context.getClassifier();
        final Bucket training_records = context.getTrainingRecords();
        final Instant start = Instant.now();

        classifier.trainAndEvaluate(training_records, internal_training_ratio, context.getRandom());

        final Duration training_time = Duration.between(start, Instant.now());
        context.setTrainingTime(training_time);
    }
}
