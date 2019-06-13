package uk.ac.standrews.cs.valipop.implementations;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.PersonCollection;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.MultipleDeterminedCountByString;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.OccupationChangeStatsKey;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OccupationChangeModel {

    private final PopulationStatistics desired;
    private final Population population;
    private final Config config;

    public OccupationChangeModel(Population population, PopulationStatistics desired, Config config) {
        this.population = population;
        this.desired = desired;
        this.config = config;
    }

    public void performOccupationChange(LocalDate onDate) {

        // for each sex
        occupationChangeFor(SexOption.MALE, onDate);
        occupationChangeFor(SexOption.FEMALE, onDate);

    }

    private void occupationChangeFor(SexOption sex, LocalDate onDate) {

        PersonCollection people;

        if(sex == SexOption.MALE)
            people = population.getLivingPeople().getMales();
        else
            people = population.getLivingPeople().getFemales();

        // This one at a time approach is inefficent (but linear) - searching each time so as to group by occupation makes in squared
        // if we need optomisation then a faster linear way would be to store everone in the same job in a supporting data structure

        // for all people of sex
        for(IPerson person : people) {
            // if persons age is divisible by 10
            if(PopulationNavigation.ageOnDate(person, onDate) % 10 == 0) {
                // then get last occupation
                String occupation = person.getLastOccupation();

                // use to get new occuption
                OccupationChangeStatsKey key = new OccupationChangeStatsKey(occupation, 1, Period.ofYears(10), onDate, sex);
                MultipleDeterminedCountByString mDC = (MultipleDeterminedCountByString) desired.getDeterminedCount(key, config);

                // this for loop is looking for the non zero value in the set of which there is either 1 or 0 - we could optomise this by using an OrderByValueLabelledValueSet (which somebody would first need to implement...)
                for(String label : mDC.getDeterminedCount().getLabels()) {
                    if(mDC.getDeterminedCount().get(label) != 0) {
                        // if not same as last then update occupation history
                        if(!label.equals(occupation)) {
                            person.setOccupation(onDate, label);
                        }
                        // else do nothing - it's the same job, it doesn't need multiple entries in the occupation history
                        break;
                    }
                    // if we get to here without ever breaking then it inidcates the previous occupation isn't in the occupation change data
                    // current sim behaviour is that the person sticks with this occuption - this may change next time round if the next data input for occupation change features the previous occupation
                }

                mDC.setFulfilledCount(mDC.getDeterminedCount());
                desired.returnAchievedCount(mDC);
            }
        }
    }
}
