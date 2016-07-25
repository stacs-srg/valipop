/*
 * Copyright 2016 Digitising Scotland project:
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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.supplier;

import java.util.function.*;
import java.util.logging.*;

/**
 * Supplies {@link Level log level}s.
 *
 * @author Masih Hajiarab Derkani
 */
public enum LogLevelSupplier implements Supplier<Level> {

    /** Indicates all messages must be logged. **/
    ALL(Level.ALL),

    /** Indicates that serious failures must be logged. **/
    SEVERE(Level.SEVERE),

    /** Indicates that messages about potential problems must be logged. **/
    WARNING(Level.WARNING),

    /** Indicates only informational messsages must be logged. **/
    INFO(Level.INFO),

    /** Turns off logging. **/
    OFF(Level.OFF);

    private Level level;

    LogLevelSupplier(final Level level) {

        this.level = level;
    }

    @Override
    public Level get() { return level; }
}
