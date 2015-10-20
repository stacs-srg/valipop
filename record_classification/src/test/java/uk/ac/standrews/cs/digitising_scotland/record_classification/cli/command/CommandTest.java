package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.apache.commons.io.*;
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class CommandTest {

    protected Launcher launcher;

    @Before
    public void setUp() throws Exception {

        deleteCliHome();
        launcher = new Launcher();
    }

    protected void run(Object... args) throws Exception {

        final List<String> arguments = Arrays.asList(args).stream().map(String::valueOf).collect(Collectors.toList());
        launcher.parse(arguments.toArray(new String[arguments.size()]));
        launcher.handle();
    }

    @After
    public void tearDown() throws Exception {

        deleteCliHome();
    }

    protected void deleteCliHome() throws IOException {FileUtils.deleteDirectory(Configuration.CLI_HOME.toFile());}
}
