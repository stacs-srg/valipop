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
package uk.ac.standrews.cs.digitising_scotland.record_classification.analysis;

import org.junit.Before;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class AbstractTest {

    @Before
    public void setup() throws Exception {

//        Logger logger = (Logger) LoggerFactory.getLogger(getClass().getPackage().getName());
//        logger.setLevel(Level.INFO);
    }

    protected static String getResourceFilePath(Class the_class, String resource_name) {

        URL resource = the_class.getResource(resource_name);
        return resource.getFile();
    }

    protected static String getResourceFilePath2(Class the_class, String resource_name) {

        String resource_name_prefixed_with_class = the_class.getSimpleName() + "/" + resource_name;
        URL resource = the_class.getResource(resource_name_prefixed_with_class);
        return resource.getFile();
    }

    protected InputStreamReader getInputStreamReaderForResource(Class the_class, String resource_name) {

        // This is done to make sure that the name of the resource directory containing the data files is kept in sync with the class name.

        String resource_name_prefixed_with_class = the_class.getSimpleName() + "/" + resource_name;
        InputStream resourceAsStream = the_class.getResourceAsStream(resource_name_prefixed_with_class);
        return new InputStreamReader(resourceAsStream);
    }
}
