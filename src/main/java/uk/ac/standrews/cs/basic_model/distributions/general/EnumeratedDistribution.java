/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.basic_model.distributions.general;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.basic_model.distributions.StringWithCumulativeProbability;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * A distribution of strings controlled by specified probabilities.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class EnumeratedDistribution implements Distribution<String> {

    private static final BigDecimal ALLOWABLE_TOTAL_WEIGHT_DISCREPANCY = new BigDecimal(0.000001);
    private static final Comparator<? super StringWithCumulativeProbability> ITEM_COMPARATOR = new ItemComparator();

    private final RandomGenerator random;
    public StringWithCumulativeProbability[] items = null;

    protected EnumeratedDistribution(final RandomGenerator random) {
        this.random = random;
    }

    /**
     * Creates an Enumerated distribution.
     * 
     * @param item_probabilities a map of strings to probabilities to be used in the creation of the distribution.
     * @param random a Random instance for use in creation of distribution.
     * @throws InconsistentWeightException if the weights in the underlying distribution do not sum to 1.
     */
    public EnumeratedDistribution(final Map<String, BigDecimal> item_probabilities, final RandomGenerator random) throws InconsistentWeightException {

        this(random);
        configureProbabilities(item_probabilities);
    }

    protected void configureProbabilities(final Map<String, BigDecimal> item_probabilities) throws InconsistentWeightException {

        List<StringWithCumulativeProbability> items_temp = new ArrayList<>();
        BigDecimal cumulative_probability = BigDecimal.ZERO;
        int i = 0;

        for (final Map.Entry<String, BigDecimal> entry : item_probabilities.entrySet()) {

            if(entry.getValue().compareTo(BigDecimal.ZERO) != 0) {
                cumulative_probability = cumulative_probability.add(entry.getValue());
//                cumulative_probability += entry.getValue();
                items_temp.add(new StringWithCumulativeProbability(entry.getKey(), cumulative_probability));
            }
        }

        cumulative_probability = cumulative_probability.add(new BigDecimal(-1));
        cumulative_probability = cumulative_probability.abs();
        if (cumulative_probability.compareTo(ALLOWABLE_TOTAL_WEIGHT_DISCREPANCY) > 0) {
            throw new InconsistentWeightException();
        }

        items = items_temp.toArray(new StringWithCumulativeProbability[items_temp.size()]);

    }

    @Override
    public String getSample() {

        final BigDecimal dice_throw = new BigDecimal(random.nextDouble());

        int sample_index;
        try {
            sample_index = Arrays.binarySearch(items, new StringWithCumulativeProbability("", dice_throw), ITEM_COMPARATOR);
        } catch (NullPointerException e) {
            System.out.println(items.length);
            System.out.println(dice_throw);
            System.out.println(ITEM_COMPARATOR);
            throw e;
        }

        // If the exact cumulative probability isn't matched - and it's very unlikely to be - the result of binarySearch() is (-(insertion point) - 1).
        if (sample_index < 0) {
            sample_index = -sample_index - 1;
        }
        if (sample_index >= items.length) {
            sample_index = items.length - 1;
        }

        return items[sample_index].getItem();
    }

    private static class ItemComparator implements Comparator<StringWithCumulativeProbability>, Serializable {

        @Override
        public int compare(final StringWithCumulativeProbability o1, final StringWithCumulativeProbability o2) {
            System.out.println("o1: " + o1.getItem() + " - " + o1.getCumulativeProbability());
            System.out.println("o2: " + o2.getItem() + " - " + o2.getCumulativeProbability());
            return o1.getCumulativeProbability().compareTo(o2.getCumulativeProbability());
        }
    }
}
