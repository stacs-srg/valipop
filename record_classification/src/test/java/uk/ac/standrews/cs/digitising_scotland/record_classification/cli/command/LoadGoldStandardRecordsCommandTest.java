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
public class LoadGoldStandardRecordsCommandTest extends CommandTest {

    private static final Bucket TEST_RECORDS = new Bucket();
    private static final String ID = "ID";
    private static final String LABEL = "LABEL";
    private static final String CODE = "CODE";
    public static final CSVFormat DEFAULT_GOLD_STANDARD_FORMAT = CSVFormat.DEFAULT.withHeader(ID, LABEL, CODE);
    private static final Map<Integer[], CSVFormat> TEST_FORMATS = new HashMap<>();
    private static final List<Path> TEMP_PATHS = new ArrayList<>();

    static {
        TEST_RECORDS.add(new Record(1, "fish", new Classification("swim", new TokenList("fish"), 0.0, null)));
        TEST_RECORDS.add(new Record(2, "dog", new Classification("bark", new TokenList("dog"), 0.0, null)));
        TEST_RECORDS.add(new Record(3, "cat", new Classification("purr", new TokenList("cat"), 0.0, null)));
        TEST_RECORDS.add(new Record(4, "shark", new Classification("swim", new TokenList("shark"), 0.0, null)));
    }

    static {
        TEST_FORMATS.put(new Integer[]{0, 1, 2}, DEFAULT_GOLD_STANDARD_FORMAT);
        TEST_FORMATS.put(new Integer[]{0, 1, 2}, CSVFormat.DEFAULT);
        TEST_FORMATS.put(new Integer[]{0, 1, 2}, CSVFormat.RFC4180);
        TEST_FORMATS.put(new Integer[]{0, 1, 2}, CSVFormat.EXCEL);
        TEST_FORMATS.put(new Integer[]{0, 1, 2}, CSVFormat.MYSQL);
        TEST_FORMATS.put(new Integer[]{0, 1, 2}, CSVFormat.TDF);
        TEST_FORMATS.put(new Integer[]{0, 1, 2}, CsvFormatSupplier.RFC4180_PIPE_SEPARATED.get());
        TEST_FORMATS.put(new Integer[]{0, 2, 1}, CSVFormat.DEFAULT.withHeader(ID, CODE, LABEL));
        TEST_FORMATS.put(new Integer[]{2, 1, 0}, CSVFormat.DEFAULT.withHeader(CODE, LABEL, ID));
    }

    private Path path;
    private CSVFormat format;
    private int id_index;
    private int label_index;
    private int code_index;

    public LoadGoldStandardRecordsCommandTest(Path path, CSVFormat format, int id_index, int label_index, int code_index) {

        this.path = path;
        this.format = format;
        this.id_index = id_index;
        this.label_index = label_index;
        this.code_index = code_index;
    }

    private static Path printRecordsIntoTempFileWithFormat(Bucket records, CSVFormat format, int id_index, int label_index, int code_index) {

        final Path temp_path = newTempFile();

        try (final BufferedWriter out = Files.newBufferedWriter(temp_path)) {

            final CSVPrinter printer = format.print(out);
            records.stream().forEach(record -> {
                try {

                    final String[] record_csv = new String[3];
                    record_csv[id_index] = String.valueOf(record.getId());
                    record_csv[label_index] = record.getData();
                    record_csv[code_index] = record.getClassification().getCode();
                    printer.printRecord(record_csv);
                }
                catch (IOException e) { throw new IOError(e); }
            });
        }
        catch (IOException e) { throw new IOError(e); }

        return temp_path;
    }

    private static Path newTempFile() {

        try {
            return Files.createTempFile(null, null);
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        for (Path path : TEMP_PATHS) {
            try {
                Files.deleteIfExists(path);
            }
            catch (IOException e) {
                //skip
            }
        }
    }

    @Parameterized.Parameters(name = "{index} - id: {2}, label: {3}, code: {4}")
    public static Collection<Object[]> data() {

        final List<Object[]> arguments = new ArrayList<>();

        TEST_FORMATS.entrySet().stream().forEach(index_format -> {
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

    @Test
    public void testLoad() throws Exception {

        run(LoadCommand.NAME, LoadCommand.OPTION_SOURCE_SHORT, path, LoadGoldStandardRecordsCommand.NAME, LoadRecordsCommand.OPTION_DELIMITER_SHORT, quote(format.getDelimiter()), LoadRecordsCommand.OPTION_ID_COLUMN_INDEX_SHORT, id_index, LoadRecordsCommand.OPTION_LABEL_COLUMN_INDEX_SHORT,
            label_index, LoadGoldStandardRecordsCommand.OPTION_CLASS_COLUMN_INDEX_SHORT, code_index, format.getHeader() == null ? "" : LoadRecordsCommand.OPTION_SKIP_HEADER_SHORT);

        final Bucket config_records = launcher.getConfiguration().getGoldStandardRecords().get();
        assertEquals(TEST_RECORDS, config_records);
    }

}
