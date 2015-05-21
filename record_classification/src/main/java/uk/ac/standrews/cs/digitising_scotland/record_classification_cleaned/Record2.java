/*
 * Copyright 2014 Digitising Scotland project:
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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned;

public class Record2 {

    private final int id;
    private final String data;
    private final Classification2 classification;  // TODO restore support for multiple classifications

    public Record2(final int id, final String data, final Classification2 classification) {

        this.id = id;
        this.data = data;
        this.classification = classification;
    }

    public String getData() {

        return data;
    }

    public int getId() {

        return id;
    }

    public Classification2 getClassification() {

        return classification;
    }

    @Override
    public String toString() {

        return "Record [id=" + id + ", data=" + data + "]";
    }
}
