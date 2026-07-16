/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.utils.sourceEventRecords;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecord;

import static uk.ac.standrews.cs.valipop.population.OBDModel.EXPORT_CHARSET;

abstract public class RecordType {

    // TODO rename

    public static final String BIRTH_RECORDS_FILENAME = "birth-records.csv";
    public static final String DEATH_RECORDS_FILENAME = "death-records.csv";
    public static final String MARRIAGE_RECORDS_FILENAME = "marriage-records.csv";

    // TODO allow output file paths to be configured, add -i option to output to console

    private final Iterable<IPerson> people;
    private final Iterable<IPartnership> partnerships;

    RecordType(final Iterable<IPerson> people, final Iterable<IPartnership> partnerships) {

        this.people = people;
        this.partnerships = partnerships;
    }

    abstract protected Iterable<? extends SourceRecord> toBirthRecords(Iterable<IPerson> people);

    abstract protected Iterable<? extends SourceRecord> toDeathRecords(Iterable<IPerson> people);

    abstract protected Iterable<? extends SourceRecord> toMarriageRecords(Iterable<IPartnership> partnerships);

    public void exportRecords(final Path recordDir) throws IOException {

        exportRecords(toBirthRecords(people), recordDir.resolve(BIRTH_RECORDS_FILENAME));
        exportRecords(toDeathRecords(people), recordDir.resolve(DEATH_RECORDS_FILENAME));
        exportRecords(toMarriageRecords(partnerships), recordDir.resolve(MARRIAGE_RECORDS_FILENAME));
    }

    private static void exportRecords(final Iterable<? extends SourceRecord> records, final Path recordsPath) throws IOException {

        Config.createParentDirectoryIfDoesNotExist(recordsPath);

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(recordsPath, EXPORT_CHARSET))) {

            boolean first = true;

            for (final SourceRecord record : records) {

                if (first) {
                    writer.println(record.getHeaders());
                    first = false;
                }

                writer.println(record);
            }
        }
    }
}
