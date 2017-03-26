package uk.ac.standrews.cs.digitising_scotland.linkage.normalisation;

/**
 * Created by al on 06/12/2016.
 */
public class DateNormalisation {

    /**
     * @param input the text to normalise
     * @return that text representation of the month in a standard form
     */
    public static String normaliseMonth(String input) {

        input = stripRubbish(input).toLowerCase();

        if (input.equals("january") || input.equals("1") || input.equals("jan"))
            return "jan";
        if (input.equals("february") || input.equals("2") || input.equals("feb"))
            return "feb";
        if (input.equals("march") || input.equals("3") || input.equals("mar"))
            return "mar";
        if (input.equals("april") || input.equals("4") || input.equals("apr"))
            return "apr";
        if (input.equals("may") || input.equals("5"))
            return "may";
        if (input.equals("june") || input.equals("6") || input.equals("jun"))
            return "jun";
        if (input.equals("july") || input.equals("7") || input.equals("jul"))
            return "mar";
        if (input.equals("august") || input.equals("8") || input.equals("aug"))
            return "aug";
        if (input.equals("september") || input.equals("9") || input.equals("sep") || input.equals("sept"))
            return "sep";
        if (input.equals("october") || input.equals("10") || input.equals("oct"))
            return "oct";
        if (input.equals("november") || input.equals("11") || input.equals("nov"))
            return "nov";
        if (input.equals("december") || input.equals("12") || input.equals("dec"))
            return "dec";

        throw new RuntimeException("Unrecognized month: " + input);
    }

    /**
     * @param input the text to normalise
     * @return that text representation of the month in a standard form
     */
    public static String normaliseDay(String input) {

        input = stripRubbish(input).toLowerCase();

        if (input.equals("monday") || input.equals("1") || input.equals("mon"))
            return "mon";
        if (input.equals("tuesday") || input.equals("2") || input.equals("tue"))
            return "tue";
        if (input.equals("wednesday") || input.equals("3") || input.equals("wed"))
            return "wed";
        if (input.equals("thursday") || input.equals("4") || input.equals("thu"))
            return "thu";
        if (input.equals("friday") || input.equals("5") || input.equals("fri"))
            return "fri";
        if (input.equals("saturday") || input.equals("6") || input.equals("sat"))
            return "sat";
        if (input.equals("sunday") || input.equals("7") || input.equals("sun"))
            return "sun";

        throw new RuntimeException("Unrecognized day: " + input);
    }

    private static String stripRubbish(String input) {
        if (input.contains(" ")) {
            input = input.substring(0, input.indexOf(" "));
        }
        if (input.contains("[")) {
            input = input.substring(0, input.indexOf("["));
        }
        if (input.contains("(")) {
            input = input.substring(0, input.indexOf("("));
        }
        return input;
    }

    public static String cleanDate(String day, String month, String year) {

        return cleanDay(day) + DATE_SEPARATOR + cleanMonth(month) + DATE_SEPARATOR + cleanYear(year);
    }

    private static String cleanDay(final String day) {

        if (notGiven(day)) {
            return BLANK_DAY;
        }

        try {
            String d = String.valueOf(Integer.parseInt(day));

            if (d.length() == 1) {
                return "0" + d;
            }
            return d;
        } catch (NumberFormatException e) {
            return BLANK_DAY;
        }
    }

    private static String cleanMonth(final String month) {

        if (notGiven(month)) {
            return BLANK_MONTH;
        }

        if (month.length() > 3) {
            return month.substring(0, 3);
        }
        return month;
    }

    private static String cleanYear(final String year) {

        if (notGiven(year)) {
            return BLANK_YEAR;
        }

        try {
            int i = Integer.parseInt(year);
            if (i > 10) {
                i += 1800;
            } else {
                i += 1900;
            }
            return String.valueOf(i);
        } catch (NumberFormatException e) {
            return BLANK_YEAR;
        }
    }

    private static boolean notGiven(final String field) {

        return field.equals("") || field.equals("na") || field.equals("ng");
    }

    private static final String DATE_SEPARATOR = "/";

    private static final String BLANK_DAY = "--";
    private static final String BLANK_MONTH = "---";
    private static final String BLANK_YEAR = "----";

}
