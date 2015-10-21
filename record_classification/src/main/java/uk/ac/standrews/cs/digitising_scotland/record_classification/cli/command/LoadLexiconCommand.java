/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import com.beust.jcommander.*;
import org.apache.lucene.search.spell.*;
import org.apache.lucene.search.suggest.*;
import sun.launcher.resources.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.experiments.config.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.logging.*;

/**
 * Command to load dictionary of words or stop words.
 * The loaded lexicon can later be used via the clean command.
 *
 * @author Masih Hajiarab Derkani
 */
@Parameters(commandNames = LoadLexiconCommand.NAME, commandDescription = "Load lexicon")
public class LoadLexiconCommand extends Command {

    /** The name of this command. */
    public static final String NAME = "lexicon";

    /** The short name of the option that specifies the type of lexicon to be loaded. **/
    public static final String OPTION_TYPE_SHORT = "-t";

    /** The long name of the option that specifies the type of lexicon to be loaded. **/
    public static final String OPTION_TYPE_LONG = "--type";

    private static final Logger LOGGER = Logger.getLogger(LoadLexiconCommand.class.getName());

    private final LoadCommand load_command;

    @Parameter(required = true, names = {OPTION_TYPE_SHORT, OPTION_TYPE_LONG}, description = "The type of the lexicon to be loaded.")
    private LexiconType type;

    /**
     * Instantiates this command as a sub command of the given load command.
     *
     * @param load_command the load command to which this command belongs.
     */
    public LoadLexiconCommand(final LoadCommand load_command) {

        super(load_command.launcher);
        this.load_command = load_command;
    }

    @Override
    public void run() {

        final Path source = load_command.getSource();
        final Charset charset = load_command.getCharset();
        final String name = load_command.getName();

        switch (type) {
            case DICTIONARY:
                loadDictionary(source, charset, name);
                break;
            case STOP_WORDS:
                loadStopWords(source, charset, name);
                break;
            default:
                LOGGER.fine(() -> String.format("unimplemented lexicon of type %s", type));
                throw new UnsupportedOperationException("loading lexicon of type " + type + " is not implemented.");
        }
    }

    private void loadDictionary(final Path source, final Charset charset, final String name) {

        LOGGER.finest("loading dictionary...");

        final Configuration.Dictionary dictionary = new Configuration.Dictionary(name);
        try {
            dictionary.load(source, charset);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadStopWords(final Path source, final Charset charset, final String name) {

        LOGGER.finest("loading stop words...");

        final Configuration.StopWords stop_words = new Configuration.StopWords(name);
        try {
            stop_words.load(source, charset);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** The types of lexicons that can be loaded by this command. **/
    public enum LexiconType {
        /** Dictionary of words to be used by spell checker via the clean command. **/
        DICTIONARY,

        /** Stop words to be used by {@link CleanerSupplier#ENGLISH_STOP_WORDS stop words cleaner} via the clean command. **/
        STOP_WORDS
    }
}
