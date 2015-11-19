package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author masih
 */
public class ExperimentCommandTest  extends CommandTest{

    @Test(expected = RuntimeException.class)
    public void testFailureIfCommandsNotSpecified() throws Exception {
        
        new ExperimentCommand.Builder().run();
    }
}
