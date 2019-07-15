package uk.ac.standrews.cs.valipop.implementations;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.utilities.DateManipulation;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.PersonNotFoundException;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Address;
import uk.ac.standrews.cs.valipop.utils.addressLookup.ForeignGeography;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Geography;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BalancedMigrationModel {

    private final Population population;
    private final RandomGenerator randomNumberGenerator;
    private final Geography geography;

    private final ForeignGeography foreignGeography;

    private final PersonFactory personFactory;
    private final PopulationStatistics desired;

    public BalancedMigrationModel(Population population, RandomGenerator randomNumberGenerator, Geography geography, PersonFactory personFactory, PopulationStatistics desired) {
        this.population = population;
        this.randomNumberGenerator = randomNumberGenerator;
        this.geography = geography;
        this.personFactory = personFactory;
        this.desired = desired;
        foreignGeography = new ForeignGeography(randomNumberGenerator);
    }

    public void performMigration(LocalDate currentTime, OBDModel model) {

        int numberToMigrate = Math.toIntExact(Math.round(population.getLivingPeople().getNumberOfPeople()
                * model.getDesiredPopulationStatistics()
                .getMigrationRateDistribution(Year.of(currentTime.getYear()))
                .getRate(0)));

        Collection<List<IPerson>> peopleToMigrate = new ArrayList<>();

        // select people to move out of country
        while(peopleToMigrate.size() < numberToMigrate) {

            List<IPerson> livingPeople = new ArrayList(population.getLivingPeople().getPeople());
            IPerson selected = livingPeople.get(randomNumberGenerator.nextInt(livingPeople.size()));

            LocalDate moveDate = getMoveDate(currentTime, selected);
            Address currentAbode = selected.getAddress(moveDate);
            boolean withHousehold = migrateWithHousehold(currentTime, selected, currentAbode);

            List<IPerson> household = new ArrayList<>();

            if(withHousehold) {
                Address emigrateTo = null;

                while(!currentAbode.getInhabitants().isEmpty()) {
                    IPerson person = currentAbode.getInhabitants().get(0);

                    IPerson lastChild = PopulationNavigation.getLastChild(person);

                    LocalDate personalMoveDate = moveDate;

                    if(lastChild != null && !household.contains(lastChild) && !currentAbode.getInhabitants().contains(lastChild) && lastChild.getDeathDate() == null) {
                        // if emigrating persons last child exists and is not emigrating in this group and is not dead
                        // we need to make sure that this last child was not conceived after the father left
                        personalMoveDate = checkConceptionBeforeMove(currentTime, moveDate, lastChild, personalMoveDate);
                    }
                    emigrateTo = emigratePerson(personalMoveDate, currentAbode, household, person, emigrateTo, model);
                }

                peopleToMigrate.add(household);

            } else {
                IPerson lastChild = PopulationNavigation.getLastChild(selected);

                LocalDate personalMoveDate = moveDate;

                if(lastChild != null && lastChild.getDeathDate() == null) {
                    // if emigrating persons last child exists and is not dead
                    // we need to make sure that this last child was not conceived after the father left
                    personalMoveDate = checkConceptionBeforeMove(currentTime, moveDate, lastChild, personalMoveDate);
                }

                emigratePerson(personalMoveDate, currentAbode, household, selected, model);
                peopleToMigrate.add(household);
            }

        }

        // create immigrants by approximately mimicing the emigrants
        for(List<IPerson> household : peopleToMigrate) {

            if(household.size() == 1) {
                IPerson toMimic = household.get(0);
                IPerson mimic = mimicPerson(toMimic, null, new HashMap<>());

                immigratePerson(mimic, toMimic);

            } else {

                Map<IPartnership, IPartnership> mimicLookup = new HashMap<>();
                Map<IPerson, IPerson> mimicPersonLookup = new HashMap<>();

                household.sort((o1, o2) -> o1.getBirthDate().isBefore(o2.getBirthDate()) ? 1 : -1);

                Address newHouse = geography.getRandomEmptyAddress();
                Address oldCountry = foreignGeography.getCountry();

                for(IPerson p : household) {

                    IPerson mimic = mimicPersonLookup.get(p);

                    if(mimic == null) {

                        IPartnership mimicParents = mimicLookup.get(p.getParents());

                        if (mimicParents == null) {
                            mimic = mimicPerson(p, null, mimicPersonLookup);

                            mimicLookup.put(p.getParents(), mimic.getParents());

                            if(p.getParents() != null) {
                                if(p.getParents().getMalePartner() != null)
                                    mimicPersonLookup.put(p.getParents().getMalePartner(), mimic.getParents().getMalePartner());

                                if(p.getParents().getFemalePartner() != null)
                                    mimicPersonLookup.put(p.getParents().getFemalePartner(), mimic.getParents().getFemalePartner());
                            }

                        } else {
                            mimic = mimicPerson(p, mimicParents, mimicPersonLookup);
                        }

                    } else {

                        if(mimic.getParents() == null) {
                            mimic.setParents(mimicParents(p, mimicPersonLookup));
                        }

                    }

                    population.getLivingPeople().add(mimic);
                    LocalDate arrivalDate = p.getEmigrationDate().isBefore(mimic.getBirthDate()) ? mimic.getBirthDate() : p.getEmigrationDate();

                    if(mimic.getDeathDate() != null && arrivalDate.isAfter(mimic.getDeathDate())) {
                        System.out.println("FIX - IMMIGRATION DEATH ORDERING");
                    }

                    mimic.setImmigrationDate(arrivalDate);

                    if(arrivalDate.isAfter(mimic.getBirthDate())) {
                        mimic.setAddress(mimic.getBirthDate(), oldCountry);
                    }

                    mimic.setAddress(arrivalDate, newHouse);

                }

            }
        }
    }

    private LocalDate checkConceptionBeforeMove(LocalDate currentTime, LocalDate moveDate, IPerson lastChild, LocalDate personalMoveDate) {
        LocalDate conception = lastChild.getBirthDate().minus(desired.getMinGestationPeriod());
        if (moveDate.isBefore(conception)) {
            int windowInDays = (int) conception.until(currentTime.plus(1, ChronoUnit.YEARS), ChronoUnit.DAYS) - 1;

            personalMoveDate = conception.plus(randomNumberGenerator.nextInt(windowInDays), ChronoUnit.DAYS);
        }
        return personalMoveDate;
    }

    private void immigratePerson(IPerson person, IPerson toMimic) {
        population.getLivingPeople().add(person);
        LocalDate arrivalDate = toMimic.getEmigrationDate().isBefore(person.getBirthDate()) ? person.getBirthDate() : toMimic.getEmigrationDate();
        person.setImmigrationDate(arrivalDate);

        if(arrivalDate.isAfter(person.getBirthDate())) {
            person.setAddress(person.getBirthDate(), foreignGeography.getCountry());
        }

        person.setAddress(arrivalDate, geography.getRandomEmptyAddress());

    }

    private Address emigratePerson(LocalDate moveDate, Address currentAbode, List<IPerson> emigratingGroup, IPerson person, OBDModel model) {
        return emigratePerson(moveDate, currentAbode, emigratingGroup, person, null, model);
    }

    private Address emigratePerson(LocalDate moveDate, Address currentAbode, List<IPerson> emigratingGroup, IPerson person, Address emigrateTo, OBDModel model) {

        try {
            population.getLivingPeople().remove(person);
        } catch (PersonNotFoundException e) {
            System.out.println();
        }
        population.getEmigrants().add(person);
        person.setEmigrationDate(moveDate.isBefore(person.getBirthDate()) ? person.getBirthDate() : moveDate);

        if(person.getDeathDate() != null && person.getEmigrationDate().isAfter(person.getDeathDate())) {
            System.out.println("FIX - EMIGRATION DEATH ORDERING - DEAD BEFORE LEFT");
        }

        if(currentAbode != null) currentAbode.removeInhabitant(person);
        emigratingGroup.add(person);

        Address newCountry = emigrateTo;
        if(newCountry == null) newCountry = foreignGeography.getCountry();

        person.setAddress(moveDate, newCountry);
//        model.handleSeperationMoves(person.getLastPartnership(), person);

        return newCountry;
    }

    private LocalDate getMoveDate(LocalDate currentDate, IPerson person) {
        LocalDate moveDate;
        LocalDate lastMoveDate = person.getLastMoveDate();

        if(lastMoveDate != null && lastMoveDate.isAfter(currentDate)) {
            int excludedDays = (int) ChronoUnit.DAYS.between(currentDate, lastMoveDate);
//            int excludedDays = currentDate.until(lastMoveDate).getDays();

            moveDate = lastMoveDate.plus(randomNumberGenerator.nextInt(365 - excludedDays), ChronoUnit.DAYS);
        } else {
            moveDate = currentDate.plus(randomNumberGenerator.nextInt(365), ChronoUnit.DAYS);
        }
        return moveDate;
    }

    private IPerson mimicPerson(IPerson person, IPartnership mimicedParents, Map<IPerson, IPerson> mimicPersonLookup) {

        boolean illegitimate = person.isIllegitimate();
        LocalDate birthDate = randomDateInYear(person.getBirthDate());
        IPartnership parents = mimicedParents;

        if(parents == null) {
            parents = mimicParents(person, mimicPersonLookup);
        }

        IPerson p = personFactory.makePerson(birthDate, parents, illegitimate, true, person.getSex());

        if(parents != null)
            parents.addChildren(Collections.singleton(p));

//        System.out.println(ChronoUnit.DAYS.between(p.getBirthDate(), person.getBirthDate()));

        return p;

    }

    private IPartnership mimicParents(IPerson person, Map<IPerson, IPerson> mimicPersonLookup) {
        IPartnership parents = null;
        IPartnership parentsToMimic = person.getParents();

        if(parentsToMimic != null) {
            IPerson fatherToMimic = parentsToMimic.getMalePartner();
            IPerson motherToMimic = parentsToMimic.getFemalePartner();

            IPerson mimicedFather = mimicPersonLookup.keySet().contains(fatherToMimic) ? mimicPersonLookup.get(fatherToMimic) : personFactory.makePerson(randomDateInYear(fatherToMimic.getBirthDate()), null, fatherToMimic.isIllegitimate(), true, SexOption.MALE);
            IPerson mimicedMother = mimicPersonLookup.keySet().contains(motherToMimic) ? mimicPersonLookup.get(motherToMimic) : personFactory.makePerson(randomDateInYear(motherToMimic.getBirthDate()), null, motherToMimic.isIllegitimate(), true, SexOption.FEMALE);

            parents = new Partnership(mimicedFather, mimicedMother);

            mimicedFather.recordPartnership(parents);
            mimicedMother.recordPartnership(parents);
        }

        return parents;
    }

    private LocalDate randomDateInYear(LocalDate birthDate) {
        int year = birthDate.getYear();
        int day = randomNumberGenerator.nextInt(365);

        return LocalDate.of(year, 1, 1).plus(day, ChronoUnit.DAYS);
    }

    private boolean migrateWithHousehold(LocalDate currentDate, IPerson person, Address address) {

        boolean withHousehold = false;

        if(address != null && address.getInhabitants().size() > 1) {
            withHousehold = randomNumberGenerator.nextBoolean();
        }

        return withHousehold;
    }

}
