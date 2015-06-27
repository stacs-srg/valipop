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

import uk.ac.standrews.cs.digitising_scotland.record_classification.interfaces.ClassificationProcess;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.util.dataset.DataSet;
import uk.ac.standrews.cs.util.tables.TableGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultipleClassificationProcess {

    public static final InfoLevel DEFAULT_INFO_LEVEL = InfoLevel.SHORT_SUMMARY;

    protected abstract List<ClassificationProcess> getClassificationProcesses(String[] args) throws Exception;

    public InfoLevel getInfoLevel() {

        return DEFAULT_INFO_LEVEL;
    }

    protected void process(String[] args) throws Exception {

        List<ClassificationProcess> processes = getClassificationProcesses(args);

        List<String> row_labels = new ArrayList<>();
        List<DataSet> result_sets = new ArrayList<>();

        for (ClassificationProcess process : processes) {

            process.setInfoLevel(getInfoLevel());

            row_labels.add(process.getClassifierDescription());
            result_sets.add(process.trainClassifyAndEvaluate());
        }

        summariseResults(row_labels, result_sets);
    }

    private void summariseResults(List<String> row_labels, List<DataSet> data_sets) throws IOException {

        int size = data_sets.get(0).getRecords().size();
        String table_caption = "\naggregate classifier performance (" + size + " repetition" + (size > 1 ? "s" : "") + "):\n";
        String first_column_heading = "classifier";

        TableGenerator table_generator = new TableGenerator(row_labels, data_sets, System.out, table_caption, first_column_heading, true, '\t');

        if (getInfoLevel() != InfoLevel.NONE) {

            table_generator.printTable();
        }
    }
}
