package uk.ac.standrews.cs.digitising_scotland.verisim.implementations.minimaSearch;

import java.security.InvalidParameterException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public enum Minimise {

    ALL,
    OB;

    public static Minimise resolve(String s) {

        s = s.toLowerCase();

        switch (s) {

            case "all":
                return ALL;
            case "ob":
                return OB;

        }

        throw new InvalidParameterException("Given Minimise term is not recognised. Supported options are: "
                + java.util.Arrays.asList(Minimise.values()).toString());

    }

}
