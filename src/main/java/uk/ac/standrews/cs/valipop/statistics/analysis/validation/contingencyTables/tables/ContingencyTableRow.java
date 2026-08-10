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

import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SeparationOption;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.trees.SourceType;

import java.util.*;

import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.tables.ContingencyTable.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class ContingencyTableRow<Count extends Number> {

    private Count count;

    public Count getCount() {
        return count;
    }

    public void setCount(final Count count) {
        this.count = count;
    }

    // New ArrayList to make mutable.
    protected Collection<ContingencyTableCell> cells = new ArrayList<>(List.of(
        new ContingencyTableCell(LABEL_SOURCE, ""),
        new ContingencyTableCell(LABEL_YEAR_OF_BIRTH, ""),
        new ContingencyTableCell(LABEL_SEX, ""),
        new ContingencyTableCell(LABEL_AGE, ""),
        new ContingencyTableCell(LABEL_DIED, ""),
        new ContingencyTableCell(LABEL_NUMBER_OF_PREVIOUS_CHILDREN_IN_PARTNERSHIP, "0"),
        new ContingencyTableCell(LABEL_NUMBER_OF_PREVIOUS_CHILDREN_IN_ANY_PARTNERSHIP, "0"),
        new ContingencyTableCell(LABEL_CHILDREN_IN_YEAR, "NO"),
        new ContingencyTableCell(LABEL_NUMBER_OF_CHILDREN_IN_YEAR, "0"),
        new ContingencyTableCell(LABEL_NUMBER_OF_CHILDREN_IN_PARTNERSHIP, "0"),
        new ContingencyTableCell(LABEL_SEPARATED, SeparationOption.NA.toString()),
        new ContingencyTableCell(LABEL_PARTNER_AGE, "na")
    ));

    public Collection<ContingencyTableCell> getCells() {
        return cells;
    }

    public ContingencyTableCell getVariable(final String variable) {

        for (final ContingencyTableCell cell : cells)
            if (Objects.equals(variable, cell.getVariable()))
                return cell;

        throw new RuntimeException("Cell not in row");
    }

    public void setVariable(final String variable, final String value) {

        try {
            getVariable(variable).setValue(value);
        } catch (final RuntimeException e) {
            addVariable(variable, value);
        }
    }

    private void addVariable(final String variable, final String value) {
        cells.add(new ContingencyTableCell(variable, value));
    }

    public void addDateVariable(final int offset) {

        final int yearOfBirth = Integer.parseInt(getVariable(LABEL_YEAR_OF_BIRTH).getValue());
        final int age = Integer.parseInt(getVariable(LABEL_AGE).getValue());

        final int year = yearOfBirth + age + offset;

        addVariable(LABEL_DATE, String.valueOf(year));
    }

    public void addDateVariable() {
        addDateVariable(0);
    }

    public void deleteVariable(final String variable) {

        try {
            cells.remove(getVariable(variable));
        }
        catch (final RuntimeException ignore) {
            // this is okay - it's effectively deleted as it isn't there in the first place
        }
    }

    public abstract Count combineCount(Count a, Count b);

    public String toString(final String separator) {

        final StringBuilder s = new StringBuilder();

        for (final ContingencyTableCell cell : cells)
            s.append(cell.getValue()).append(separator);

        s.append(getIntegerCount()).append("\n");

        return s.toString();
    }

    public String toString(final String separator, final double targetScaleFactor) {

        final StringBuilder s = new StringBuilder();

        for (final ContingencyTableCell cell : cells)
            s.append(cell.getValue()).append(separator);

        if (getVariable(LABEL_SOURCE).getValue().equals(SourceType.TARGET.toString()))
            s.append(Math.round(getIntegerCount() * targetScaleFactor)).append("\n");
        else
            s.append(getIntegerCount()).append("\n");

        return s.toString();
    }

    public String hash() {

        final StringBuilder s = new StringBuilder();
        for (final ContingencyTableCell cell : cells)
            s.append(cell.getVariable()).append(cell.getValue());

        return s.toString();
    }

    public abstract int getIntegerCount();

    public abstract boolean countEqualToZero();

    public abstract boolean countGreaterThanOrEqual(Double v);
}
