package uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.standrews.cs.basic_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.person.Person;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.IOException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonTests {

    @Test
    public void testAgeOnDate() throws InconsistentWeightException, IOException, InvalidInputFileException {

        Config config = new Config(new MonthDate(1,1), new MonthDate(1,100),
                new MonthDate(1,200), 0, 0, 0, null,
                "src/test/resources/valipop/test-pop", "", "",
                0, 0, true, 0, 0,
                0, new CompoundTimeUnit(1, TimeUnit.YEAR), RecordFormat.NONE, null);
        // use config to make make ps
        PopulationStatistics ps = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        Person p1 = new Person('M', new ExactDate(1,1,1900), null, ps);
        Person p3 = new Person('M', new ExactDate(2,1,1900), null, ps);

        YearDate y1 = new YearDate(1900);
        YearDate y2 = new YearDate(1901);
        YearDate y3 = new YearDate(1902);

        ExactDate e = new ExactDate(31,12,1901);

        Assert.assertEquals(0, p1.ageOnDate(y1));

        Assert.assertEquals(0, p3.ageOnDate(y2));
        Assert.assertEquals(0, p1.ageOnDate(y2));

        Assert.assertEquals(1, p1.ageOnDate(y3));

        Person p2 = new Person('M', new ExactDate(31,12,1900), null, ps);

        Assert.assertEquals(0, p2.ageOnDate(y2));
        Assert.assertEquals(1, p2.ageOnDate(e));
        Assert.assertEquals(1, p2.ageOnDate(y3));

    }


}
