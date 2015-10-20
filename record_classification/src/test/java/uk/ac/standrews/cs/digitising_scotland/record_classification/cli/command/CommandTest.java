package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.apache.commons.io.*;
import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.io.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class CommandTest {

    @Before
    public void setUp() throws Exception {

        deleteCliHome();
    }

    @After
    public void tearDown() throws Exception {

        deleteCliHome();
    }

    protected void deleteCliHome() throws IOException {FileUtils.deleteDirectory(Configuration.CLI_HOME.toFile());}
}
