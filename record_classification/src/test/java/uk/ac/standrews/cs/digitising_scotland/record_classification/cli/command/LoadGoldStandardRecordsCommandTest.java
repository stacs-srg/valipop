package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.nio.file.*;
import java.util.stream.*;

import static org.junit.Assert.assertEquals;

/**
 * @author masih
 */
public class LoadGoldStandardRecordsCommandTest extends CommandTest {

    private Path temp_records_path;
    private Bucket temp_records;

    @Override
    public void setUp() throws Exception {

        super.setUp();

        temp_records = new Bucket();
        temp_records.add(new Record(1, "fish", new Classification("swim", new TokenList("fish"), 0.0, null)));
        temp_records.add(new Record(2, "dog", new Classification("bark", new TokenList("dog"), 0.0, null)));
        temp_records.add(new Record(3, "cat", new Classification("purr", new TokenList("cat"), 0.0, null)));
        temp_records.add(new Record(4, "shark", new Classification("swim", new TokenList("shark"), 0.0, null)));

        temp_records_path = Files.createTempFile(null, null);
        Files.write(temp_records_path, temp_records.stream().map(record -> String.join(",", String.valueOf(record.getId()), record.getData(), record.getClassification().getCode())).collect(Collectors.toList()));
    }

    @Test
    public void testLoad() throws Exception {

        run(LoadCommand.NAME, LoadCommand.OPTION_SOURCE_SHORT, temp_records_path, LoadGoldStandardRecordsCommand.NAME);
        final Bucket config_records = launcher.getConfiguration().getGoldStandardRecords().get();
        assertEquals(temp_records, config_records);

    }

    @Override
    public void tearDown() throws Exception {

        super.tearDown();
        Files.deleteIfExists(temp_records_path);
    }
}
