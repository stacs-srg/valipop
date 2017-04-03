package datastructure.summativeStatistics.structure.FailureAgainstTimeTable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class FailureTimeDoubleArrays {

    double[] timeObserved;
    double[] eventObserved;
    double[] timeExpected;
    double[] eventExpected;

    public FailureTimeDoubleArrays(double[] timeObserved, double[] eventObserved, double[] timeExpected, double[] eventExpected) {
        this.timeObserved = timeObserved;
        this.eventObserved = eventObserved;
        this.timeExpected = timeExpected;
        this.eventExpected = eventExpected;
    }

    public double[] getEventExpected() {
        return eventExpected;
    }

    public double[] getEventObserved() {
        return eventObserved;
    }

    public double[] getTimeExpected() {
        return timeExpected;
    }

    public double[] getTimeObserved() {
        return timeObserved;
    }

}
