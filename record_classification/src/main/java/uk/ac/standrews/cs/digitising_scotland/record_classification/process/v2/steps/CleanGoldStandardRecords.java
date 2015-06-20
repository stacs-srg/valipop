package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

/**
 * Cleans the gold standard records in a classification process context.
 *
 * @author Masih Hajiarab Derkani
 */
public class CleanGoldStandardRecords implements Step {

    private static final long serialVersionUID = -4959580121867002858L;
    private final Cleaner cleaner;

    /**
     * Instantiates a new step that cleans the gold standard records in the context of a classification process.
     *
     * @param cleaner the cleaner by which to perform the cleaning
     */
    public CleanGoldStandardRecords(Cleaner cleaner) {

        this.cleaner = cleaner;
    }

    @Override
    public void perform(final Context context) throws Exception {

        final Bucket gold_standard = context.getGoldStandard();
        if (gold_standard != null) {
            final Bucket cleaned_gold_standard = cleaner.clean(gold_standard);
            context.setGoldStandard(cleaned_gold_standard);
        }
    }
}
