/*
 * valipop - <https://github.com/stacs-srg/valipop>
 * Copyright © 2025 Systems Research Group, University of St Andrews (graham.kirby@st-andrews.ac.uk)
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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure;

import java.util.*;

import static uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes.NewPartnerAgeNodeInt.PARTNER_AGE_LABEL;

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
        new ContingencyTableCell("Source", ""),
        new ContingencyTableCell("YOB", ""),
        new ContingencyTableCell("Sex", ""),
        new ContingencyTableCell("Age", ""),
        new ContingencyTableCell("Died", ""),
        new ContingencyTableCell("PNCIP", "0"),
        new ContingencyTableCell("NPCIAP", "0"),
        new ContingencyTableCell("CIY", "NO"),
        new ContingencyTableCell("NCIY", "0"),
        new ContingencyTableCell("NCIP", "0"),
        new ContingencyTableCell("Separated", "NA"),
        new ContingencyTableCell(PARTNER_AGE_LABEL, "na")
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

        final int yearOfBirth = Integer.parseInt(getVariable("YOB").getValue());
        final int age = Integer.parseInt(getVariable("Age").getValue());

        final int year = yearOfBirth + age + offset;

        addVariable("Date", String.valueOf(year));
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

        s.append(getCount()).append("\n");

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

    public abstract boolean countGreaterThan(Double v);
}
