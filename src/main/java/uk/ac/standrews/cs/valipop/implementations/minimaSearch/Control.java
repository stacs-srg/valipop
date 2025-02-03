package uk.ac.standrews.cs.valipop.implementations.minimaSearch;

import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * Specifies the controlling factor of the minima search.
 * 
 * <br>
 * 
 * Either RF (recovery factor).
 * 
 * <br>
 * 
 * Or PRF (proportional recovery factor).
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public enum Control {

    RF,
    PRF;

    public static Control resolve(String s) {

        s = s.toLowerCase();

        switch (s) {

            case "rf":
                return RF;
            case "prf":
                return PRF;

        }

        throw new InvalidParameterException("Given Control option is not recognised. Supported options are: " + Arrays.asList(Control.values()));
    }
}
