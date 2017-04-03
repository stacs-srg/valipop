package annotations.names;

import simulationEntities.person.IPerson;

public class SurnameGenerator implements NameGenerator {

    @Override
    public String getName(IPerson personToBeNamed) {

        // OZGUR - surname stuff handled here

        return "Hancock";
    }
}
