package uk.ac.standrews.cs.digitising_scotland.verisim.annotations.names;


import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPerson;

public class FirstNameGenerator implements NameGenerator {

    @Override
    public String getName(IPerson personToBeNamed) {

        // OZGUR - forename stuff handled here

        return "John";
    }

}
