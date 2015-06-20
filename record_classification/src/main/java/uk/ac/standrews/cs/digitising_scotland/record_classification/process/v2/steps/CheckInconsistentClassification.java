package uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.steps;

import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.process.v2.*;

import java.util.*;

/**
 * Checks whether identical data has been classified under multiple classifications.
 *
 * @author Graham Kirby
 * @author Masih Hajiarab Derkani
 */
public class CheckInconsistentClassification implements Step {

    private static final long serialVersionUID = -1169844023653663515L;

    @Override
    public void perform(final Context context) throws Exception {

        final Bucket classified_records = context.getClassifiedUnseenRecords();

        if (classified_records != null) {

            final Map<String, String> classifications = new HashMap<>();

            for (Record record : classified_records) {

                final String data = record.getData();
                final Classification classification = record.getClassification();

                String code = classification.getCode();

                if (classifications.containsKey(data)) {
                    if (!code.equals(classifications.get(data))) {
                        throw new InconsistentCodingException("data: " + data + " classified as both " + code + " and " + classifications.get(data));
                    }
                }
                else {
                    classifications.put(data, code);
                }
            }
        }
        else {
            //TODO warn of skipped step
        }
    }
}
