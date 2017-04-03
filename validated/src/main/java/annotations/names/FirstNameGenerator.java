package annotations.names;


import simulationEntities.person.IPerson;

public class FirstNameGenerator implements NameGenerator {

    @Override
    public String getName(IPerson personToBeNamed) {

        // OZGUR - forename stuff handled here

        return "John";
    }

}
