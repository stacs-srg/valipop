package uk.ac.standrews.cs.valipop.implementations.minimaSearch;

import java.security.InvalidParameterException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public enum Minimise {

    ALL,
    OB,
    GEEGLM;

    public static Minimise resolve(String s) {

        s = s.toLowerCase();

        switch (s) {

            case "all":
                return ALL;
            case "ob":
                return OB;
            case "geeglm":
                return GEEGLM;

        }

        throw new InvalidParameterException("Given Minimise term is not recognised. Supported options are: "
                + java.util.Arrays.asList(Minimise.values()).toString());

    }

}
