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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifier.linear_regression;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.Reader;
import java.util.Iterator;

/**
 * Created by fraserdunlop on 24/04/2014 at 14:08.
 */
public class StandardTokenizerIterable implements Iterable<CharTermAttribute> {

    /** The tokenizer. */
    private StandardTokenizer tokenizer;

    /**
     * Instantiates a new standard tokenizer iterable.
     *
     * @param matchVersion the match version
     * @param input the input
     */
    public StandardTokenizerIterable(final Version matchVersion, final Reader input) {

        this.tokenizer = new StandardTokenizer(matchVersion, input);
    }

    @Override
    public Iterator<CharTermAttribute> iterator() {

        return new TokenStreamIterator<>(tokenizer, CharTermAttribute.class);
    }
}
