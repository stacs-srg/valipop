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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure;

import java.io.PrintStream;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class ContingencyTable {

    public static final double MINIMUM_THRESHOLD = 0.0001;
    @SuppressWarnings("rawtypes")
    protected Map<String, ContingencyTableRow> table = new HashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void outputToFile(final PrintStream printStream) throws NoTableRowsException {

        printStream.print(getVarNames());

        for (final ContingencyTableRow row : table.values())
            if (row.countGreaterThan(MINIMUM_THRESHOLD))
                printStream.print(row.toString(","));

        printStream.close();
    }

    @SuppressWarnings("rawtypes")
    private String getVarNames() throws NoTableRowsException {

        final List<String> keys = new ArrayList<>(table.keySet());
        if (keys.isEmpty())
            throw new NoTableRowsException();

        final ContingencyTableRow row = table.get(keys.getFirst());

        final StringBuilder s = new StringBuilder();

        for (final Object cell : row.getCells())
            s.append(((ContingencyTableCell) cell).getVariable()).append(",");

        s.append("freq\n");

        return s.toString();
    }
}
