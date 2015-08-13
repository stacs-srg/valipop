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

    public static final Classification UNCLASSIFIED = new Classification("UNCLASSIFIED", TokenList.UNMODIFIABLE_TOKEN_SET, 0.0, null);

    private static final long serialVersionUID = 7074436345885045033L;

    private String code;
    private TokenList token_list;
    private double confidence;
    private String detail;

    public Classification() {
    }

    public Classification(final String code, final TokenList token_list, final double confidence, final String detail) {

        this.code = code;
        this.token_list = token_list;
        this.confidence = confidence;
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public TokenList getTokenList() {
        return token_list;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getDetail() {
        return detail;
    }

    // TODO check whether equals() and hashCode() needed

    @Override
    public boolean equals(final Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Classification that = (Classification) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(token_list, that.token_list) &&
                Objects.equals(confidence, that.confidence);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, token_list, confidence);
    }

    @Override
    public String toString() {

        return "Classification [code=" + code + ", token_list=" + token_list + ", confidence=" + confidence + (detail != null ? ", detail=" + detail : "") + "]";
    }

    public Classification makeClone() {

        return makeClone(confidence);
    }

    public Classification makeClone(double confidence) {

        return new Classification(code, new TokenList(token_list.toString()), confidence, detail);
    }
}
