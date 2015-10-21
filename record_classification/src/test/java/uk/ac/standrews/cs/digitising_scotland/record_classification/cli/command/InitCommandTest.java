package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.junit.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.nio.file.*;

import static org.junit.Assert.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class InitCommandTest extends CommandTest {

    @Test
    public void testBasicInitialisation() throws Exception {

        run(InitCommand.NAME);

        assertTrue(Files.isDirectory(Configuration.CLI_HOME));
        assertTrue(Files.isRegularFile(Configuration.CONFIGURATION_FILE));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBasicInitialisationFailureIfConfigurationExists() throws Exception {

        run(InitCommand.NAME);
        run(InitCommand.NAME);
    }
    
    @Test
    public void testForcedInitialisationIfConfigurationExists() throws Exception {

        run(InitCommand.NAME);
        run(InitCommand.NAME, InitCommand.OPTION_FORCE_LONG);
    }
}
