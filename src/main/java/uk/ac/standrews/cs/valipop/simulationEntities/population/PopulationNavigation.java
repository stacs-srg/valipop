package uk.ac.standrews.cs.valipop.simulationEntities.population;

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class PopulationNavigation {

    private static final int NUMBER_OF_GENERATIONS_TO_EXCLUDE = 3;

    public static Collection<IPerson> siblingsOf(IPerson person) {

        // Include half-siblings.

        final Collection<IPerson> siblings = new TreeSet<>();
        final IPartnership parents = person.getParentsPartnership();

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

            IPartnership parentsPartnership = person.getParentsPartnership();

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
}
