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
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class CTtable {

    protected HashMap<String, CTRow> table = new HashMap<>();

    public void outputToFile(PrintStream ps) throws NoTableRowsException {

        ps.print(getVarNames(","));

        for(CTRow row : table.values()) {
            ps.print(row.toString(","));
        }

        ps.close();

    }

    protected String getVarNames(String sep) throws NoTableRowsException {

        ArrayList<String> keys = new ArrayList<>(table.keySet());
        if(keys.size() == 0) {
            throw new NoTableRowsException();
        }

        CTRow row = table.get(keys.get(0));

        StringBuilder s = new StringBuilder();

        for(Object cell : row.getCells()) {

            s.append(((CTCell) cell).getVariable() + sep);

        }

        s.append("freq\n");


        return s.toString();
    }

}
