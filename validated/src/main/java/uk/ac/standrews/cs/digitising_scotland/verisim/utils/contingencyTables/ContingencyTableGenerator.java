package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ContingencyTableGenerator {

    private TableNode<String,       // Root
            TableNode<Boolean,      // Synthetic / Statistics
            TableNode<YearDate,     // Year
            TableNode<Boolean,      // Male / Female
            TableNode<Integer,      // Age
            TableNode<Boolean,      // Died / Alive
            TableNode<Integer,      // Previous children in years first active partnership
            TableNode<Integer,      // Children in year
            TableNode<Boolean,      // Separated in year
            TableNode<IntegerRange, // Current Partner age / none
            TableNode<IntegerRange, // Previous Partner age / none (if two partners in year)
                    ?>>>>>>>>>>>     // Count
                        tree = new TableNode<>("ROOT");

    private TableNode<String, Boolean> t = new TableNode<>("ROOT");

    public ContingencyTableGenerator(IPopulation population, PopulationStatistics expectedStatistics,
                                     Date startDate, Date endDate) {

        // For every year
        for (YearDate y = startDate.getYearDate(); DateUtils.dateBefore(y, endDate);
             y = y.advanceTime(1, TimeUnit.YEAR).getYearDate()) {

            // for every person in population
            for(IPerson person : population.getPeople()) {

                // who was alive or died in the year of consideration
                if(person.aliveInYear(y)) {

                }


            }



        }

    }

    public void addPersonToTree(IPerson person, Boolean synthetic, YearDate y) {

        TableNode node;

        // Get Synthetic / Statistics node

        try {
            node = t.getChild(synthetic);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = t.addChild(synthetic);
        }

        // Get node for year of birth

        YearDate birthYear = person.getBirthDate().getYearDate();
        try {
            node = node.getChild(birthYear);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(birthYear);
        }

        // Get node for sex

        boolean sex = convertSexToBoolean(person.getSex());
        try {
            node = node.getChild(sex);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(sex);
        }

        // Get node for age

        Integer age = person.ageOnDate(y);
        try {
            node = node.getChild(age);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(age);
        }

        // Get node for Died / Alive

        boolean died = person.diedInYear(y);
        try {
            node = node.getChild(died);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(died);
        }

        Collection<IPartnership> partnershipsFromYear = person.getPartnershipsActiveInYear(y);

        if(partnershipsFromYear.size() > 1) {
            if(sex) {
                System.out.println("Male multiple active partners in year: " + partnershipsFromYear);
            } else {
                System.out.println("Female multiple active partners in year: " + partnershipsFromYear);
            }
        }

        IPartnership activePartnership = getActivePartnership(partnershipsFromYear);
        int childrenInActivePartnership = activePartnership.getChildren().size();

        try {
            node = node.getChild(childrenInActivePartnership);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(childrenInActivePartnership);
        }

        Integer childrenBirthedInYear = getChildrenBirthedInYear(activePartnership, y);

        Boolean activePartnershipToEndInYear = null;
        if(childrenBirthedInYear != 0) {
            activePartnershipToEndInYear = isPartnershipToEndInYear(activePartnership, y);
        }

        try {
            node = node.getChild(activePartnershipToEndInYear);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(activePartnershipToEndInYear);
        }

        IntegerRange prevChildrennewPartnerAge = null;
        if(startedInYear(activePartnership, y)) {
            IPerson partner = activePartnership.getPartnerOf(person);
            int partnerAge = partner.ageOnDate(activePartnership.getPartnershipDate());
            newPartnerAge = resolveToRange(partnerAge);
        }

        try {
            node = node.getChild(newPartnerAge);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(newPartnerAge);
        }

        Collection<IPartnership> allPartnerships = person.getPartnerships();
        Integer prevChildren = sumChildrenBirthedFromPrevYears(partnershipsInPastYears, y);

        try {
            node = node.getChild(prevChildren);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(prevChildren);
        }

        try {
            node = node.getChild(childrenBirthedInYear);
        } catch (IsLeafException | ChildNotFoundException e) {
            node = node.addChild(childrenBirthedInYear);
        }

        node.incrementCount(1);


    }

    private Boolean isPartnershipToEndInYear(IPartnership activePartnership, YearDate y) {



    }

    private Integer getChildrenBirthedInYear(IPartnership activePartnership, YearDate year) {

        Collection<IPerson> children = activePartnership.getChildren();

        int c = 0;

        for(IPerson child : children) {
            if(child.bornInYear(year)) {
                c++;
            }
        }

        return c;

    }

    private IPartnership getActivePartnership(Collection<IPartnership> partnershipsFromYear) {
        return null;
    }

    private Boolean convertSexToBoolean(char sex) {

        char c = Character.toUpperCase(sex);

        if(c == 'M') {
            return true;
        } else if(c == 'F') {
            return false;
        }

        // Incorrect sex assigned
        throw new Error();

    }

}
