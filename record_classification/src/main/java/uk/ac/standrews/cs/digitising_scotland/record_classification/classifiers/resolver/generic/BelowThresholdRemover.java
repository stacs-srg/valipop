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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BelowThresholdRemover {

    private final double threshold;

    public BelowThresholdRemover(final Double threshold) {

        this.threshold = threshold;
    }

    public MultiValueMap<Code, Classification> removeBelowThreshold(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {

        MultiValueMap<Code, Classification> clone = map.deepClone();
        for (Code k : clone) {
            List<Classification> oldList = clone.get(k);
            List<Classification> newList = new ArrayList<>();
            for (Classification v : oldList) {
                if (v.getConfidence() >= threshold) {
                    newList.add(v);
                }
            }
            clone.put(k, newList);
        }
        return clone;
    }
}
