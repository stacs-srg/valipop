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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.string_similarity;

import org.simmetrics.SetMetric;
import org.simmetrics.StringMetric;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.TokenSet;

import java.util.Set;

public class SetMetricAdapter implements StringMetric {

    SetMetric<String> set_metric;

    public SetMetricAdapter(SetMetric<String> set_metric) {

        this.set_metric = set_metric;
    }

    @Override
    public float compare(String s1, String s2) {

        Set<String> set1 = new TokenSet(s1);
        Set<String> set2 = new TokenSet(s2);

        return set_metric.compare(set1, set2);
    }
}
