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

import java.io.*;
import java.util.*;

/**
 * A data record.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public class Record implements Serializable {

    private static final long serialVersionUID = 5810954671977163993L;
    
    private final int id;
    private final String data;
    private final Classification classification;  // TODO restore support for multiple classifications

    /**
     * Instantiates an {@link Classification#UNCLASSIFIED unclassified} record.
     *
     * @param id   the unique record identifier
     * @param data the record data
     */
    public Record(final int id, final String data) {

        this(id, data, Classification.UNCLASSIFIED);
    }

    /**
     * Instantiates a record classified by the given classification.
     *
     * @param id             the unique record identifier
     * @param data           the record data
     * @param classification the classification of this record
     * @throws NullPointerException if the given classification is {@code null}
     */
    public Record(final int id, final String data, final Classification classification) {

        Objects.requireNonNull(classification, "record classification cannot be null");

        this.id = id;
        this.data = data;
        this.classification = classification;
    }

    /**
     * Gets the data of this record.
     *
     * @return the data of this record
     */
    public String getData() {

        return data;
    }

    public int getId() {

        return id;
    }

    /**
     * Gets the classification of this record.
     *
     * @return the classification of this record.
     */
    public Classification getClassification() {

        return classification;
    }

    @Override
    public boolean equals(Object other) {

        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Record other_record = (Record) other;
        return Objects.equals(id, other_record.id) &&
                Objects.equals(data, other_record.data) &&
                Objects.equals(classification, other_record.classification);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, data, classification);
    }

    @Override
    public String toString() {

        return "Record [id=" + id + ", data=" + data + ", classification=" + classification + "]";
    }
}
