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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.*;
import org.apache.lucene.util.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.model.*;

import java.util.*;

/**
 * Removes english stop words from records.
 *
 * @author Masih Hajiarab Derkani
 */
public class EnglishStopWordCleaner extends TokenFilterCleaner {

    private static final long serialVersionUID = 3018841867887670242L;
    private static final Set<?> STOP_WORDS = EnglishAnalyzer.getDefaultStopSet();

    @Override
    public Bucket clean(final Bucket bucket) throws Exception {

        final Bucket cleaned_bucket = new Bucket();
        for (Record record : bucket) {

            final Record cleaned_record = cleanRecord(record);
            cleaned_bucket.add(cleaned_record);
        }
        return cleaned_bucket;
    }

    @Override
    protected TokenFilter getTokenFilter(final TokenStream tokenizer_stream) {

        return new StopFilter(Version.LUCENE_36, tokenizer_stream, STOP_WORDS);
    }
}
