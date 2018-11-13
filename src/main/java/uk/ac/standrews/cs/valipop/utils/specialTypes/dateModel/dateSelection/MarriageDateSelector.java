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

    PoissonDistribution dist = null;
    double poissonM = 15;
    double averageYearsFromMarriageToChild = 3.0;
    int daysInYear = 365;

    public ExactDate selectDate(ValipopDate earliestDate, ValipopDate latestDate, RandomGenerator random) {

        if(dist == null) {
            dist = new PoissonDistribution(random, poissonM - 0.5,PoissonDistribution.DEFAULT_EPSILON, PoissonDistribution.DEFAULT_MAX_ITERATIONS);
        }

        int daysInWindow = DateUtils.differenceInDays(earliestDate, latestDate);

        double chosenYear = dist.sample() * averageYearsFromMarriageToChild / poissonM;
        double dayAdjust = random.nextInt((int) (Math.floor(daysInYear *  (averageYearsFromMarriageToChild / poissonM))));

        int chosenDay = Math.toIntExact(Math.round(chosenYear * daysInYear + dayAdjust));

        if(chosenDay > daysInWindow) {
            try {
                // revert to unifrom dist
                chosenDay = random.nextInt(daysInWindow);
            } catch (IllegalArgumentException e) {
                System.out.println("Unpermitted bound in Date Selector - window size = " + daysInWindow);
                chosenDay = 0;
            }
        }

        ExactDate d = DateUtils.calculateExactDate(latestDate, -1 * chosenDay);

        if(DateUtils.dateBefore(latestDate, d)) {
            System.out.print("WHAT?!?");
        }

        return d;

    }

}
