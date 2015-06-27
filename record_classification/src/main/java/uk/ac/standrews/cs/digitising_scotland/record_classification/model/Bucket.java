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

import org.apache.commons.csv.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.util.dataset.*;

import java.io.*;
import java.util.*;

public class Bucket implements Iterable<Record>, Serializable {

    private static final long serialVersionUID = 7216381249689825103L;

    private final List<Record> records;

    public Bucket(Reader reader) throws InputFileFormatException, IOException {

        this(new DataSet(reader));
    }

    public Bucket(DataSet records_csv) throws InputFileFormatException {

        this();

        for (List<String> record : records_csv.getRecords()) {

            if (record.size() != 3)
                throw new InputFileFormatException("record should contain 3 values");

            int id = Integer.parseInt(record.get(0));
            String data = record.get(1);
            String code = record.get(2);

            Classification classification = code.isEmpty() ? Classification.UNCLASSIFIED : new Classification(code, new TokenSet(data), 1.0);

            records.add(new Record(id, data, classification));
        }
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

    /**
     * Instantiates a new empty bucket.
     */
    public Bucket() {

        records = new ArrayList<>();
    }

    public void add(final Record... records) {

        Collections.addAll(this.records, records);
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

        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        final Bucket that = (Bucket) other;
        return Objects.equals(records, that.records);
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
}
