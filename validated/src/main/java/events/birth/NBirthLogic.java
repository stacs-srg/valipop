package events.birth;

import config.Config;
import dateModel.Date;
import dateModel.DateUtils;
import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;
import events.EventLogic;
import events.init.InitLogic;
import populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import populationStatistics.dataDistributionTables.determinedCounts.SingleDeterminedCount;
import populationStatistics.dataDistributionTables.statsKeys.BirthStatsKey;
import populationStatistics.dataDistributionTables.statsKeys.MultipleBirthStatsKey;
import populationStatistics.recording.PopulationStatistics;
import simulationEntities.person.IPerson;
import simulationEntities.population.dataStructure.FemaleCollection;
import simulationEntities.population.dataStructure.Population;
import simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NBirthLogic implements EventLogic {

    public static int tBirths = 0;

    @Override
    public void handleEvent(Config config, AdvancableDate currentDate, CompoundTimeUnit consideredTimePeriod,
                            Population population, PopulationStatistics desiredPopulationStatistics) throws InsufficientNumberOfPeopleException {

        int bornAtTS = 0;

        FemaleCollection femalesLiving = population.getLivingPeople().getFemales();
        Iterator<AdvancableDate> divDates = femalesLiving.getDivisionDates(consideredTimePeriod).iterator();

        AdvancableDate divDate;
        // For each division in the population data store upto the current date
        while(divDates.hasNext() && DateUtils.dateBeforeOrEqual(divDate = divDates.next(), currentDate)) {

            int age = DateUtils.differenceInYears(divDate, currentDate).getCount();
            Collection<IPerson> needingPartners = new ArrayList<>();

            Set<Integer> orders = femalesLiving.getBirthOrdersInDivision(divDate, consideredTimePeriod);

            for(Integer order : orders) {

                Collection<IPerson> people = femalesLiving.getByDatePeriodAndBirthOrder(divDate, consideredTimePeriod, order);
                int number = people.size();

                BirthStatsKey key = new BirthStatsKey(age, order, number, consideredTimePeriod, currentDate);
                DeterminedCount determinedCount = desiredPopulationStatistics.getDeterminedCount(key);

                int numberOfChildren = ((SingleDeterminedCount) determinedCount).getDeterminedCount();

                // Make women into mothers

                MotherSet mothers = selectMothers(config, people, numberOfChildren, desiredPopulationStatistics,
                        currentDate, consideredTimePeriod, population);

                int childrenMade = mothers.size();

                bornAtTS += childrenMade;
                InitLogic.incrementBirthCount(childrenMade);

                determinedCount.setFufilledCount(childrenMade);
                desiredPopulationStatistics.returnAchievedCount(determinedCount);

            }
            // Partner females of age who don't have partners
        }

        tBirths += bornAtTS;
        System.out.print(bornAtTS + "\t");

    }

    private MotherSet selectMothers(Config config, Collection<IPerson> females, int numberOfChildren,
                                    PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate,
                                    CompoundTimeUnit consideredTimePeriod, Population population) throws InsufficientNumberOfPeopleException {

        Collection<IPerson> needPartners = new ArrayList<>();
        Collection<IPerson> havePartners = new ArrayList<>();

        if(females.size() == 0) {
            return new MotherSet(havePartners, needPartners);
        }

        ArrayList<IPerson> femalesAL = new ArrayList<>(females);

        int ageOfMothers = femalesAL.get(0).ageOnDate(currentDate);

        MultipleDeterminedCount requiredBirths = calcNumberOfPreganciesOfMultipleBirth(ageOfMothers, numberOfChildren,
                desiredPopulationStatistics, currentDate, consideredTimePeriod);

        int childrenMade = 0;

        for(IPerson f : femalesAL) {

            if(childrenMade >= numberOfChildren) {
                break;
            }

            if(eligible(f, config, currentDate)) {
                f.giveChildren(1, currentDate, consideredTimePeriod, population);
                f.getLastChild().getParentsPartnership().setFather(BirthLogic.getRandomFather(population, population.getLivingPeople().resolveDateToCorrectDivisionDate(f.getBirthDate()), consideredTimePeriod));
                needPartners.add(f);
                childrenMade ++;
            }
        }

        return new MotherSet(havePartners, needPartners);

    }

    private MultipleDeterminedCount calcNumberOfPreganciesOfMultipleBirth(int ageOfMothers, int numberOfChildren, PopulationStatistics desiredPopulationStatistics, AdvancableDate currentDate,
                                                                          CompoundTimeUnit consideredTimePeriod) {

        MultipleBirthStatsKey key = new MultipleBirthStatsKey(ageOfMothers, numberOfChildren, consideredTimePeriod, currentDate);

        desiredPopulationStatistics.getDeterminedCount(key);


        return null;
    }

    private boolean eligible(IPerson potentialMother, Config config, Date currentDate) {

        IPerson lastChild = potentialMother.getLastChild();

        if(lastChild != null) {
            ExactDate earliestDateOfNextChild = DateUtils.
                    calculateExactDate(lastChild.getBirthDate(), config.getMinBirthSpacing());

            // Returns true if last child was born far enough in the past for another child to be born at currentDate
            return DateUtils.dateBefore(earliestDateOfNextChild, currentDate);
        } else {
            // i.e. there is no previous child and thus no limitation to birth
            return true;
        }
    }
}
