package simulationEntities.population;

import simulationEntities.person.IPerson;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationCounts {

    private int createdMales = 0;
    private int createdFemales = 0;

    private int livingMales = 0;
    private int livingFemales = 0;

    private int createdPartnerships = 0;
    private int currentPartnerships = 0;

    private int maxPopulation = 0;

    private int illegitimateBirths = 0;

    public void newMale(int numberOf) {
        createdMales += numberOf;
        livingMales += numberOf;
    }

    public void newMale() {
        newMale(1);
    }

    public void newFemale(int numberOf) {
        createdFemales += numberOf;
        livingFemales += numberOf;
    }

    public void newFemale() {
        newFemale(1);
    }

    public void newPartnership(int numberOf) {
        createdPartnerships += numberOf;
        currentPartnerships += numberOf;
    }

    public void newPartnership() {
        newPartnership(1);
    }

    public void maleDeath(int numberOf) {
        livingMales -= numberOf;
    }

    public void maleDeath() {
        maleDeath(1);
    }

    public void femaleDeath(int numberOf) {
        livingFemales -= numberOf;
    }

    public void femaleDeath() {
        femaleDeath(1);
    }

    public void death(IPerson deceased) {
        if(Character.toLowerCase(deceased.getSex()) == 'm') {
            maleDeath();
        } else {
            femaleDeath();
        }
    }

    public void newIllegitimateBirth(int numberOf) {
        illegitimateBirths += numberOf;
    }

    public void newIllegitimateBirth() {
        newIllegitimateBirth(1);
    }

    public void partnershipEnd(int numberOf) {
        currentPartnerships -= numberOf;
    }

    public void partnershipEnd() {
        partnershipEnd(1);
    }

    public int getLivingMales() {
        return livingMales;
    }

    public int getLivingFemales() {
        return livingFemales;
    }

    public int getCurrentPartnerships() {
        return currentPartnerships;
    }

    public double getLivingSexRatio() {
        return livingMales / (double) livingFemales;
    }

    public void updateMaxPopulation(int populationSize) {
        if(populationSize > maxPopulation) {
            maxPopulation = populationSize;
        }
    }

    public int getCreatedPeople() {
        return createdFemales + createdMales;
    }

    public int getIllegitimateBirths() {
        return illegitimateBirths;
    }
}
