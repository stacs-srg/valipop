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

import org.apache.commons.csv.CSVFormat;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.DuplicateRecordIdException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

public class Bucket implements Iterable<Record>, Serializable {

    private static final long serialVersionUID = 7216381249689825103L;

    private final Collection<Record> records;

    /**
     * Instantiates a new empty bucket.
     */
    public Bucket() {

        records = new TreeSet<>();
    }

    public Bucket(File records, Charset charset, char delimiter) throws IOException {

        this(Files.newBufferedReader(records.toPath(), charset), delimiter);
    }

    public Bucket(Reader reader, char delimiter) {

        this(new DataSet(reader, delimiter));
    }

    public Bucket(DataSet data_set) {

        this();

        for (List<String> record : data_set.getRecords()) {

            if (record.size() != 3) {
                throw new InputFileFormatException("record should contain 3 values");
            }

            int id = Integer.parseInt(record.get(0));
            String data = record.get(1);
            String code = record.get(2);

            Classification classification = code.isEmpty() ? Classification.UNCLASSIFIED : new Classification(code, new TokenList(data), 1.0);

            add(new Record(id, data, classification));
        }
    }

    public Bucket(Collection<Record> records) {

        this();
        add(records);
    }

    public Bucket(Record... records) {

        this(Arrays.asList(records));
    }

    public DataSet toDataSet(List<String> column_labels, CSVFormat format) {

        final DataSet dataset = new DataSet(column_labels);
        dataset.setOutputFormat(format);

        for (Record record : records) {

            final String column_0 = String.valueOf(record.getId());
            final String column_1 = record.getData();
            final String column_2 = record.getClassification().getCode();

            final List<String> row = Arrays.asList(column_0, column_1, column_2);
            dataset.addRow(row);
        }
        return dataset;
    }

    @Override
    public Iterator<Record> iterator() {

        return records.iterator();
    }

    @Override
    public int hashCode() {

        return Objects.hash(records);
    }

    @Override
    public boolean equals(final Object other) {

        return this == other || other instanceof Bucket && Objects.equals(records, ((Bucket) other).records);
    }

    @Override
    public String toString() {

        return "Bucket [records=" + records + ", size=" + size() + "]";
    }

    /**
     * Returns the number of records in the bucket.
     *
     * @return the number of records
     */
    public int size() {

        return records.size();
    }

    /**
     * Constructs a new bucket containing this bucket's records that do not exist in the give bucket.
     *
     * @param other the other bucket
     * @return a new bucket containing this records that are present in this bucket and not present in the other bucket
     */
    public Bucket difference(final Bucket other) {

        final Bucket difference = new Bucket();

        for (Record record : this) {
            if (!other.contains(record)) {
                difference.add(record);
            }
        }

        return difference;
    }

    /**
     * Adds the given records to this bucket.
     *
     * @param records the records to add
     */
    public void add(final Record... records) {

        add(Arrays.asList(records));
    }

    public void add(final Collection<Record> records) {

        int original_size = this.records.size();
        this.records.addAll(records);
        int final_size = this.records.size();

        if (final_size != original_size + records.size()) {
            throw new DuplicateRecordIdException();
        }
    }

    public void add(final Bucket bucket) {

        add(bucket.records);
    }

    /**
     * Checks whether the specified record is in this bucket.
     *
     * @param record the record to check.
     * @return true if the record is a member of this bucket
     */
    public boolean contains(final Record record) {

        return records.contains(record);
    }

    /**
     * Constructs a new bucket containing records of this bucket without classification.
     *
     * @return a new bucket containing records of this bucket without classification
     * @see Record#Record(int, String)
     */
    public Bucket stripRecordClassifications() {

        Bucket unclassified_bucket = new Bucket();

        for (Record record : this) {
            unclassified_bucket.add(new Record(record.getId(), record.getData()));
        }

        return unclassified_bucket;
    }

    /**
     * Constructs a new bucket containing the records with unique data in this bucket.
     *
     * @return a new bucket containing the records with unique data in this bucket
     */
    public Bucket uniqueDataRecords() {

        Map<String, Record> unique_data_records = new HashMap<>();

        for (Record record : this) {
            unique_data_records.put(record.getData(), record);
        }

        final Bucket unique_bucket = new Bucket();
        unique_bucket.records.addAll(unique_data_records.values());

        return unique_bucket;
    }

    /**
     * Constructs a new bucket containing a randomly selected subset of records present in this bucket based on a given selection probability.
     *
     * @param random                the random number generator
     * @param selection_probability the probability of a record being selected expressed within inclusive range of {@code 0.0}  to {@code 1.0}
     * @return a new bucket containing a randomly selected subset of this bucket's records
     */
    public Bucket randomSubset(final Random random, double selection_probability) {

        final Bucket subset = new Bucket();

        for (Record record : this) {
            if (random.nextDouble() < selection_probability) {
                subset.add(record);
            }
        }

        return subset;
    }
}
