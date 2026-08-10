/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2026 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables;

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SourceType;

import java.io.PrintStream;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class ContingencyTable {

    // The string constants defined here must match the variables used in formulae in
    // resources/valipop/analysis-r/geeglm/validation-analysis.R.

    public static final String LABEL_DATE = "Date";
    public static final String LABEL_SOURCE = "Source";
    public static final String LABEL_YEAR_OF_BIRTH = "YOB";
    public static final String LABEL_SEX = "Sex";
    public static final String LABEL_AGE = "Age";
    public static final String LABEL_PARTNER_AGE = "PartnerAge";
    public static final String LABEL_SEPARATED = "Separated";
    public static final String LABEL_DIED = "Died";

    public static final String LABEL_CHILDREN_IN_YEAR = "CIY";
    public static final String LABEL_NUMBER_OF_CHILDREN_IN_YEAR = "NCIY";
    public static final String LABEL_NUMBER_OF_CHILDREN_IN_PARTNERSHIP = "NCIP";
    public static final String LABEL_NUMBER_OF_PREVIOUS_CHILDREN_IN_PARTNERSHIP = "PNCIP";
    public static final String LABEL_NUMBER_OF_PREVIOUS_CHILDREN_IN_ANY_PARTNERSHIP = "NPCIAP";

    public static final Set<String> NUMERICAL_VARIABLES = Set.of(
        LABEL_DATE, LABEL_YEAR_OF_BIRTH, LABEL_AGE, LABEL_NUMBER_OF_CHILDREN_IN_YEAR,
        LABEL_NUMBER_OF_CHILDREN_IN_PARTNERSHIP, LABEL_NUMBER_OF_PREVIOUS_CHILDREN_IN_PARTNERSHIP,
        LABEL_NUMBER_OF_PREVIOUS_CHILDREN_IN_ANY_PARTNERSHIP
    );

    @SuppressWarnings("rawtypes")
    protected final Map<String, ContingencyTableRow> table = new TreeMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void outputToFile(final PrintStream printStream, final double frequencyThreshold) throws NoTableRowsException {

        final Set<String> keys = table.keySet();
        if (keys.isEmpty())
            throw new NoTableRowsException();

        final ContingencyTableRow sampleRow = table.values().iterator().next();

        for (final ContingencyTableCell cell : (Collection<ContingencyTableCell>) sampleRow.getCells()) {

            printStream.print(cell.getVariable());
            printStream.print(",");
        }

        printStream.print("Frequency\n");

        // Previously there was a minimum threshold of 0.0001 for frequency, below which a table row would not
        // be output. This has been removed since it seems important to include zeros in the validation process.

        // Since the table is a TreeMap, the rows will be output in key order. A row key is the concatenation of
        // the row values.

        keys.stream().
            map(table::get).
            filter(row -> row.countGreaterThanOrEqual(frequencyThreshold)).
            map(row -> row.toString(",")).
            forEach(printStream::print);

        printStream.close();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void scaleTargetFrequencies() {

        final double targetScaleFactor = calculateTargetScaleFactor();

        for (final ContingencyTableRow row : table.values()) {

            if (row.getVariable(LABEL_SOURCE).getValue().equals(SourceType.TARGET.toString()))
                row.setCount(row.getIntegerCount() * targetScaleFactor);
        }
    }

    @SuppressWarnings("rawtypes")
    private double calculateTargetScaleFactor() {

        double simulatedTotal = 0.0;
        double targetTotal = 0.0;

        for (final ContingencyTableRow row : table.values()) {

            if (row.getVariable(LABEL_SOURCE).getValue().equals(SourceType.TARGET.toString()))
                targetTotal += row.getIntegerCount() ;
            else
                simulatedTotal += row.getIntegerCount();
        }

        return simulatedTotal / targetTotal;
    }
}
