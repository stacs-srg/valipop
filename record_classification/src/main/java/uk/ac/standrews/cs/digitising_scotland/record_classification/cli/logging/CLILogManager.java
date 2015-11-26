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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.logging;

import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

/**
 * @author masih
 */
public class CLILogManager extends LogManager {

    private static final CLILogManager CLI_LOG_MANAGER = new CLILogManager();
    private static final Handler CONSOLE_LOG_HANDLER = new CLIConsoleHandler();
    private static final String CLI_PARENT_LOGGER_NAME = Launcher.class.getPackage().getName();
    private static final Map<String, FileHandler> INTERNAL_LOG_HANDLERS = new HashMap<>();

    static {
        final Logger parent_logger = getCLIParentLogger();
        parent_logger.setUseParentHandlers(false);
        parent_logger.addHandler(CONSOLE_LOG_HANDLER);
    }

    public static Logger getCLIParentLogger() {

        return CLILogger.getLogger(CLI_PARENT_LOGGER_NAME);
    }

    public synchronized static FileHandler getInternalLogHandler(Configuration configuration) throws IOException {

        final String pattern = getLogFilePattern(configuration);
        return isAlreadyInitialised(pattern) ? getInternalLogHandlerByPattern(pattern) : initInternalLogHandler(pattern);
    }

    protected static FileHandler getInternalLogHandlerByPattern(final String pattern) {

        return INTERNAL_LOG_HANDLERS.get(pattern);
    }

    protected static FileHandler initInternalLogHandler(final String pattern) throws IOException {

        final Logger logger = CLILogManager.getCLIParentLogger();
        final FileHandler handler;
        handler = new FileHandler(pattern, true);
        handler.setEncoding(Configuration.RESOURCE_CHARSET.name());
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.SEVERE);
        logger.addHandler(handler);
        INTERNAL_LOG_HANDLERS.put(pattern, handler);
        return handler;
    }

    protected static boolean isAlreadyInitialised(final String log_file_pattern) {

        return INTERNAL_LOG_HANDLERS.containsKey(log_file_pattern);
    }

    protected static String getLogFilePattern(final Configuration configuration) {

        final Path logs_home = configuration.getInternalLogsHome();
        return logs_home.resolve(Configuration.PROGRAM_NAME + "_%g.log").toString();
    }

    private CLILogManager() {

    }

    public static void setConsoleLogLevel(final Level log_level) {

        CONSOLE_LOG_HANDLER.setLevel(log_level);
    }

    public static class CLILogger extends Logger {

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
                }
                while (result == null);
            }
            return result;
        }
    }
}
