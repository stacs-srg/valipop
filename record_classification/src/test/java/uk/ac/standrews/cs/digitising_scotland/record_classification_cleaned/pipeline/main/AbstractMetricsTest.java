package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.main;

import org.junit.Before;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.*;

import java.util.HashSet;
import java.util.Set;

public class AbstractMetricsTest {

    protected static final Record2 haddock_correct = new Record2(3, "haddock", new Classification2("fish", new TokenSet(), 1.0));
    protected static final Record2 haddock_incorrect = new Record2(3, "haddock", new Classification2("mammal", new TokenSet(), 1.0));
    protected static final Record2 osprey_incorrect = new Record2(3, "osprey", new Classification2("mammal", new TokenSet(), 1.0));
    private static final Record2 sparrow_correct = new Record2(3, "sparrow", new Classification2("bird", new TokenSet(), 1.0));
    private static final Record2 eagle_correct = new Record2(3, "eagle", new Classification2("bird", new TokenSet(), 1.0));
    private static final Record2 elephant_correct = new Record2(3, "elephant", new Classification2("mammal", new TokenSet(), 1.0));
    protected static final Record2 unicorn_unclassified = new Record2(3, "unicorn", null);
    private static final Record2 horse_incorrect = new Record2(3, "horse", new Classification2("fish", new TokenSet(), 1.0));

    protected static final Record2[] test_classified_records = new Record2[]{haddock_correct, osprey_incorrect, sparrow_correct, eagle_correct, elephant_correct, unicorn_unclassified, horse_incorrect};

    protected static final Record2 haddock_gold_standard = new Record2(3, "haddock", new Classification2("fish", new TokenSet(), 1.0));
    protected static final Record2 cow_gold_standard = new Record2(3, "cow", new Classification2("mammal", new TokenSet(), 1.0));
    private static final Record2 osprey_gold_standard = new Record2(3, "osprey", new Classification2("bird", new TokenSet(), 1.0));
    private static final Record2 sparrow_gold_standard = new Record2(3, "sparrow", new Classification2("bird", new TokenSet(), 1.0));
    private static final Record2 eagle_gold_standard = new Record2(3, "eagle", new Classification2("bird", new TokenSet(), 1.0));
    private static final Record2 elephant_gold_standard = new Record2(3, "elephant", new Classification2("mammal", new TokenSet(), 1.0));
    private static final Record2 horse_gold_standard = new Record2(3, "horse", new Classification2("mammal", new TokenSet(), 1.0));
    private static final Record2 unicorn_gold_standard = new Record2(3, "unicorn", new Classification2("mythical", new TokenSet(), 1.0));

    protected static final Record2[] test_gold_standard_records = new Record2[]{haddock_gold_standard, cow_gold_standard, osprey_gold_standard, sparrow_gold_standard, eagle_gold_standard, elephant_gold_standard, horse_gold_standard, unicorn_gold_standard};

    protected Bucket2 classified_records;
    protected Bucket2 gold_standard_records;
    protected StrictConfusionMatrix2 matrix;


    @Before
    public void setUp() throws Exception {

        classified_records = new Bucket2();
        gold_standard_records = new Bucket2();
    }

    protected void initMatrix() throws InvalidCodeException, InconsistentCodingException, UnknownDataException, UnclassifiedGoldStandardRecordException {

        matrix = new StrictConfusionMatrix2(classified_records, gold_standard_records);
    }

    protected void initFullRecords() {

        classified_records.add(test_classified_records);
        gold_standard_records.add(test_gold_standard_records);
    }

    protected int getNumberOfCodes() {

        Set<String> valid_codes = new HashSet<>();

        for (Record2 record : gold_standard_records) {

            valid_codes.add(record.getClassification().getCode());
        }
        return valid_codes.size();
    }
}
