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
package uk.ac.standrews.cs.digitising_scotland.record_classification_old.data_readers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.CODOriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification_old.tools.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * The Class FormatConverter converts a comma separated text file in the format that is used by the modern cod data
 * to a list of Record objects.
 * @author jkc25
 */
public final class LongFormatConverter extends AbstractFormatConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LongFormatConverter.class);

    private static final String CHARSET_NAME = "UTF8";

    /** The Constant CODLINELENGTH. */
    static final int CODLINELENGTH = 38;

    /** The Constant idPosition. */
    private static final int ID_POSITION = 0;

    /** The Constant agePosition. */
    private static final int AGE_POSITION = 34;

    /** The Constant sexPosition. */
    private static final int SEX_POSITION = 35;

    /** The Constant descriptionStart. */
    private static final int DESC_START = 1;

    /** The Constant descriptionEnd. */
    private static final int DESC_END = 4;

    /** The Constant yearPosition. */
    private static final int YEAR_POSITION = 37;

    /**
     * Converts the data in the inputFile (one record per line, comma separated) into {@link Record}s.
     *
     * @param inputFile the input file to be read
     * @return the list of records
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     * @throws CodeNotValidException 
     */
    public List<Record> convert(final File inputFile, final CodeDictionary codeDictionary) throws IOException, InputFormatException, CodeNotValidException {

        BufferedReader br = ReaderWriterFactory.createBufferedReader(inputFile);

        String line = "";
        List<Record> recordList = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split(Utils.getCSVComma());

            checkLineLength(lineSplit, CODLINELENGTH);

            int id = Integer.parseInt(lineSplit[ID_POSITION]);
            int imageQuality = 1;
            int ageGroup = convertAgeGroup(removeQuotes(lineSplit[AGE_POSITION]));
            int sex = convertSex(removeQuotes(lineSplit[SEX_POSITION]));
            String description = formDescription(lineSplit, DESC_START, DESC_END);
            int year = Integer.parseInt(removeQuotes(lineSplit[YEAR_POSITION]));

            CODOriginalData originalData = new CODOriginalData(description, year, ageGroup, sex, imageQuality, inputFile.getName());
            HashSet<Classification> goldStandard = new HashSet<>();
            populateGoldStandardSet(codeDictionary, lineSplit, goldStandard);

            Record r = new Record(id, originalData);
            r.getOriginalData().setGoldStandardClassification(goldStandard);

            if (goldStandard.size() == 0) {
                LOGGER.info("Gold Standard Set Empty: " + r.getDescription());
            }
            else {
                recordList.add(r);
            }
        }

        br.close();
        return recordList;
    }

    /**
     * Populate gold standard set.
     *
     * @param lineSplit the line split
     * @param goldStandard the gold standard
     * @throws CodeNotValidException 
     */
    private static void populateGoldStandardSet(final CodeDictionary codeDictionary, final String[] lineSplit, final HashSet<Classification> goldStandard) throws CodeNotValidException {

        final int start_pos = 6;
        final int end_pos = 31;
        final int jump_size = 3;

        for (int i = start_pos; i < end_pos; i = i + jump_size) {
            if (lineSplit[i].length() != 0) {
                int causeIdentifier = Integer.parseInt(lineSplit[i]);

                if (causeIdentifier != start_pos) {
                    Code code = codeDictionary.getCode(removeQuotes(lineSplit[i + 2]));
                    TokenSet tokenSet = new TokenSet(lineSplit[causeIdentifier]);
                    Classification codeTriple = new Classification(code, tokenSet, 1.0);
                    goldStandard.add(codeTriple);
                }
            }
        }
    }
}
