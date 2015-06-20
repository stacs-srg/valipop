package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Presents a classification process that consists of a list of steps and produces a bucket of classified records.
 *
 * @author Masih Hajiarab Derkani
 */
public class ClassificationProcess implements Callable<Bucket>, Serializable {

    private static final long serialVersionUID = -3086230162106640193L;

    private final Context context;
    private final List<Step> steps;

    /**
     * Instantiates a new classification process with an empty context.
     */
    public ClassificationProcess() {

        this(new Context());
    }

    /**
     * Instantiates a new classification process.
     *
     * @param context the context in which to classify
     */
    public ClassificationProcess(final Context context) {

        this.context = context;
        steps = new ArrayList<>();
    }

    /**
     * Adds a step to the steps to be performed by this process.
     *
     * @param step the step to be performed in the classification process.
     * @return this classification process to accommodate chaining of step additions.
     */
    public ClassificationProcess addStep(Step step) {

        steps.add(step);
        return this;
    }

    /**
     * Sequentially performs the steps in this classification process.
     *
     * @return the classified records, or {@code null} if no records were classified
     * @throws Exception if an error while performing the process steps
     */
    @Override
    public Bucket call() throws Exception {

        for (Step step : steps) {
            step.perform(context);
        }

        return context.getClassifiedRecords();
    }

    /**
     * Gets the context of this classification process.
     *
     * @return the context of this classification process
     */
    public Context getContext() {

        return context;
    }
}
