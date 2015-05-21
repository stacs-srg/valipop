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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned.pipeline.main;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Before;
import org.slf4j.LoggerFactory;

public class AbstractTest {

    public static final String PACKAGE_ROOT = "uk.ac.standrews.cs.digitising_scotland";

    @Before
    public void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(PACKAGE_ROOT);
        logger.setLevel(Level.INFO);
    }

    protected static String getResourceFilePath(Class the_class, String resource_file_name) {

        return the_class.getResource(resource_file_name).getFile();
    }
}
