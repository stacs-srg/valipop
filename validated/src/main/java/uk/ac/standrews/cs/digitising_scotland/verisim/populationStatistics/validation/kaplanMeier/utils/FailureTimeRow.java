package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.kaplanMeier.utils;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FailureTimeRow {

    private int timeElapsed;
    private boolean eventOccured;
    private String groupIdentifier;

    public FailureTimeRow(int timeElapsed, boolean eventOccured, String groupIdentifier) {
        this.timeElapsed = timeElapsed;
        this.eventOccured = eventOccured;
        this.groupIdentifier = groupIdentifier;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public boolean hasEventOccured() {
        return eventOccured;
    }

    public String rowAsString() {
        if(eventOccured) {
            return Integer.toString(timeElapsed) + " 1 " + groupIdentifier;
        } else {
            return Integer.toString(timeElapsed) + " 0 " + groupIdentifier;
        }
    }

}
