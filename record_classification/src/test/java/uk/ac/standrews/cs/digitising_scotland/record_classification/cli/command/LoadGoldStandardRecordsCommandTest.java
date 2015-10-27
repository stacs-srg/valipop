package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.apache.commons.csv.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Masih Hajiarab Derkani
 */
@RunWith(Parameterized.class)
public class LoadGoldStandardRecordsCommandTest extends LoadRecordsCommandTest {

    private int code_index;

    public LoadGoldStandardRecordsCommandTest(Path path, CSVFormat format, int id_index, int label_index, int code_index) {

        super(path, format, id_index, label_index);
        this.code_index = code_index;
    }

    @Parameterized.Parameters(name = "{index} - id: {2}, label: {3}, code: {4}")
    public static Collection<Object[]> data() {

        final List<Object[]> arguments = new ArrayList<>();

        TEST_FORMATS.entrySet().forEach(index_format -> {
            final Integer[] indices = index_format.getKey();
            final CSVFormat format = index_format.getValue();
            final Integer id_index = indices[0];
            final Integer label_index = indices[1];
            final Integer code_index = indices[2];
            final Path path = printRecordsIntoTempFileWithFormat(TEST_RECORDS, format, id_index, label_index, code_index);
            arguments.add(new Object[]{path, format, id_index, label_index, code_index});
            TEMP_PATHS.add(path);
        });

        return arguments;
    }

    @Override
    protected List<Object> getArguments() {

        final List<Object> arguments = super.getArguments();
        arguments.add(LoadGoldStandardRecordsCommand.OPTION_CLASS_COLUMN_INDEX_SHORT);
        arguments.add(code_index);
        return arguments;
    }

    @Override
    protected String getSubCommandName() {

        return LoadGoldStandardRecordsCommand.NAME;
    }

    @Override
    protected Bucket getActualRecords() {

        return launcher.getConfiguration().getGoldStandardRecords().get();
    }
}
