package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.apache.commons.csv.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Masih Hajiarab Derkani
 */
@RunWith(Parameterized.class)
public class LoadUnseenRecordsCommandTest extends LoadRecordsCommandTest {

    public LoadUnseenRecordsCommandTest(Path path, CSVFormat format, int id_index, int label_index) {

        super(path, format, id_index, label_index);
    }

    @Override
    protected Bucket getActualRecords() {

        return launcher.getConfiguration().getUnseenRecords().get();
    }

    @Override
    protected String getSubCommandName() {

        return LoadUnseenRecordsCommand.NAME;
    }
}
