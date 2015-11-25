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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression;

import org.la4j.*;
import org.la4j.matrix.*;

import java.io.*;

/**
 * @author Fraser Dunlop
 */
public class SerializableDenseMatrix implements Serializable {

    private static final long serialVersionUID = -7738028674358816065L;

    private DenseMatrix matrix;

    /** Needed for JSON deserialization. */
    public SerializableDenseMatrix() {}

    public SerializableDenseMatrix(final Matrix matrix) {

        this.matrix = matrix.toDenseMatrix();
    }

    public SerializableDenseMatrix(final int rows, final int columns) {

        this.matrix = DenseMatrix.zero(rows, columns);
    }

    public DenseMatrix getMatrix() {

        return matrix;
    }

    public void setMatrix(DenseMatrix matrix) {

        this.matrix = matrix;
    }

    public double get(final int row, final int column) {

        return matrix.get(row, column);
    }

    public Vector times(final Vector vector) {

        return matrix.multiply(vector);
    }

    public void set(final int row, final int column, final double value) {

        matrix.set(row, column, value);
    }

    private void readObject(final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {

        matrix = DenseMatrix.from2DArray((double[][]) inputStream.readObject());
    }

    private void writeObject(final ObjectOutputStream outputStream) throws IOException {

        outputStream.writeObject(matrix.toArray());
    }
}
