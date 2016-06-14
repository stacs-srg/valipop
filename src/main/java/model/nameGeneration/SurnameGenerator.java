package model.nameGeneration;

import model.IPerson;

/**
 * @author Özgür Akgün
 */
public class SurnameGenerator implements NameGenerator {

    @Override
    public String getName(IPerson personToBeNamed) {

        // OZGUR - surname stuff handled here

        return "Hancock";
    }
}
