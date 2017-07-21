package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure;

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

    private String getVarNames(String sep) throws NoTableRowsException {

        ArrayList<String> keys = new ArrayList<>(table.keySet());
        if(keys.size() == 0) {
            throw new NoTableRowsException();
        }

        CTRow row = table.get(keys.get(0));

        String s = "";

        for(Object cell : row.getCells()) {

            s += ((CTCell) cell).getVariable() + sep;

        }

        s += "freq\n";


        return s;
    }

}
