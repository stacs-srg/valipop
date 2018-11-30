package uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MarriageDateSelector extends DateSelector {

    private final PoissonDistribution distribution;

    private static final double poissonM = 15;
    private static final double averageYearsFromMarriageToChild = 3.0;
    private static final int daysInYear = 365;

    public MarriageDateSelector(RandomGenerator random) {

        super(random);
        distribution = new PoissonDistribution(random, poissonM - 0.5, PoissonDistribution.DEFAULT_EPSILON, PoissonDistribution.DEFAULT_MAX_ITERATIONS);
    }

    public ExactDate selectRandomDate(ValipopDate earliestDate, ValipopDate latestDate) {

        int daysInWindow = DateUtils.differenceInDays(earliestDate, latestDate);

        double chosenYear = distribution.sample() * averageYearsFromMarriageToChild / poissonM;
        double dayAdjust = random.nextInt((int) (Math.floor(daysInYear * (averageYearsFromMarriageToChild / poissonM))));

        int chosenDay = Math.toIntExact(Math.round(chosenYear * daysInYear + dayAdjust));

        if (chosenDay > daysInWindow) {

            // revert to uniform distribution
            chosenDay = random.nextInt(daysInWindow);
        }

        return DateUtils.calculateExactDate(latestDate, -1 * chosenDay);
    }
}
