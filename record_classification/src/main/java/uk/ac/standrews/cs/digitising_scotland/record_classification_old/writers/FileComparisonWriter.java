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
package uk.ac.standrews.cs.digitising_scotland.record_classification_old.writers;

import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.tools.Utils;

import java.io.*;
import java.util.Set;

/**
 * The Class FileComparisonWriter is used to write a human readable copy of a record to a file using a chosen delimiter.
 * This class is designed to compare the gold standard codes to classification codes.
 */
public class FileComparisonWriter extends OutputDataFormatter implements Closeable {

    /** The writer. */
    private BufferedWriter writer;

    /**
     * Instantiates a new human readable writer.
     *
     * @param outputPath the output path
     * @param delimiter the delimiter
     * @throws FileNotFoundException the file not found exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public FileComparisonWriter(final File outputPath, final String delimiter) throws FileNotFoundException, UnsupportedEncodingException {

        writer = (BufferedWriter) ReaderWriterFactory.createBufferedWriter(outputPath);
        setDelimiter(delimiter);
    }

    /**
     * Write this {@link Record} to file in a human readable format.
     *
     * @param record the record to be written
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write(final Record record) throws IOException {

        String recordAsString = formatRecord(record);
        writer.write(recordAsString);

    }

    /**
     * Formats the record into a human readable format.
     *
     * @param record the record to produce a string version of.
     * @return the string version of the record
     */
    private String formatRecord(final Record record) {

        String id = getID(record);
        String description = getDescription(record);
        String codes = getCodes(record);

        return id + description + codes + "\n";
    }

    /**
     * Gets the codes.
     *
     * @param record the record
     * @return the codes
     */
    public String getCodes(final Record record) {

        final String dlim = getDelimiter();
        StringBuilder sb = new StringBuilder();
        Set<Classification> classifications = record.getClassifications();
        Set<Classification> goldStandardSet = record.getGoldStandardClassificationSet();

        for (Classification goldCodeTriple : goldStandardSet) {
            Code goldCode = goldCodeTriple.getCode();

            if (Utils.contains(goldCode, classifications)) {
                sb.append("[correct]" + dlim + Utils.getCodeTripleWithCode(goldCode, classifications).getTokenSet() + dlim + goldCode.getDescription() + dlim + Utils.getCodeTripleWithCode(goldCode, classifications).getConfidence() + dlim);
            }
            else {
                sb.append("[missing]" + dlim + goldCodeTriple.getTokenSet() + dlim + goldCode.getDescription() + dlim + "GoldStandard" + dlim);
            }
        }

        for (Classification codeTriple : classifications) {
            Code code = codeTriple.getCode();

            if (!Utils.contains(code, goldStandardSet)) {
                sb.append("[extra]" + dlim + codeTriple.getTokenSet() + dlim + code.getDescription() + dlim + codeTriple.getConfidence() + dlim);
            }
        }

        return sb.toString();
    }

    @Override
    public void close() throws IOException {

        writer.close();
    }
}
