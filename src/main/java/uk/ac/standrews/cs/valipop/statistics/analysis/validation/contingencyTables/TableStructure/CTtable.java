/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure;

import java.io.PrintStream;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class CTtable {

    protected Map<String, CTRow> table = new HashMap<>();

    public void outputToFile(PrintStream ps) throws NoTableRowsException {

        int simZeroFreqs = 0;
        int statZeroFreqs = 0;
        ps.print(getVarNames());

        for (CTRow row : table.values()) {

            // TODO paramaterise this?
            if (row.countGreaterThan(0.0001)) {

                ps.print(row.toString(","));

                Collection<CTCell> cells = row.getCells();

                CTRowInt twin = new CTRowInt(cells);

                if (Objects.equals(row.getVariable("Source").getValue(), "STAT")) {

                    twin.setVariable("Source", "SIM");
                    CTRow t = table.get(twin.hash());

                    if (t == null) {
                        simZeroFreqs++;
                    }

                } else {

                    twin.setVariable("Source", "STAT");
                    CTRow t = table.get(twin.hash());

                    if (t == null) {
                        statZeroFreqs++;
                    }
                }
            }
        }

        ps.close();
    }

    private String getVarNames() throws NoTableRowsException {

        ArrayList<String> keys = new ArrayList<>(table.keySet());
        if (keys.size() == 0) {
            throw new NoTableRowsException();
        }

        CTRow row = table.get(keys.get(0));

        StringBuilder s = new StringBuilder();

        for (Object cell : row.getCells()) {

            s.append(((CTCell) cell).getVariable()).append(",");
        }

        s.append("freq\n");

        return s.toString();
    }
}
