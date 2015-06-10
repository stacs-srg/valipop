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
package uk.ac.standrews.cs.digitising_scotland.record_classification.process.multiple_classifier;

import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InvalidArgException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.single_classifier.ExactMatchClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.single_classifier.DummyClassificationProcess;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExactMatchAndDummyClassificationProcess extends AbstractMultipleClassificationProcess {

    public static void main(final String[] args)  {

        try {
            ExactMatchAndDummyClassificationProcess classification_process = new ExactMatchAndDummyClassificationProcess();

            classification_process.process(args);
        }
        catch (Exception e) {
            System.out.println("problem in classification process: " + e.getMessage());
        }
    }

    protected List<ClassificationProcess> getClassificationProcesses(String[] args) throws IOException, InvalidArgException {

        ClassificationProcess process1 = new ExactMatchClassificationProcess(args);
        ClassificationProcess process2 = new DummyClassificationProcess(args);

        return Arrays.asList(process1, process2);
    }
}
