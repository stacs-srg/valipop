package uk.ac.standrews.cs.valipop.implementations;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.Partnership;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Address;
import uk.ac.standrews.cs.valipop.utils.addressLookup.ForeignGeography;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Geography;

import java.time.LocalDate;
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

    public void performMigration(LocalDate currentTime) {

        int numberToMigrate = Math.toIntExact(Math.round(population.getLivingPeople().getNumberOfPeople() * getMigationRate(currentTime)));
        Collection<List<IPerson>> peopleToMigrate = new ArrayList<>();

        // select people to move out of country
        while(peopleToMigrate.size() < numberToMigrate) {

            List<IPerson> livingPeople = new ArrayList(population.getLivingPeople().getPeople());
            IPerson selected = livingPeople.get(randomNumberGenerator.nextInt(livingPeople.size()));

            LocalDate moveDate = getmoveDate(currentTime, selected);
            Address currentAbode = selected.getAddress(moveDate);
            boolean withHousehold = migrateWithHousehold(currentTime, selected, currentAbode);

            List<IPerson> household = new ArrayList<>();

            if(withHousehold) {
                Address emigrateTo = null;

                while(!currentAbode.getInhabitants().isEmpty()) {
                    emigrateTo = emigratePerson(moveDate, currentAbode, household, currentAbode.getInhabitants().get(0), emigrateTo);
                }

                peopleToMigrate.add(household);

            } else {
                emigratePerson(moveDate, currentAbode, household, selected);
                peopleToMigrate.add(household);
            }

        }

        // TODO add in emmigrate from address!

        // create immigrants by approximately mimicing the emigrants
        for(List<IPerson> household : peopleToMigrate) {

            if(household.size() == 1) {
                IPerson toMimic = household.get(0);
                IPerson mimic = mimicPerson(toMimic, null);
                population.getLivingPeople().add(mimic);
                mimic.setImmigrationDate(toMimic.getEmigrationDate());
                mimic.setAddress(toMimic.getEmigrationDate(), geography.getRandomEmptyAddress());
            } else {

                Map<IPartnership, IPartnership> mimicLookup = new HashMap<>();
                Map<IPerson, IPerson> mimicPersonLookup = new HashMap<>();

                household.sort((o1, o2) -> o1.getBirthDate().isBefore(o2.getBirthDate()) ? -1 : 1);

                Address newHouse = geography.getRandomEmptyAddress();

                for(IPerson p : household) {

                    IPerson mimic = mimicPersonLookup.get(p);

                    if(mimic == null) {

                        IPartnership mimicParents = mimicLookup.get(p.getParents());

                        if (mimicParents == null) {
                            mimic = mimicPerson(p, null);

                            mimicLookup.put(p.getParents(), mimic.getParents());

                            if(p.getParents() != null) {
                                if(p.getParents().getMalePartner() != null)
                                    mimicPersonLookup.put(p.getParents().getMalePartner(), mimic.getParents().getMalePartner());

                                if(p.getParents().getFemalePartner() != null)
                                    mimicPersonLookup.put(p.getParents().getFemalePartner(), mimic.getParents().getFemalePartner());
                            }

                        } else {
                            mimic = mimicPerson(p, mimicParents);
                        }

                    } else {

                        if(mimic.getParents() == null) {
                            mimic.setParents(mimicParents(p));
                        }

                    }

                    population.getLivingPeople().add(mimic);
                    LocalDate arrivalDate = p.getEmigrationDate().isBefore(mimic.getBirthDate()) ? mimic.getBirthDate() : p.getEmigrationDate();
                    mimic.setImmigrationDate(arrivalDate);
                    mimic.setAddress(arrivalDate, newHouse);

                }
            }
        }
    }

    private Address emigratePerson(LocalDate moveDate, Address currentAbode, List<IPerson> emigratingGroup, IPerson person) {
        return emigratePerson(moveDate, currentAbode, emigratingGroup, person, null);
    }

    private Address emigratePerson(LocalDate moveDate, Address currentAbode, List<IPerson> emigratingGroup, IPerson person, Address emigrateTo) {
        population.getLivingPeople().remove(person);
        population.getEmigrants().add(person);
        person.setEmigrationDate(moveDate.isBefore(person.getBirthDate()) ? person.getBirthDate() : moveDate);
        if(currentAbode != null) currentAbode.removeInhabitant(person);
        emigratingGroup.add(person);

        Address newCountry = emigrateTo;
        if(newCountry == null) newCountry = foreignGeography.getCountry(person);

        person.setAddress(moveDate, newCountry);
        return newCountry;
    }

    private LocalDate getmoveDate(LocalDate currentDate, IPerson person) {
        LocalDate moveDate;
        LocalDate lastMoveDate = person.getLastMoveDate();

        if(lastMoveDate != null && lastMoveDate.isAfter(currentDate)) {
            int excludedDays = currentDate.until(lastMoveDate).getDays();
            moveDate = lastMoveDate.plus(randomNumberGenerator.nextInt(365 - excludedDays), ChronoUnit.DAYS);
        } else {
            moveDate = currentDate.plus(randomNumberGenerator.nextInt(365), ChronoUnit.DAYS);
        }
        return moveDate;
    }

    private IPerson mimicPerson(IPerson person, IPartnership mimicedParents) {

        boolean illegitimate = person.isIllegitimate();
        LocalDate birthDate = randomDateInYear(person.getBirthDate());
        IPartnership parents = mimicedParents;

        if(parents == null) {
            parents = mimicParents(person);
        }

        return personFactory.makePerson(birthDate, parents, illegitimate, true);

    }

    private IPartnership mimicParents(IPerson person) {
        IPartnership parents = null;
        IPartnership parentsToMimic = person.getParents();

        if(parentsToMimic != null) {
            IPerson fatherToMimic = parentsToMimic.getMalePartner();
            IPerson motherToMimic = parentsToMimic.getFemalePartner();

            IPerson mimicedFather = personFactory.makePerson(randomDateInYear(fatherToMimic.getBirthDate()), null, fatherToMimic.isIllegitimate(), true);
            IPerson mimicedMother = personFactory.makePerson(randomDateInYear(motherToMimic.getBirthDate()), null, motherToMimic.isIllegitimate(), true);

            parents = new Partnership(mimicedFather, mimicedMother);
        }

        return parents;
    }

    private LocalDate randomDateInYear(LocalDate birthDate) {
        int year = birthDate.getYear();
        int day = randomNumberGenerator.nextInt(365);

        return LocalDate.of(year, 1, 1).plus(day, ChronoUnit.DAYS);
    }

    private double getMigationRate(LocalDate currentTime) {
        return 0.01;
    }

    private boolean migrateWithHousehold(LocalDate currentDate, IPerson person, Address address) {

        boolean withHousehold = false;

        if(address != null && address.getInhabitants().size() > 1) {
            withHousehold = randomNumberGenerator.nextBoolean();
        }

        return withHousehold;
    }

}
