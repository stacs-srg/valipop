package uk.ac.standrews.cs.digitising_scotland.linkage.normalisation;

/**
 * Created by al on 06/12/2016.
 */
public class PlaceNormalisation {

    private static String notgiven = "ng";

    /**
     * @param input the text to normalise
     * @return that text representation of the place in a standard form
     */
    public static String normalisePace(String input) {

        input = stripRubbish(input).toLowerCase();

        if (input.equals("")) {
            return notgiven;
        }
        return input;
    }

    private static String stripRubbish(String input) {

        if (input.contains("[")) {
            input = input.substring(0, input.indexOf("["));
        }
        if (input.contains("(")) {
            input = input.substring(0, input.indexOf("("));
        }
        return input;
    }
}
