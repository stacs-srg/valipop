package model.nameGeneration;

import model.IPerson;

/**
 * @author Özgür Akgün
 */
public class FirstNameGenerator implements NameGenerator {

    @Override
    public String getName(IPerson personToBeNamed) {

        // OZGUR - forename stuff handled here

        return "John";
    }

}
