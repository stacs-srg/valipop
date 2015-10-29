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
