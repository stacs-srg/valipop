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
import java.util.Objects;

public class Classification implements Serializable {

    //FIXME The token set in #UNCLASSIFIED is modifiable; need unmodifiable token set.
    public static final Classification UNCLASSIFIED = new Classification("UNCLASSIFIED", new TokenSet(), 0.0);

    private static final long serialVersionUID = 7074436345885045033L;

    private final String code; // TODO find a better name for this field; too domain-specific, maybe id?
    private final TokenSet tokenSet;
    private final double confidence; //TODO Introduce confidence type.

    public Classification(final String code, final TokenSet tokenSet, final double confidence) {

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

    public double getConfidence() {

        return confidence;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Classification that = (Classification) o;
        return Objects.equals(code, that.code) &&
                        Objects.equals(tokenSet, that.tokenSet) &&
                        Objects.equals(confidence, that.confidence);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, tokenSet, confidence);
    }

    @Override
    public String toString() {

        return "Classification [code=" + code + ", tokenSet=" + tokenSet + ", confidence=" + confidence + "]";
    }
}
