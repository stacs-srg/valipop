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
package uk.ac.standrews.cs.digitising_scotland.record_classification.model;

import old.record_classification_old.data_readers.AbstractFormatConverter;
import old.record_classification_old.data_readers.LongFormatConverter;
import old.record_classification_old.datastructures.code.CodeNotValidException;
import old.record_classification_old.datastructures.tokens.TokenSet;
import old.record_classification_old.exceptions.InputFormatException;
import old.record_classification_old.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.util.csv.DataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Bucket implements Iterable<Record> {

    private List<Record> records;

    /**
     * Instantiates a new empty bucket.
     */
    public Bucket() {

        records = new ArrayList<>();
    }

    public Bucket(final List<Record> existing_records) {

        this();

        records.addAll(existing_records);
    }

    /**
     * Generates a bucket of training records (with gold standard codes) from the given training file.
     * The file should be either in the short NRS format or in the format the matches the {@link AbstractFormatConverter}
     * specified in the class. Set to {@link LongFormatConverter} as  default.
     *
     * @param trainingFile the training file to generate the records and train the models from
     * @return the bucket that will be populated
     * @throws IOException           Signals that an I/O exception has occurred.
     * @throws InputFormatException  the input format exception
     * @throws CodeNotValidException
     */
    public Bucket(final File trainingFile) throws IOException, InputFormatException, CodeNotValidException {

        this(makeOccupationRecordsFromFile(trainingFile));
    }

    public Bucket(InputStreamReader reader) throws InputFileFormatException, IOException {

        this(new DataSet(reader));
    }

    public Bucket(DataSet records_csv) throws InputFileFormatException {

        this();

        for (List<String> record : records_csv.getRecords()) {

            if (record.size() != 3) throw new InputFileFormatException("record should contain 3 values");

            int id = Integer.parseInt(record.get(0));
            String data = record.get(1);
            String code = record.get(2);

            Classification classification = "".equals(code) ? Classification.UNCLASSIFIED : new Classification(code, new TokenSet(data), 1.0);

            records.add(new Record(id, data, classification));
        }
    }

    private static List<Record> makeOccupationRecordsFromFile(final File inputFile) throws IOException, InputFormatException {

        final String delimiter = "\\|";
        final int idPos = 0;
        final int descriptionPos = 5;
        final int codePos = 6;

        List<Record> recordList = new ArrayList<>();

        String line;

        try (BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile)) {
            while ((line = br.readLine()) != null) {

                System.out.println("line: " + line);

                String[] lineSplit = line.split(delimiter);

                int id = Integer.parseInt(lineSplit[idPos]);
                String data = lineSplit[descriptionPos];
                String code = lineSplit[codePos];  // TODO restore checks for legal codes

                Classification classification = "".equals(code) ? null : new Classification(code, new TokenSet(data), 1.0);

                recordList.add(new Record(id, code, classification));
            }
        }
        return recordList;
    }

    public void add(final Record... records) {

        Collections.addAll(this.records, records);
    }



    /**
     * Checks if the specified record is in this bucket. Will return true if and only if the record is a member of the bucket.
     *
     * @param record record to check for membership.
     * @return true if this record is a member, false otherwise
     */
    public boolean contains(final Record record) {

        return records.contains(record);
    }


    /**
     * Returns the number of {@link old.record_classification_old.datastructures.records.Record}s in the bucket.
     *
     * @return the number of records in the bucket
     */
    public int size() {

        return records.size();
    }

    /**
     * Checks if a bucket is empty or not.
     *
     * @return true is there are no records in the bucket, false if not.
     */
    public boolean isEmpty() {

        return records.size() == 0;
    }

    /**
     * Iterator A {@link old.record_classification_old.datastructures.records.Record} itereator that allows iteration though all the records in
     * the bucket.
     *
     * @return iterator of type Iterator<Record>.
     */
    @Override
    public Iterator<Record> iterator() {

        return records.iterator();
    }

    @Override
    public String toString() {

        return "Bucket [records=" + records + "]";
    }
}
