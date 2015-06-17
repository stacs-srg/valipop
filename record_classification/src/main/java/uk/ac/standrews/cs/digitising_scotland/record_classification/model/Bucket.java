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

import old.record_classification_old.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFileFormatException;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Bucket implements Iterable<Record> {

    private final List<Record> records;

    /**
     * Instantiates a new empty bucket.
     */
    public Bucket() {

        records = new ArrayList<>();
    }

    public Bucket(InputStreamReader reader) throws InputFileFormatException, IOException {

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

            Classification classification = "".equals(code) ? Classification.UNCLASSIFIED : new Classification(code, new TokenSet(data), 1.0);

            records.add(new Record(id, data, classification));
        }
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

    /**
     * Returns the number of records in the bucket.
     *
     * @return the number of records
     */
    public int size() {

        return records.size();
    }

    @Override public Iterator<Record> iterator() {

        return records.iterator();
    }

    @Override public String toString() {

        return "Bucket [records=" + records + ", size=" + size() + "]";
    }
}
