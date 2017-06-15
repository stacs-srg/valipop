package uk.ac.standrews.cs.digitising_scotland.verisim.dateModel;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MisalignedTimeDivisionError extends Error {
    public MisalignedTimeDivisionError(String s) {
        super(s);
    }

    public MisalignedTimeDivisionError() {};
}
