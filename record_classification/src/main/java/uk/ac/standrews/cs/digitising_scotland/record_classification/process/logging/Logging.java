package uk.ac.standrews.cs.digitising_scotland.record_classification.process.logging;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.InfoLevel;
import uk.ac.standrews.cs.digitising_scotland.util.PercentageProgressIndicator;
import uk.ac.standrews.cs.digitising_scotland.util.ProgressIndicator;

public class Logging {

    private static final int NUMBER_OF_PROGRESS_UPDATES = 20;
    private static InfoLevel info_level = InfoLevel.NONE;
    private static ProgressIndicator progress_indicator;

    public static InfoLevel getInfoLevel() {

        return info_level;
    }

    public static void setInfoLevel(InfoLevel info_level) {

        Logging.info_level = info_level;
    }

    public static void output(String message, InfoLevel threshold) {

        if (info_level.compareTo(threshold) >= 0) {
            System.out.println(message);
        }
    }

    public static void setProgressIndicatorSteps(int number_of_steps) {

        progress_indicator = new PercentageProgressIndicator(NUMBER_OF_PROGRESS_UPDATES);
        progress_indicator.setTotalSteps(number_of_steps);
    }

    public static void progressStep() {

        if (info_level != InfoLevel.NONE) {
            progress_indicator.progressStep();
        }
    }
}
