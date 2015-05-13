package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline;

public class InvalidArgException extends Exception {
    public InvalidArgException(String message) {
        super(message);
    }
}
