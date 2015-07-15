package uk.ac.standrews.cs.digitising_scotland.record_classification.model;


import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.DuplicateRecordIdException;

public class BucketTest {

    private static final Record[] RECORDS_WITH_DUPLICATE_IDS = new Record[]{
            new Record(1, "abc", new Classification("class1", new TokenSet("abc"), 1.0)),
            new Record(2, "def", new Classification("class2", new TokenSet("def"), 1.0)),
            new Record(2, "ghi", new Classification("class3", new TokenSet("ghi"), 1.0)),
            new Record(3, "bcd", new Classification("class4", new TokenSet("bcd"), 1.0)),
            new Record(4, "efg", new Classification("class5", new TokenSet("efg"), 1.0))
    };

    @Test(expected= DuplicateRecordIdException.class)
    public void duplicateRecordIdsDetected() {

        new Bucket(RECORDS_WITH_DUPLICATE_IDS);
    }
}
