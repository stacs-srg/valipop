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
package uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces;

import uk.ac.standrews.cs.digitising_scotland.record_classification.analysis.ClassificationMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;

import java.io.IOException;
import java.io.InputStreamReader;

public interface ClassificationProcess {

    void performClassification() ;

    void setGoldStandardData(InputStreamReader gold_standard_data_reader);

    void setTrainingRatio(double training_ratio);

    void configureRecords() throws IOException, InputFileFormatException;

    void performTraining();

    ClassificationMetrics evaluateClassification() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException;

    void repeatedlyTrainClassifyAndEvaluate() throws IOException, InputFileFormatException, UnclassifiedGoldStandardRecordException, UnknownDataException, InvalidCodeException, InconsistentCodingException;

    void setInfoLevel(InfoLevel verbose);
}
