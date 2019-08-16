package uk.ac.standrews.cs.valipop.simulationEntities.dataStructure;

import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.Person;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;

import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation.ageOnDate;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonTest {

    @Ignore
    @Test
    public void testAgeOnDate() {

        Config config = new Config(
                LocalDate.of(1, 1, 1),
                LocalDate.of(100, 1, 1),
                LocalDate.of(200, 1, 1),
                0,
                Paths.get("src/test/resources/valipop/test-pop"), Config.DEFAULT_RESULTS_SAVE_PATH, "PERSON_TEST",
                Config.DEFAULT_RESULTS_SAVE_PATH).setDeterministic(true);

        PopulationStatistics ps = new PopulationStatistics(config);

        Person p1 = new Person(SexOption.MALE, LocalDate.of(1900, 1, 1), null, ps, false);
        Person p3 = new Person(SexOption.MALE, LocalDate.of(1900, 1, 2), null, ps, false);

        LocalDate y1 = LocalDate.of(1900, 1, 1);
        LocalDate y2 = LocalDate.of(1901, 1, 1);
        LocalDate y3 = LocalDate.of(1902, 1, 1);
        LocalDate y4 = LocalDate.of(1901, 1, 2);

        LocalDate e = LocalDate.of(1901, 12, 31);

        assertEquals(0, ageOnDate(p1, y1));

        assertEquals(0, ageOnDate(p3, y2));
        assertEquals(0, ageOnDate(p1, y2));
        assertEquals(1, ageOnDate(p1, y4));

        assertEquals(1, ageOnDate(p1, y3));

        Person p2 = new Person(SexOption.MALE, LocalDate.of(1900, 12, 31), null, ps, false);

        assertEquals(0, ageOnDate(p2, y2));
        assertEquals(1, ageOnDate(p2, e));
        assertEquals(1, ageOnDate(p2, y3));
    }
}
