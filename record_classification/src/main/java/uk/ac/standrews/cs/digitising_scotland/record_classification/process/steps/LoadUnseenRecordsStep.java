/*
 * record_classification - Automatic record attribute classification.
 * Copyright © 2014-2016 Digitising Scotland project
 * (http://digitisingscotland.cs.st-andrews.ac.uk/)
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

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.ClassificationContext;

import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * Loads unseen data records from a file into a classification process {@link ClassificationContext context}.
 *
 * @author Masih Hajiarab Derkani
 */
public class LoadUnseenRecordsStep extends LoadStep {

    private static final long serialVersionUID = 7742825393693404041L;

    /**
     * Instantiates a new step which loads unseen data records into a classification process {@link ClassificationContext context}.
     *
     * @param path the file to the CSV file
     */
    public LoadUnseenRecordsStep(Path path, Charset charset, String delimiter) {

        super(path, charset, delimiter);
    }

    @Override
    protected Bucket getRecords(ClassificationContext context) {

        return context.getUnseenRecords();
    }

    @Override
    protected void clearRecords(ClassificationContext context) {

        context.resetUnseenRecords();
    }
}
