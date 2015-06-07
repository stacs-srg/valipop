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
package uk.ac.standrews.cs.digitising_scotland.record_classification_old.tools.fileutils;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.util.ExportUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Allows a MS Access database to be dumped to TSV files.
 */
public class AccessDatabaseDumper {

    private static final String TAB = "\t";

    public static final String TAB_SEPARATED_SUFFIX = "tsv";

    private Database database;

    /**
     * Instantiates a new access database dumper.
     *
     * @param databaseLocation the database location
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public AccessDatabaseDumper(final String databaseLocation) throws IOException {

        File dbFile = new File(databaseLocation);
        database = DatabaseBuilder.open(dbFile);
    }

    /**
     * Instantiates a new access database dumper.
     *
     * @param database the database
     */
    public AccessDatabaseDumper(final Database database) {

        this.database = database;
    }

    /**
     * Writes each table in the database to a tab-separated file with the same name as the table.
     * @param export_directory directory in which to write database table files
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void writeTablesToFile(final Path export_directory) throws IOException {

        new ExportUtil.Builder(database).setDelimiter(TAB).setFileNameExtension(TAB_SEPARATED_SUFFIX).setHeader(true).exportAll(export_directory.toFile());
    }

    /**
     * The main method.
     *
     * @param args the path to the database, followed by the path to the output directory.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(final String[] args) throws IOException {

        AccessDatabaseDumper reader = new AccessDatabaseDumper(args[0]);
        reader.writeTablesToFile(Paths.get(args[1]));
    }
}
