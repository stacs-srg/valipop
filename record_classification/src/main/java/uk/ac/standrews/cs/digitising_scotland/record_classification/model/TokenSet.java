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
package uk.ac.standrews.cs.digitising_scotland.record_classification.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 *{@link TokenSet}s are used to represent a bag of tokens. Tokens in this context are strings that are whitespace delimited, ie single words.
 * TokenSets can contain multiple of the same token.
 * 
 * Created by fraserdunlop on 09/06/2014 at 10:06.
 */
public class TokenSet extends HashSet<String> implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4771078200991926082L;

    // Need to escape the -, [, ] symbols in the regular expression, and then escape the backslashes in the string.
    private static final String SEPARATOR_CHARACTERS = "[ ,\\-\\[\\]]+";

    /**
     * Instantiates a new token set.
     */
    public TokenSet() {

        super();
    }

    /**
     * Instantiates a new token set.
     *
     * @param string the string
     */
    public TokenSet(final String string) {

        this();

        for (String word : string.split(SEPARATOR_CHARACTERS)) {

            String cleaned_word = cleanWord(word);
            if (cleaned_word.length() > 0) {
                add(cleaned_word);
            }
        }
    }

    /**
     * Instantiates a new token set.
     *
     * @param tokenSet the token set
     */
    public TokenSet(final Collection<String> tokenSet) {

        this();
        addAll(tokenSet);
    }

    private String cleanWord(String word) {

        String cleaned_word = word.toLowerCase();
        while (cleaned_word.length() > 0 && !isLetter(cleaned_word.charAt(0))) cleaned_word = cleaned_word.substring(1);
        while (cleaned_word.length() > 0 && !isLetter(cleaned_word.charAt(cleaned_word.length() - 1)))
            cleaned_word = cleaned_word.substring(0, cleaned_word.length() - 1);
        return cleaned_word;
    }

    private boolean isLetter(char c) {
        return c >= 'a' && c <= 'z';
    }
}
