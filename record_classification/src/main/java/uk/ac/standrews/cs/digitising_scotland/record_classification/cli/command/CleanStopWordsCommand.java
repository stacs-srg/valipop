package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import com.beust.jcommander.*;
import com.beust.jcommander.converters.*;
import org.apache.lucene.analysis.util.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Cleans a user-specified set of stop words from the loaded gold standard and unseen records.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = CleanStopWordsCommand.NAME, resourceBundle = Configuration.RESOURCE_BUNDLE_NAME, commandDescriptionKey = "command.clean.stop_words.description")
public class CleanStopWordsCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "stop_words";

    /** The short name of the option that specifies whether the stop words are case sensitive. **/
    public static final String OPTION_CASE_SENSITIVE_SHORT = "-c";

    /** The long name of the option that specifies whether the stop words are case sensitive. **/
    public static final String OPTION_CASE_SENSITIVE_LONG = "--caseSensitive";

    @Parameter(required = true, names = {LoadCommand.OPTION_SOURCE_SHORT, LoadCommand.OPTION_SOURCE_LONG}, descriptionKey = "command.clean.stop_words.source.description", converter = PathConverter.class)
    private Path source;

    @Parameter(names = {LoadCommand.OPTION_CHARSET_SHORT, LoadCommand.OPTION_CHARSET_LONG}, descriptionKey = "command.clean.stop_words.source.description")
    private CharsetSupplier charset_supplier = launcher.getConfiguration().getDefaultCharsetSupplier();

    @Parameter(names = {OPTION_CASE_SENSITIVE_SHORT, OPTION_CASE_SENSITIVE_LONG}, descriptionKey = "command.clean.stop_words.case_sensitive.description")
    private boolean case_sensitive = false;

    /**
     * Instantiates this command for the given launcher.
     *
     * @param launcher the launcher to which this command belongs.
     */
    public CleanStopWordsCommand(final Launcher launcher) {

        super(launcher);
    }

    @Override
    public void run() {

        final Cleaner cleaner = getCleaner();
        final Configuration configuration = launcher.getConfiguration();

        CleanCommand.cleanUnseenRecords(cleaner, configuration);
        CleanCommand.cleanGoldStandardRecords(cleaner, configuration);
    }

    protected Cleaner getCleaner() {

        final List<String> words = readWords();
        final CharArraySet stop_words = new CharArraySet(words, case_sensitive);
        return new EnglishStopWordCleaner(stop_words);
    }

    private List<String> readWords() {

        try {
            return Files.lines(getSource(), getCharset()).collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    protected Path getSource() {

        return source;
    }

    protected Charset getCharset() {

        return charset_supplier.get();
    }
}
