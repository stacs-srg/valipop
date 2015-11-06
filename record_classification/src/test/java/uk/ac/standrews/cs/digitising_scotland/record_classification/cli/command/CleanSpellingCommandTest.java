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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.command;

import org.junit.Test;
import org.junit.runner.*;
import org.junit.runners.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.cli.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static uk.ac.standrews.cs.digitising_scotland.record_classification.dataset.TestDataSets.*;

/**
 * @author Masih Hajirab Derkani
 */
@RunWith(Parameterized.class)
public class CleanSpellingCommandTest extends CleanStopWordsCommandTest {

    public static final TestResource DICTIONARY = new TestResource(TestResource.class, "spelling/dictionary.txt");
    public static final List<TestDataSet> GS_WITH_MISTAKES = Arrays.asList(new TestDataSet(TestDataSet.class, "spelling/gold_standard_with_spelling_mistakes.csv"));
    public static final List<TestDataSet> GS_WITHOUT_MISTAKES = Arrays.asList(new TestDataSet(TestDataSet.class, "spelling/gold_standard_without_spelling_mistakes.csv"));
    

    @Parameterized.Parameters(name = "{index} {5}")
    public static Collection<Object[]> data() {

        final List<Object[]> parameters = new ArrayList<>();
        for (CharsetSupplier charset_supplier : CharsetSupplier.values()) {
            parameters.add(new Object[]{GS_WITH_MISTAKES, GS_WITH_MISTAKES, GS_WITHOUT_MISTAKES, GS_WITHOUT_MISTAKES, DICTIONARY, charset_supplier, false});
        }
        return parameters;
    }

    public CleanSpellingCommandTest(List<TestDataSet> gold_standards, List<TestDataSet> unseens, List<TestDataSet> expected_gold_standards, List<TestDataSet> expected_unseens, TestResource dictionary, CharsetSupplier dictionary_charset, boolean case_sensitive) throws IOException {

        super(gold_standards, unseens, expected_gold_standards, expected_unseens, dictionary, dictionary_charset, case_sensitive);
    }

    @Override
    protected CleanStopWordsCommand.Builder getBuilder() {

        return new CleanSpellingCommand.Builder().accuracyThreshold(0.7f);
    }
}
