package dateModel;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MisalignedTimeDivisionError extends Error {
    public MisalignedTimeDivisionError(String s) {
        super(s);
    }

    public MisalignedTimeDivisionError() {};
}
