package uk.ac.standrews.cs.valipop.simulationEntities.population;

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class PopulationNavigation {

    private static final int NUMBER_OF_GENERATIONS_TO_EXCLUDE = 3;

    public static Collection<IPerson> siblingsOf(IPerson person) {

        // Include half-siblings.

        final Collection<IPerson> siblings = new TreeSet<>();
        final IPartnership parents = person.getParents();

        if (parents != null) {

            siblings.addAll(parents.getMalePartner().getAllChildren());
            siblings.addAll(parents.getFemalePartner().getAllChildren());
        }

        return siblings;
    }

    private static Collection<IPerson> siblingsOf(IPerson person, SexOption sex) {

        Collection<IPerson> siblings = siblingsOf(person);
        siblings.removeIf(p -> p.getSex() != sex);
        return siblings;
    }

    public static Collection<IPerson> sistersOf(IPerson person) {

        return siblingsOf(person, SexOption.FEMALE);
    }

    public static Collection<IPerson> brothersOf(IPerson person) {

        return siblingsOf(person, SexOption.MALE);
    }

    public static Collection<IPerson> partnersOf(IPerson person) {

        Collection<IPerson> partners = new TreeSet<>();

        for (IPartnership partnership : person.getPartnerships()) {
            partners.add(partnership.getPartnerOf(person));
        }
        return partners;
    }

    private static Collection<IPerson> descendantsOf(Collection<IPerson> people) {

        Collection<IPerson> descendants = new TreeSet<>();
        for (IPerson person : people) {
            descendants.addAll(descendantsOf(person));
        }
        return descendants;
    }

    public static Collection<IPerson> descendantsOf(IPerson person) {

        return descendantsOf(person, NUMBER_OF_GENERATIONS_TO_EXCLUDE);
    }

    public static Collection<IPerson> femaleDescendantsOf(Collection<IPerson> people) {

        Collection<IPerson> descendants = descendantsOf(people);
        descendants.removeIf(p -> p.getSex() != SexOption.FEMALE);
        return descendants;
    }

    public static Collection<IPerson> femaleDescendantsOf(IPerson person) {

        return descendantsOf(person, SexOption.FEMALE);
    }

    public static Collection<IPerson> maleDescendantsOf(IPerson person) {

        return descendantsOf(person, SexOption.MALE);
    }

    private static Collection<IPerson> descendantsOf(IPerson person, SexOption sex) {

        Collection<IPerson> descendants = descendantsOf(person);
        descendants.removeIf(p -> p.getSex() != sex);
        return descendants;
    }

    public static Collection<IPerson> ancestorsOf(IPerson person) {

        return ancestorsOf(person, NUMBER_OF_GENERATIONS_TO_EXCLUDE);
    }

    public static Collection<IPerson> femaleAncestorsOf(Collection<IPerson> people) {

        Collection<IPerson> ancestors = new ArrayList<>();
        for (IPerson person : people) {
            ancestors.addAll(femaleAncestorsOf(person));
        }
        return ancestors;
    }

    public static Collection<IPerson> femaleAncestorsOf(IPerson person) {

        return ancestorsOf(person, SexOption.FEMALE);
    }

    public static Collection<IPerson> maleAncestorsOf(IPerson person) {

        return ancestorsOf(person, SexOption.MALE);
    }

    private static Collection<IPerson> ancestorsOf(IPerson person, SexOption sex) {

        Collection<IPerson> ancestors = ancestorsOf(person);
        ancestors.removeIf(p -> p.getSex() != sex);
        return ancestors;
    }

    private static Collection<IPerson> descendantsOf(IPerson person, int generations) {

        Collection<IPerson> descendants = new TreeSet<>();

        if (generations > 0) {

            for (IPerson child : person.getAllChildren()) {

                descendants.add(child);
                descendants.addAll(descendantsOf(child, generations - 1));
            }
        }

        return descendants;
    }

    private static Collection<IPerson> ancestorsOf(IPerson person, int generations) {

        Collection<IPerson> ancestors = new TreeSet<>();

        if (generations > 0) {

            IPartnership parentsPartnership = person.getParents();

            if (parentsPartnership != null) {
                IPerson mother = parentsPartnership.getFemalePartner();
                IPerson father = parentsPartnership.getMalePartner();

                ancestors.add(mother);
                ancestors.add(father);

                ancestors.addAll(ancestorsOf(mother, generations - 1));
                ancestors.addAll(ancestorsOf(father, generations - 1));
            }
        }

        return ancestors;
    }

    public static Collection<IPerson> partnersOf(Collection<IPerson> people) {

        List<IPerson> partners = new ArrayList<>();

        for (IPerson person : people) {
            partners.addAll(partnersOf(person));
        }
        return partners;
    }

    public static boolean aliveOnDate(IPerson person, ValipopDate date) {

        if (DateUtils.dateBeforeOrEqual(person.getBirthDate(), date)) {

            ValipopDate deathDate = person.getDeathDate();
            return deathDate == null || DateUtils.dateBefore(date, deathDate);
        }
        return false;
    }

    public static IPerson getLastChild(IPerson person) {

        ValipopDate latestChildBirthDate = new YearDate(Integer.MIN_VALUE);
        IPerson child = null;

        for (IPartnership p : person.getPartnerships()) {
            for (IPerson c : p.getChildren()) {

                if (DateUtils.dateBeforeOrEqual(latestChildBirthDate, c.getBirthDate())) {
                    latestChildBirthDate = c.getBirthDate();
                    child = c;
                }
            }
        }

        return child;
    }

    public static boolean lastPartnerDied(IPerson person, ValipopDate currentDate) {

        try {
            IPerson lastPartner = getLastChild(person).getParents().getPartnerOf(person);
            return !aliveOnDate(lastPartner, currentDate);

        } catch (NullPointerException e) {
            return true;
        }
    }

    public static int ageOnDate(IPerson person, ValipopDate currentDate) {

        ValipopDate birthDate = person.getBirthDate();

        if (birthDate.getDay() == 1 && birthDate.getMonth() == 1) {
            int age = DateUtils.differenceInYears(birthDate, currentDate).getCount() - 1;
            return age == -1 ? 0 : age;
        } else {
            return DateUtils.differenceInYears(birthDate, currentDate).getCount();
        }
    }

    public static int numberOfChildrenInLatestPartnership(IPerson person) {
        return getLastChild(person).getParents().getChildren().size();
    }

    public static boolean bornInYear(IPerson person, YearDate year) {

        ValipopDate birthDate = person.getBirthDate();
        return birthDate != null && DateUtils.dateInYear(birthDate, year);
    }

    public static boolean diedInYear(IPerson person, YearDate year) {

        ValipopDate deathDate = person.getDeathDate();
        return deathDate != null && DateUtils.dateInYear(deathDate, year);
    }

    public static boolean diedAfter(IPerson person, ValipopDate date) {

        ValipopDate deathDate = person.getDeathDate();
        return deathDate == null || DateUtils.dateBefore(date, deathDate);
    }

    public static Collection<IPartnership> getPartnershipsActiveInYear(IPerson person, YearDate year) {

        Collection<IPartnership> activePartnerships = new ArrayList<>();

        for (IPartnership partnership : person.getPartnerships()) {
            ValipopDate startDate = partnership.getPartnershipDate();

            if (DateUtils.dateInYear(startDate, year)) {
                activePartnerships.add(partnership);
            } else {
                for (IPerson p : partnership.getChildren()) {
                    if (DateUtils.dateInYear(p.getBirthDate(), year)) {
                        activePartnerships.add(partnership);
                        break;
                    }
                }
            }
        }

        return activePartnerships;
    }

    public static IPartnership getLastPartnership(IPerson person) {

        ValipopDate latestPartnershipDate = new YearDate(Integer.MIN_VALUE);
        IPartnership partnership = null;

        for (IPartnership p : person.getPartnerships()) {
            if (DateUtils.dateBefore(latestPartnershipDate, p.getPartnershipDate())) {
                latestPartnershipDate = p.getPartnershipDate();
                partnership = p;
            }
        }
        return partnership;
    }

    public static Integer numberOfChildrenBirthedBeforeDate(IPerson person, YearDate y) {

        int count = 0;

        for (IPartnership p : person.getPartnerships()) {
            for (IPerson c : p.getChildren()) {
                if (DateUtils.dateBefore(c.getBirthDate(), y)) {
                    count++;
                }
            }
        }

        return count;
    }

    public static ValipopDate getDateOfNextPostSeparationEvent(IPerson person, ValipopDate separationDate) {

        ValipopDate earliestDate = null;

        for (IPartnership partnership : person.getPartnerships()) {
            ValipopDate date = partnership.getPartnershipDate();
            if (DateUtils.dateBefore(separationDate, date)) {

                if (earliestDate == null || DateUtils.dateBefore(date, earliestDate)) {
                    earliestDate = date;
                }
            }

            date = partnership.getMarriageDate();

            if (date != null) {
                if (DateUtils.dateBefore(separationDate, date)) {

                    if (earliestDate == null || DateUtils.dateBefore(date, earliestDate)) {
                        earliestDate = date;
                    }
                }
            }
        }

        if (earliestDate == null) {
            earliestDate = person.getDeathDate();
        }

        return earliestDate;
    }
}
