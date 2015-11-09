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

import org.apache.commons.csv.*;
import org.apache.commons.io.*;
import org.junit.*;
import org.junit.rules.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

import static java.util.logging.Logger.getLogger;

/**
 * Captures common functionality among {@link Command command} test classes.
 *
 * @author Masih Hajiarab Derkani
 */
public abstract class CommandTest {

    protected Launcher launcher;
    private static final Logger LOGGER = getLogger(CommandTest.class.getName());

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {

        deleteCliHome();
        launcher = new Launcher();
    }

    protected void run(Object... args) throws Exception {

        final List<String> arguments = Arrays.asList(args).stream().map(String::valueOf).collect(Collectors.toList());
        arguments.add(0, Launcher.OPTION_VERBOSITY_SHORT);
        arguments.add(1, LogLevelSupplier.OFF.name());
        LOGGER.info(() -> String.format("running: %s", String.join(" ", arguments)));
        launcher.parse(arguments.toArray(new String[arguments.size()]));
        launcher.handle();
    }

    protected String quote(Object value) {

        return String.format("\"%s\"", String.valueOf(value));
    }

    protected void init() throws Exception {new InitCommand.Builder().run(launcher);}

    protected void initForcefully() throws Exception {new InitCommand.Builder().forcefully().run(launcher);}

    protected void setVerbosity(final LogLevelSupplier supplier) throws Exception { new SetCommand.Builder().verbosity(supplier).run(launcher); }

    protected void setClassifier(ClassifierSupplier classifier_supplier) throws Exception { new SetCommand.Builder().classifier(classifier_supplier).run(launcher); }

    protected void loadGoldStandards(List<TestDataSet> gold_standards, final CharsetSupplier charset, final CSVFormat format) throws Exception {

        for (TestDataSet gold_standard : gold_standards) {
            loadGoldStandard(gold_standard, charset, format);
        }
    }

    protected void loadUnseens(List<TestDataSet> unseens, final CharsetSupplier charset, final CSVFormat format) throws Exception {

        for (TestDataSet unseen : unseens) {
            loadUnseen(unseen, charset, format);
        }
    }

    protected void evaluate() throws Exception { new EvaluateCommand.Builder().run(launcher); }

    protected void train() throws Exception {new TrainCommand.Builder().internalTrainingRatio(1.0).run(launcher);}

    protected void clean(CleanerSupplier cleaner) throws Exception {new CleanCommand.Builder().cleaners(cleaner).run(launcher);}

    protected Path classify() throws Exception {

        final Path output_path = temp.newFile().toPath();
        new ClassifyCommand.Builder().output(output_path).run(launcher);
        return output_path;
    }

    protected void loadGoldStandards(final List<TestDataSet> records) throws Exception {

        loadGoldStandards(records, TestDataSet.DEFAULT_CHARSET, TestDataSet.DEFAULT_CSV_FORMAT);
    }

    protected void loadGoldStandard(final TestDataSet records, final CharsetSupplier charset, final CSVFormat format) throws Exception {

        final LoadRecordsCommand.Builder builder = new LoadGoldStandardRecordsCommand.Builder().trainingRatio(1.0).classColumnIndex(records.class_column_index);
        load(builder, records, charset, format);
    }

    protected void loadUnseens(final List<TestDataSet> records) throws Exception {

        loadUnseens(records, TestDataSet.DEFAULT_CHARSET, TestDataSet.DEFAULT_CSV_FORMAT);
    }

    protected void loadUnseen(final TestDataSet records, final CharsetSupplier charset, final CSVFormat format) throws Exception {

        load(new LoadUnseenRecordsCommand.Builder(), records, charset, format);
    }

    protected void load(LoadRecordsCommand.Builder builder, TestDataSet records, final CharsetSupplier charset, final CSVFormat format) throws Exception {

        final Path source = getTestCopy(records, charset, format);
        builder.idColumnIndex(records.id_column_index).labelColumnIndex(records.label_column_index).delimiter(format.getDelimiter()).skipHeader(format.getSkipHeaderRecord()).from(source).charset(charset).run(launcher);
    }

    protected Path getTestCopy(final TestDataSet records, final CharsetSupplier charset, final CSVFormat format) {

        try {
            final Path destination = temp.newFile().toPath();
            records.copy(destination, charset.get(), format);
            return destination;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void tearDown() throws Exception {

        deleteCliHome();
    }

    protected void deleteCliHome() throws IOException {FileUtils.deleteDirectory(Configuration.CLI_HOME.toFile());}
}
