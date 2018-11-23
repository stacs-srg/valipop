package uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure;

import org.junit.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.person.Person;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import static org.junit.Assert.assertEquals;
import static uk.ac.standrews.cs.valipop.simulationEntities.population.PopulationNavigation.ageOnDate;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonTest {

    @Test
    public void testAgeOnDate() {

        Config config = new Config(new MonthDate(1,1), new MonthDate(1,100),
                new MonthDate(1,200), 0, 0, 0, null,
                "src/test/resources/valipop/test-pop", "", "",
                0, 0, true, 0, 0, 0,
                0, new CompoundTimeUnit(1, TimeUnit.YEAR), RecordFormat.NONE, null, 0, true);
        // use config to make make ps
        PopulationStatistics ps = new PopulationStatistics(config);

        Person p1 = new Person(SexOption.MALE, new ExactDate(1,1,1900), null, ps, false);
        Person p3 = new Person(SexOption.MALE, new ExactDate(2,1,1900), null, ps, false);

        YearDate y1 = new YearDate(1900);
        YearDate y2 = new YearDate(1901);
        YearDate y3 = new YearDate(1902);

        ExactDate e = new ExactDate(31,12,1901);

        assertEquals(0, ageOnDate(p1, y1));

        assertEquals(0, ageOnDate(p3,y2));
        assertEquals(0, ageOnDate(p1,y2));

        assertEquals(1, ageOnDate(p1,y3));

        Person p2 = new Person(SexOption.MALE, new ExactDate(31,12,1900), null, ps, false);

        assertEquals(0, ageOnDate(p2,y2));
        assertEquals(1, ageOnDate(p2,e));
        assertEquals(1, ageOnDate(p2,y3));
    }
}
