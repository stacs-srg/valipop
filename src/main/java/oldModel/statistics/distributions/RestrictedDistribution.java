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
package oldModel.statistics.distributions;

import java.util.ArrayList;
import java.util.List;

/**
 * The restricted distribution class provided the ability for the return value of the distribution when sampled to be set to fall with a given range.
 *
 * @param <Value> Allows for the distribution to be set up using any specified Value
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class RestrictedDistribution<Value> implements Distribution<Value> {

    /**
     * The Minimum specified value.
     */
// Restricted Distribution Helper Values
    protected Double minimumSpecifiedValue = null;
    /**
     * The Maximum specified value.
     */
    protected Double maximumSpecifiedValue = null;

    /**
     * The Unused sample values.
     */
    protected List<Double> unusedSampleValues = new ArrayList<>();
    /**
     * The Preemptive sample values.
     */
    protected List<Double> preemptiveSampleValues = new ArrayList<>();
    /**
     * The Zero count.
     */
    protected int zeroCount = -1;
    /**
     * The Zero cap.
     */
    protected double zeroCap;

    /**
     * Get weights int [ ].
     *
     * @return the int [ ]
     */
    public abstract int[] getWeights();

    /**
     * Samples distribution and returns a value that falls between the two given values.
     *
     * @param smallestPermissableReturnValue The smallest value that the caller wishes for the distribution to return on this sample.
     * @param largestPermissableReturnValue  The largest value that the caller wishes for the distribution to return on this sample.
     * @return The Value sampled from the distribution.
     * @throws NoPermissableValueException           Thrown when no value in the distribution can satisfy the given range.
     * @throws NotSetUpAtClassInitilisationException Thrown if the distribution wasn't correctly set up at initialisation.
     */
    public abstract Value getSample(double smallestPermissableReturnValue, double largestPermissableReturnValue) throws NoPermissableValueException, NotSetUpAtClassInitilisationException;

    /**
     * Returns the minimum possible return value from the distribution as defined by the values in the user provided data.
     *
     * @return The minimum possible return value of the distribution.
     */
    public Double getMinimumReturnValue() {
        return minimumSpecifiedValue;
    }

    /**
     * Returns the maximum possible return value from the distribution as defined by the values in the user provided data.
     *
     * @return The maximum possible return value of the distribution.
     */
    public Double getMaximumReturnValue() {
        return maximumSpecifiedValue;
    }
}
