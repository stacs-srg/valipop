package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.nio.file.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class InitCommandTest extends CommandTest {

    @Test
    public void testRun() throws Exception {

        Launcher.main(new String[]{InitCommand.NAME});

        assertTrue(Files.isDirectory(Configuration.CLI_HOME));
        assertTrue(Files.isRegularFile(Configuration.CONFIGURATION_FILE));
    }
}
