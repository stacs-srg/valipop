package datastructure.summativeStatistics.structure.FailureAgainstTimeTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FailureTimeRow {

    private int timeElapsed;
    private boolean eventOccured;
    private int groupIdentifier;

    public FailureTimeRow(int timeElapsed, boolean eventOccured, int groupIdentifier) {
        this.timeElapsed = timeElapsed;
        this.eventOccured = eventOccured;
        this.groupIdentifier = groupIdentifier;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public String toString() {
        if(eventOccured) {
            return Integer.toString(timeElapsed) + " 1 " + Integer.toString(groupIdentifier);
        } else {
            return Integer.toString(timeElapsed) + " 0 " + Integer.toString(groupIdentifier);
        }
    }

}
