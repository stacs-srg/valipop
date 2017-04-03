package utils;

import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ProcessArgs {

    public static String[] process(String[] args) {

        String[] processed = new String[3];

        try {
            processed[0] = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("No config file given as 1st arg");
        }

        try {
            processed[1] = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("No results write path given as 2nd arg");
        }

        try {
            processed[2] = args[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            processed[2] = "unstated";
        }

        return processed;

    }

    public static boolean check(String[] args) {

        return args.length == 3 && !Objects.equals(args[0], "") && !Objects.equals(args[1], "");

    }

}
