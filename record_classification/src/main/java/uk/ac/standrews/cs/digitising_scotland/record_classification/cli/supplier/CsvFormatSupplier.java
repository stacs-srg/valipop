/*
 * Copyright 2016 Digitising Scotland project:
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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.supplier;

import org.apache.commons.csv.*;

import java.util.function.*;

/**
 * Predefined {@link CSVFormat csv format}s.
 *
 * @author Masih Hajiarab Derkani
 */
public enum CsvFormatSupplier implements Supplier<CSVFormat> {

    DEFAULT(CSVFormat.DEFAULT.withIgnoreEmptyLines()),
    EXCEL(CSVFormat.EXCEL.withIgnoreEmptyLines()),
    MYSQL(CSVFormat.MYSQL.withIgnoreEmptyLines()),
    RFC4180(CSVFormat.RFC4180.withIgnoreEmptyLines()),
    RFC4180_PIPE_SEPARATED(CSVFormat.RFC4180.withDelimiter('|').withIgnoreEmptyLines()),
    TDF(CSVFormat.TDF.withIgnoreEmptyLines());

    private final CSVFormat format;

    CsvFormatSupplier(CSVFormat format) {

        this.format = format;
    }

    @Override
    public CSVFormat get() {

        return format;
    }
}
