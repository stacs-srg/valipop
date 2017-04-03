package utils;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ProgramTimer {

    private long startTime;
    private long endTime;
    private boolean stopped = false;

    public ProgramTimer() {
        startTime = System.nanoTime();
    }

    public void stopTime() {
        endTime = System.nanoTime();
    }

    public String getTimeMMSS() {

        long runEndTime;

        if (stopped) {
            runEndTime = endTime;
        } else {
            runEndTime = System.nanoTime();
        }

        double runTime = (runEndTime - startTime) / Math.pow(10, 9);
        int minutes = (int) (runTime / 60);
        int seconds = (int) (runTime % 60);
        String rT = minutes + ":" + seconds;
        return rT;

    }


}
