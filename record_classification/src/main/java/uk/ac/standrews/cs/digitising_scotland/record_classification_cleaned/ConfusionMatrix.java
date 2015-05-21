package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned;

import java.util.Map;

public interface ConfusionMatrix {

    Map<String, Integer> getClassificationCounts();

    Map<String, Integer> getTruePositiveCounts();

    Map<String, Integer> getFalsePositiveCounts();

    Map<String, Integer> getTrueNegativeCounts();

    Map<String, Integer> getFalseNegativeCounts();

    int getNumberOfClassifications();
}
