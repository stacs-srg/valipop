package populationStatistics.dataDistributionsTables.selfCorrecting;

import dateModel.dateImplementations.YearDate;
import dateModel.timeSteps.CompoundTimeUnit;
import dateModel.timeSteps.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import populationStatistics.dataDistributionTables.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import sun.util.resources.cldr.ar.CalendarData_ar_JO;
import utils.specialTypes.dataKeys.DataKey;
import utils.specialTypes.dataKeys.DeathDataKey;
import utils.specialTypes.integerRange.IntegerRange;

import javax.xml.crypto.Data;
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

            DataKey k1 = new DeathDataKey(iR.getValue(), 100);
            double r1 = sc1DDD.getCorrectingRate(k1, y);
            Assert.assertEquals(check, r1, DELTA);

            DataKey k2 = new DeathDataKey(iR.getValue(), 1000);
            double r2 = sc1DDD.getCorrectingRate(k2, y);
            Assert.assertEquals(check, r2, DELTA);

            sc1DDD.returnAppliedRate(k1, r1, y);
            r1 = sc1DDD.getCorrectingRate(k1, y);
            Assert.assertEquals(check, r1, DELTA);

            sc1DDD.returnAppliedRate(k2, r2, y);
            r2 = sc1DDD.getCorrectingRate(k2, y);
            Assert.assertEquals(check, r2, DELTA);

            r1 = sc1DDD.getCorrectingRate(k1, y);
            Assert.assertEquals(check, r1, DELTA);

            DataKey k3 = new DeathDataKey(iR.getValue(), 0);
            sc1DDD.returnAppliedRate(k3, r1, y);
            double r3 = sc1DDD.getCorrectingRate(k3, y); // Theoretically should this not return infinity?
            Assert.assertEquals(check, r3, DELTA);

            DataKey k4 = new DeathDataKey(iR.getValue(), 10);
            double r4 = sc1DDD.getCorrectingRate(k4, y);
            Assert.assertEquals(check, r4, DELTA);

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

                DataKey k1 = new DeathDataKey(iR.getValue(), 100);
                DataKey k2 = new DeathDataKey(iR.getValue(), 1000);

                // --- B

                double c1 = sc1DDDCopy.getRate(iR.getValue());

                double r1 = sc1DDD.getCorrectingRate(k1, tp);
                Assert.assertEquals(c1, r1, DELTA);

                double rr2 = 1.5 * r1;
                sc1DDD.returnAppliedRate(k1, rr2, tp);

                // --- C

                double rr2c1 = sc1DDD.getCorrectingRate(k1, tp);
                double exprr2c1 = calcExpectedCorrectiveRate(c1, rr2, k1, k1);
                Assert.assertEquals(exprr2c1, rr2c1, DELTA);

                double rr2c2 = sc1DDD.getCorrectingRate(k2, tp);
                double exprr2c2 = calcExpectedCorrectiveRate(c1, rr2, k1, k2);
                Assert.assertEquals(exprr2c2, rr2c2, DELTA);

                sc1DDD.returnAppliedRate(k2, rr2c2, tp);

                // --- D

                double rr3c = sc1DDD.getCorrectingRate(k1, tp);
                Assert.assertEquals(c1, rr3c, DELTA);

                double rr4 = 0.5 * r1;
                sc1DDD.returnAppliedRate(k1, rr4, tp);

                // --- E

                double rr4c1 = sc1DDD.getCorrectingRate(k1, tp);
                double exprr4c1 = calcExpectedCorrectiveRate(c1, rr4, k1, k1);
                Assert.assertEquals(exprr4c1, rr4c1, DELTA);

                double rr4c2 = sc1DDD.getCorrectingRate(k2, tp);
                double exprr4c2 = calcExpectedCorrectiveRate(c1, rr4, k1, k2);
                Assert.assertEquals(exprr4c2, rr4c2, DELTA);

                if (rr4c2 == 1) {
                    rr4c2 = calcUnfeteredExpectedCorrectiveRate(c1, rr4, k1, k2);
                }

                sc1DDD.returnAppliedRate(k2, rr4c2, tp);

                // --- F

                double rr5c = sc1DDD.getCorrectingRate(k1, tp);
                Assert.assertEquals(c1, rr5c, DELTA);

                double rr6 = 0.25 * r1;
                sc1DDD.returnAppliedRate(k1, rr6, tp);

                // --- G

                double rr6c1 = sc1DDD.getCorrectingRate(k1, tp);
                double exprr6c1 = calcExpectedCorrectiveRate(c1, rr6, k1, k1);
                Assert.assertEquals(exprr6c1, rr6c1, DELTA);

                double rr6c2 = sc1DDD.getCorrectingRate(k2, tp);
                double exprr6c2 = calcExpectedCorrectiveRate(c1, rr6, k1, k2);
                Assert.assertEquals(exprr6c2, rr6c2, DELTA);

                double rr7 = rr6c2 * 1.75;

                sc1DDD.returnAppliedRate(k2, rr7, tp);

                // --- H

                double rrA = calcAdditiveRate(k1, rr6, k2, rr7);
                DataKey kA = new DeathDataKey(iR.getValue(), k1.getForNPeople() + k2.getForNPeople());

                double rrAc1 = sc1DDD.getCorrectingRate(k1, tp);
                double exprrAc1 = calcExpectedCorrectiveRate(c1, rrA, kA, k1);
                Assert.assertEquals(exprrAc1, rrAc1, DELTA);

                double rrAc2 = sc1DDD.getCorrectingRate(k2, tp);
                double exprrAc2 = calcExpectedCorrectiveRate(c1, rrA, kA, k2);
                Assert.assertEquals(exprrAc2, rrAc2, DELTA);

                double rr8 = rrAc1 * 0.9;

                sc1DDD.returnAppliedRate(k1, rr8, tp);

                // --- I

                double rrB = calcAdditiveRate(kA, rrA, k1, rr8);
                DataKey kB = new DeathDataKey(iR.getValue(), kA.getForNPeople() + k1.getForNPeople());

                double rrBc1 = sc1DDD.getCorrectingRate(k1, tp);
                double exprrBc1 = calcExpectedCorrectiveRate(c1, rrB, kB, k1);
                Assert.assertEquals(exprrBc1, rrBc1, DELTA);

                double rrBc2 = sc1DDD.getCorrectingRate(k2, tp);
                double exprrBc2 = calcExpectedCorrectiveRate(c1, rrB, kB, k2);
                Assert.assertEquals(exprrBc2, rrBc2, DELTA);

                sc1DDD.returnAppliedRate(k2, rrBc2, tp);

                // --- J

                double rrBc2c = sc1DDD.getCorrectingRate(k1, tp);
                Assert.assertEquals(c1, rrBc2c, DELTA);

            }
        }

    }

    private double calcAdditiveRate(DataKey k1, double r1, DataKey k2, double r2) {

        double aR = (r1 * k1.getForNPeople() + r2 * k2.getForNPeople()) / (k1.getForNPeople() + k2.getForNPeople());

        return aR;
    }

    private double calcExpectedCorrectiveRate(double targetRate, double returnedRate, DataKey returnedKey, DataKey checkKey) {

        double expectedCorrectiveRate =
                (targetRate * (returnedKey.getForNPeople()
                        + checkKey.getForNPeople())
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

    private double calcUnfeteredExpectedCorrectiveRate(double targetRate, double returnedRate, DataKey returnedKey, DataKey checkKey) {

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
        CompoundTimeUnit m6 = new CompoundTimeUnit(6, TimeUnit.MONTH);


        DataKey yearK = new DeathDataKey(age, popSize);
        int expPopSize = popSize - (int) Math.round(popSize * data.getCorrectingRate(yearK, y));

        for(int m = 1; m <= 12; m+=2) {
            DataKey k = new DeathDataKey(age, popSize);

            double rate = data.getCorrectingRate(k, m6);

            popSize -= Math.round(popSize * rate);

        }

        Assert.assertEquals(expPopSize, popSize);

    }


}






























