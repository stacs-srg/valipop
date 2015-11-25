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

import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.junit.*;
import org.la4j.matrix.*;
import org.la4j.vector.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.util.*;

import java.time.*;
import java.util.*;

/**
 * @author masih
 */
public class SerializableDenseMatrixTest {

    @Test
    public void testTimes() throws Exception {

        final Random random = new Random(1413);
        final int rows = 1000;
        final int columns = 4000;
        final DenseMatrix mah = new DenseMatrix(rows, columns);
        
        final org.la4j.matrix.DenseMatrix la4j = org.la4j.matrix.DenseMatrix.zero(rows, columns);
        
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                final double value = random.nextDouble();
                mah.set(row, column, value);
                la4j.set(row, column, value);
            }
        }

        final double[] vector_values = new double[columns];
        for (int i = 0; i < columns; i++) {
            vector_values[i] = random.nextDouble();
        }

        final DenseVector vector = new DenseVector(vector_values);

        final SparseVector that = SparseVector.fromArray(vector_values);

        final Instant start2 = Instant.now();
        final Vector mahtimes = mah.times(vector);
        final Duration mahelapsed = Duration.between(start2, Instant.now());

        final Instant start = Instant.now();
        final org.la4j.Vector la4jtimes = la4j.multiply(that);
        final Duration la4jelapes = Duration.between(start, Instant.now());

        System.out.println(mahelapsed.minus(la4jelapes).toMillis() + " millis saved");
        
        for (int i = 0; i < rows; i++) {
            Assert.assertEquals(la4jtimes.get(i), mahtimes.get(i), Validators.DELTA);
        }
    }
}
