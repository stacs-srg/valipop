package populationStatistics.dataDistributionsTables.selfCorrecting;

import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.BirthStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.MultipleBirthStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.inputted.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatisticsTests {

    @Test
    public void testA() throws IOException, InvalidInputFileException {
        Path p = Paths.get("/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/test/resources/config-ps.txt");
        Config config = new Config(p,"TEST", "...");
        PopulationStatistics ps = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        int age = 20;
        int order = 0;
        int cohortSize = 1000;
        CompoundTimeUnit consideredTimePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);
        YearDate currentDate = new YearDate(1900);

        BirthStatsKey key = new BirthStatsKey(age, order, cohortSize, consideredTimePeriod, currentDate);
        SingleDeterminedCount determinedCount = (SingleDeterminedCount) ps.getDeterminedCount(key);

        int numberOfChildren = determinedCount.getDeterminedCount();


        MultipleBirthStatsKey keyM = new MultipleBirthStatsKey(age, numberOfChildren, consideredTimePeriod, currentDate);
        MultipleDeterminedCount mDC = (MultipleDeterminedCount) ps.getDeterminedCount(keyM);

        int numberOfMothers = mDC.getDeterminedCount().getSumOfValues();


    }

    @Test
    public void testB() throws IOException, InvalidInputFileException {

        Path p = Paths.get("/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/test/resources/config-ps.txt");
        Config config = new Config(p,"TEST", "...");
        PopulationStatistics ps = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        int age = 20;
        int order = 0;
        int cohortSize = 1000;
        CompoundTimeUnit consideredTimePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);
        YearDate currentDate = new YearDate(1900);

        SingleDeterminedCount sDC = (SingleDeterminedCount) ps.getDeterminedCount(
                new BirthStatsKey(age, order, cohortSize, consideredTimePeriod, currentDate));

        double numberOfChildren = sDC.getRawUncorrectedCount();

        MultipleDeterminedCount mDc = (MultipleDeterminedCount) ps.getDeterminedCount(
                new MultipleBirthStatsKey(age, numberOfChildren, consideredTimePeriod, currentDate));

        double numberOfMothers = mDc.getRawUncorrectedCount().getSumOfValues();


        MultipleDeterminedCount mDC = (MultipleDeterminedCount) ps
                .getDeterminedCount(new MultipleBirthStatsKey(age, numberOfMothers, new CompoundTimeUnit(1, TimeUnit.YEAR), currentDate));

        double numberOfChildrenB = mDC.getRawUncorrectedCount().productOfLabelsAndValues().getSumOfValues();

        LabeledValueSet<IntegerRange, Double> stat = mDC.getRawUncorrectedCount();
    }
}
