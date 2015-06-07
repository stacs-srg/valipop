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
package uk.ac.standrews.cs.digitising_scotland.record_classification.util;

import uk.ac.standrews.cs.util.csv.DataSet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnonymiseText {

    private static boolean RANDOMISE = false;
    private static boolean RESET_IDS = true;

    private static Map<String, String> anonymised_strings = new HashMap<>();

    public static void main(String[] args) throws IOException {

        DataSet source_data = new DataSet(new InputStreamReader(Files.newInputStream(Paths.get(args[0]))));

        DataSet anonymised_data = new DataSet(Arrays.asList("id", "data", "code"));

        for (List<String> record : source_data.getRecords()) {

            String id = record.get(0);
            String data = record.get(1);
            String code = record.get(2);

            if (RESET_IDS) id = String.valueOf(id_counter++);

            anonymised_data.addRow(Arrays.asList(id, anonymise(data), anonymise(code)));
        }

        OutputStreamWriter out = new OutputStreamWriter(Files.newOutputStream(Paths.get(args[1])));
        anonymised_data.print(out);
        out.flush();
        out.close();
    }

    private static String anonymise(String s) {

        String anonymised = anonymised_strings.get(s);

        if (anonymised == null) {
            anonymised = makeAnonymousString();
            anonymised_strings.put(s, anonymised);
        }

        return anonymised;
    }

    private static SecureRandom random = new SecureRandom();
    private static int string_counter = 1;
    private static int id_counter = 1;

    private static String makeAnonymousString() {

        if (RANDOMISE) {

            return new BigInteger(130, random).toString(32).replaceAll("\\d", " ");
        }
        else {
            return "string_" + string_counter++;
        }
    }
}
