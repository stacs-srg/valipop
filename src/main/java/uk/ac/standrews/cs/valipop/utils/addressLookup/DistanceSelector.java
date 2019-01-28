package uk.ac.standrews.cs.valipop.utils.addressLookup;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DistanceSelector {

    RandomGenerator random;

    private final PoissonDistribution distribution;

    private static final double poissonM = 7;
    private static final double averageMoveDistance = 21.0;

    public DistanceSelector(RandomGenerator random) {

        this.random = random;
        distribution = new PoissonDistribution(random, poissonM - 0.5, PoissonDistribution.DEFAULT_EPSILON, PoissonDistribution.DEFAULT_MAX_ITERATIONS);
    }

    public double selectRandomDistance() {

        return Math.round(distribution.sample() * averageMoveDistance / poissonM);

    }

}
