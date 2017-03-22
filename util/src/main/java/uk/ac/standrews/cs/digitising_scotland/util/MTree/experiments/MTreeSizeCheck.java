/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module util.
 *
 * util is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * util is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with util. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.util.MTree.experiments;

import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.experiments.euclidean.EuclideanDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.experiments.euclidean.Point;
import uk.ac.standrews.cs.util.tools.PercentageProgressIndicator;
import uk.ac.standrews.cs.util.tools.ProgressIndicator;

import java.util.Random;

/**
 * Note this is not a Unit test!
 * Designed to test the scalability of MTree implementation.
 * Created by al on 21/03/2017.
 */
public class MTreeSizeCheck {

    int initial = 5000000; // 1/2  million
    int increment = 5000000; // 1/2  million
    int max = 30000000; // 30 million.

    EuclideanDistance ed = new EuclideanDistance();

    public void loadtest() {

        for (int i = initial; i < max; i += increment) {
            create_tree(i);
        }
    }

    /**
     * Create an MTree of the specified size.
     */
    private void create_tree(int size) {

        MTree<Point> tree = new MTree<>(ed);

        long time = System.currentTimeMillis();
        System.out.println("Creating tree of size " + size);

        ProgressIndicator indicator = new PercentageProgressIndicator(10);
        indicator.setTotalSteps(size);

        Random random = new Random();
        for (int count = 0; count < size; count++) {
            tree.add(new Point(random.nextFloat(), random.nextFloat()));
            indicator.progressStep();
        }
        long elapsed = (System.currentTimeMillis() - time) / 1000;
        System.out.println("tree creation of size " + size + " - took " + elapsed + " seconds.");
    }

    public static void main(String args[]) {
        new MTreeSizeCheck().loadtest();
    }

}
