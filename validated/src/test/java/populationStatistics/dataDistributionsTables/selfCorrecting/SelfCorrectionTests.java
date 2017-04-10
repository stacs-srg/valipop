package populationStatistics.dataDistributionsTables.selfCorrecting;

import dateModel.dateImplementations.YearDate;
import dateModel.timeSteps.CompoundTimeUnit;
import dateModel.timeSteps.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import populationStatistics.dataDistributionTables.statsKeys.DeathStatsKey;
import populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import utils.specialTypes.integerRange.IntegerRange;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectionTests {

    private final double DELTA = 1E-10;

    private SelfCorrectingOneDimensionDataDistribution createSC1DDD() {

        Map<IntegerRange, Double> data = new HashMap<>();
        data.put(new IntegerRange(0), 0.1);
        data.put(new IntegerRange(1), 0.000037);
        data.put(new IntegerRange(2), 0D);
        data.put(new IntegerRange(3), 1D);
        data.put(new IntegerRange(4), 0.5);
        data.put(new IntegerRange(5), 0.01);

        SelfCorrectingOneDimensionDataDistribution sc1DDD =
                new SelfCorrectingOneDimensionDataDistribution(new YearDate(0),"test",
                                                                "test", data);

        return sc1DDD;
    }

    @Test
    public void firstAccessSC1DDD() {

        SelfCorrectingOneDimensionDataDistribution sc1DDD = createSC1DDD();
        OneDimensionDataDistribution sc1DDDCopy = sc1DDD.clone();

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);

        for(IntegerRange iR : sc1DDD.getRate().keySet()) {

            double check = sc1DDDCopy.getRate(iR.getValue());

            // Basic first retrieval tests
            StatsKey k1 = new DeathStatsKey(iR.getValue(), 100, y);
            DeterminedCount r1 = sc1DDD.determineCount(k1);
            Assert.assertEquals((int)Math.round(check * 100), r1.getDeterminedCount(), DELTA);

            StatsKey k2 = new DeathStatsKey(iR.getValue(), 1000, y);
            DeterminedCount r2 = sc1DDD.determineCount(k2);
            Assert.assertEquals((int)Math.round(check * 1000), r2.getDeterminedCount(), DELTA);

            // Secondary retrieval having met first request
            r1.setFufilledCount(r1.getDeterminedCount());
            sc1DDD.returnAchievedCount(r1);
            r1 = sc1DDD.determineCount(k1);
            Assert.assertEquals((int)Math.round(check * 100), r1.getDeterminedCount(), DELTA);

            r2.setFufilledCount(r2.getDeterminedCount());
            sc1DDD.returnAchievedCount(r2);
            r2 = sc1DDD.determineCount(k2);
            Assert.assertEquals((int)Math.round(check * 1000), r2.getDeterminedCount(), DELTA);

            r1 = sc1DDD.determineCount(k1);
            Assert.assertEquals((int)Math.round(check * 100), r1.getDeterminedCount(), DELTA);

            StatsKey k4 = new DeathStatsKey(iR.getValue(), 10, y);
            DeterminedCount r4 = sc1DDD.determineCount(k4);

            Assert.assertEquals((int)Math.round(check * 10), r4.getDeterminedCount(), DELTA);

        }

    }

    @Test
    public void correctingChecksSC1DDD() {

        SelfCorrectingOneDimensionDataDistribution sc1DDD = createSC1DDD();
        OneDimensionDataDistribution sc1DDDCopy = sc1DDD.clone();

        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        CompoundTimeUnit m6 = new CompoundTimeUnit(6, TimeUnit.MONTH);

        CompoundTimeUnit[] tps = {y};

        for(CompoundTimeUnit tp : tps) {

            for (IntegerRange iR : sc1DDD.getRate().keySet()) {

                StatsKey k1 = new DeathStatsKey(iR.getValue(), 100, tp);
                StatsKey k2 = new DeathStatsKey(iR.getValue(), 1000, tp);

                // --- B

                double c1 = sc1DDDCopy.getRate(iR.getValue());

                DeterminedCount r1 = sc1DDD.determineCount(k1);
                Assert.assertEquals((int) Math.round(c1 * r1.getKey().getForNPeople()), r1.getDeterminedCount());

                int rr2 = (int) Math.round(1.5 * r1.getDeterminedCount());
                r1.setFufilledCount(rr2);
                sc1DDD.returnAchievedCount(r1);

                // --- C

                DeterminedCount rr2c1 = sc1DDD.determineCount(k1);

//                double exprr2c1 = calcExpectedCorrectiveRate(c1, rr2, k1, k1);
                int expc2c1 = calcExpectedCount(r1, k1, c1);
                Assert.assertEquals(expc2c1, rr2c1.getDeterminedCount());

                DeterminedCount rr2c2 = sc1DDD.determineCount(k2);
                int expc2c2 = calcExpectedCount(r1, k2, c1);
                Assert.assertEquals(expc2c2, rr2c2.getDeterminedCount());

                rr2c2.setFufilledCount(rr2c2.getDeterminedCount());
                sc1DDD.returnAchievedCount(rr2c2);

                // --- D

                DeterminedCount rr3c = sc1DDD.determineCount(k1);
                Assert.assertEquals((int) Math.round(c1 * rr3c.getKey().getForNPeople()), rr3c.getDeterminedCount());


                int rr4 = (int) Math.round(0.5 * r1.getDeterminedCount());
                rr3c.setFufilledCount(rr4);
                sc1DDD.returnAchievedCount(rr3c);

                // --- E

                DeterminedCount rr4c1 = sc1DDD.determineCount(k1);
                int expc4c1 = calcExpectedCount(rr3c, k1, c1);

                Assert.assertEquals(expc4c1, rr4c1.getDeterminedCount());

                DeterminedCount rr4c2 = sc1DDD.determineCount(k2);

                double exprr4c2 = calcExpectedCorrectiveRate(c1, rr4, k1, k2);
                int expc4c2 = calcExpectedCount(rr3c, k2, c1);

                Assert.assertEquals(expc4c2, rr4c2.getDeterminedCount());

                if (rr4c2.getDeterminedCount() == rr4c2.getKey().getForNPeople()) {
                    rr4c2.setFufilledCount(calcUnfetteredExpectedCount(rr3c, k2, c1));
                } else {
                    rr4c2.setFufilledCount(expc4c2);
                }

                sc1DDD.returnAchievedCount(rr4c2);

                // --- F

                DeterminedCount rr5c = sc1DDD.determineCount(k1);
                Assert.assertEquals((int) Math.round(c1 * rr5c.getKey().getForNPeople()), rr5c.getDeterminedCount());

//                double rr6 = 0.25 * r1;
//                sc1DDD.returnAchievedCount(k1, rr6, tp);
//
//                // --- G
//
//                double rr6c1 = sc1DDD.determineCount(k1, tp);
//                double exprr6c1 = calcExpectedCorrectiveRate(c1, rr6, k1, k1);
//                Assert.assertEquals(exprr6c1, rr6c1, DELTA);
//
//                double rr6c2 = sc1DDD.determineCount(k2, tp);
//                double exprr6c2 = calcExpectedCorrectiveRate(c1, rr6, k1, k2);
//                Assert.assertEquals(exprr6c2, rr6c2, DELTA);
//
//                double rr7 = rr6c2 * 1.75;
//
//                sc1DDD.returnAchievedCount(k2, rr7, tp);
//
//                // --- H
//
//                double rrA = calcAdditiveRate(k1, rr6, k2, rr7);
//                StatsKey kA = new DeathStatsKey(iR.getValue(), k1.getForNPeople() + k2.getForNPeople());
//
//                double rrAc1 = sc1DDD.determineCount(k1, tp);
//                double exprrAc1 = calcExpectedCorrectiveRate(c1, rrA, kA, k1);
//                Assert.assertEquals(exprrAc1, rrAc1, DELTA);
//
//                double rrAc2 = sc1DDD.determineCount(k2, tp);
//                double exprrAc2 = calcExpectedCorrectiveRate(c1, rrA, kA, k2);
//                Assert.assertEquals(exprrAc2, rrAc2, DELTA);
//
//                double rr8 = rrAc1 * 0.9;
//
//                sc1DDD.returnAchievedCount(k1, rr8, tp);
//
//                // --- I
//
//                double rrB = calcAdditiveRate(kA, rrA, k1, rr8);
//                StatsKey kB = new DeathStatsKey(iR.getValue(), kA.getForNPeople() + k1.getForNPeople());
//
//                double rrBc1 = sc1DDD.determineCount(k1, tp);
//                double exprrBc1 = calcExpectedCorrectiveRate(c1, rrB, kB, k1);
//                Assert.assertEquals(exprrBc1, rrBc1, DELTA);
//
//                double rrBc2 = sc1DDD.determineCount(k2, tp);
//                double exprrBc2 = calcExpectedCorrectiveRate(c1, rrB, kB, k2);
//                Assert.assertEquals(exprrBc2, rrBc2, DELTA);
//
//                sc1DDD.returnAchievedCount(k2, rrBc2, tp);
//
//                // --- J
//
//                double rrBc2c = sc1DDD.determineCount(k1, tp);
//                Assert.assertEquals(c1, rrBc2c, DELTA);

            }
        }

    }

    private int calcExpectedCount(DeterminedCount applied, StatsKey corrective, double targetRate) {

        int count = calcUnfetteredExpectedCount(applied, corrective, targetRate);

        if(count > corrective.getForNPeople()) {
            count = corrective.getForNPeople();
        }

        if(count < 0) {
            count = 0;
        }

        return count;

    }

    private int calcUnfetteredExpectedCount(DeterminedCount applied, StatsKey corrective, double targetRate) {

        return (int) Math.round(targetRate * (applied.getKey().getForNPeople() + corrective.getForNPeople()) - applied.getFufilledCount());
    }

    private double calcAdditiveRate(StatsKey k1, double r1, StatsKey k2, double r2) {

        double aR = (r1 * k1.getForNPeople() + r2 * k2.getForNPeople()) / (k1.getForNPeople() + k2.getForNPeople());

        return aR;
    }

    private double calcExpectedCorrectiveRate(double targetRate, double returnedRate, StatsKey returnedKey, StatsKey checkKey) {

        double expectedCorrectiveRate =
                (targetRate * (returnedKey.getForNPeople() + checkKey.getForNPeople())
                        - (returnedRate * returnedKey.getForNPeople()))
                / checkKey.getForNPeople();

        if(expectedCorrectiveRate > 1) {
            return 1;
        }

        if(expectedCorrectiveRate < 0) {
            return 0;
        }

        return expectedCorrectiveRate;
    }

    private double calcUnfeteredExpectedCorrectiveRate(double targetRate, double returnedRate, StatsKey returnedKey, StatsKey checkKey) {

        double expectedCorrectiveRate =
                (targetRate * (returnedKey.getForNPeople()
                        + checkKey.getForNPeople())
                        - (returnedRate * returnedKey.getForNPeople()))
                        / checkKey.getForNPeople();


        return expectedCorrectiveRate;
    }


    @Test
    public void variedTimeStepTest() {

        SelfCorrectingOneDimensionDataDistribution data = createSC1DDD();

        int age = 5;

        int popSize = 1000000;
        CompoundTimeUnit y = new CompoundTimeUnit(1, TimeUnit.YEAR);
        CompoundTimeUnit m2 = new CompoundTimeUnit(2, TimeUnit.MONTH);


        StatsKey yearK = new DeathStatsKey(age, popSize, y);
        int expPopSize = popSize - data.determineCount(yearK).getDeterminedCount();

        for(int m = 1; m <= 12; m+=2) {
            StatsKey k = new DeathStatsKey(age, popSize, m2);

            int count = data.determineCount(k).getDeterminedCount();

            popSize -= count;

        }

        Assert.assertEquals(expPopSize, popSize);

    }


}






























