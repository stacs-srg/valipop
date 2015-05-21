/*
 * Copyright 2014 Digitising Scotland project:
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
package uk.ac.standrews.cs.digitising_scotland.record_classification_cleaned;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

public class Classification2 {

    private final String code;
    private final TokenSet tokenSet;
    private final Double confidence;

    public Classification2(final String code, final TokenSet tokenSet, final Double confidence) {

        this.code = code;
        this.tokenSet = tokenSet;
        this.confidence = confidence;
    }

    public String getCode() {

        return code;
    }

    public TokenSet getTokenSet() {

        return tokenSet;
    }

    public Double getConfidence() {

        return confidence;
    }

    @Override
    public String toString() {

        return "Classification [code=" + code + ", tokenSet=" + tokenSet + ", confidence=" + confidence + "]";
    }
}
