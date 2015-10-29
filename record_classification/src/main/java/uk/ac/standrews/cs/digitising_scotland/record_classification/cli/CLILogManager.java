package uk.ac.standrews.cs.digitising_scotland.record_classification.cli;

import sun.reflect.*;

import java.util.logging.*;

/**
 * @author masih
 */
public class CLILogManager extends LogManager {

    public static final CLILogManager CLI_LOG_MANAGER = new CLILogManager();

    private CLILogManager() {

    }

    public static CLILogManager getLogManager() {
        return CLI_LOG_MANAGER;
    }
    
    
    
    public static class CLILogger extends Logger{

        protected CLILogger(final String name, final String resource_bundle_name) {

            super(name, resource_bundle_name);
        }

        public static Logger getLogger(String name) {
            Logger result = CLI_LOG_MANAGER.getLogger(name);
            if (result == null) {
                Logger newLogger = new CLILogger(name, null);
                do {
                    if (CLI_LOG_MANAGER.addLogger(newLogger)) {
                        return newLogger;
                    }

                    result = CLI_LOG_MANAGER.getLogger(name);
                } while (result == null);
            }
            return result;
        }
    }
}
