package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class CTRow<count extends Number> {

    count count;

    count getCount() {
        return count;
    }

    public void setCount(count count) {
        this.count = count;
    }

    CTCell[] c = {
            new CTCell("Source", ""),
            new CTCell("YOB", ""),
            new CTCell("Sex", ""),
            new CTCell("Age", ""),
            new CTCell("Died", ""),
            new CTCell("PNCIP", "0"),
            new CTCell("NPCIAP", "0"),
            new CTCell("CIY", "No"),
            new CTCell("NCIY", "0"),
            new CTCell("NCIP", "0"),
            new CTCell("Separated", "NA"),
            new CTCell("NPA", "na")
    };

    Collection<CTCell> cells = new ArrayList<>(Arrays.asList(c));

    public CTCell getVariable(String variable) throws VariableNotFoundExcepction {
        for(CTCell cell : cells) {
            if(Objects.equals(variable, cell.getVariable())) {
                return cell;
            }
        }
        throw new VariableNotFoundExcepction("Cell not in row");
    }

    public void setVariable(String variable, String value) {

        try {
            getVariable(variable).setValue(value);
        } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
            addVariable(variable, value);
        }

    }

    private void addVariable(String variable, String value) {
        cells.add(new CTCell(variable, value));
    }

    CTCell addDateVariable() {

        try {
            Integer yob = new Integer(getVariable("YOB").getValue());

            if(Objects.equals(getVariable("Age").getValue(), "")) {
                System.out.println("-A-");
            }

            Integer age = new Integer(getVariable("Age").getValue());

            Integer date = yob + age;

            addVariable("Date", String.valueOf(date));

            return getVariable("Date");

        } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
            throw new Error(variableNotFoundExcepction.getMessage(), variableNotFoundExcepction);
        }
    }

    void deleteVariable(String variable) {
        try {
            cells.remove(getVariable(variable));
        } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
            // this is okay - it's effectivly deleted as it ain't there in the first place
        }
    }

    void discritiseVariable(String variable, String forInput, PopulationStatistics inputStatistics) {

        if(Objects.equals(variable, "Age")) {

            Date date;
            int age;

            try {
                date = new YearDate(Integer.parseInt(getVariable("Date").getValue()));
            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                CTCell d = addDateVariable();
                date = new YearDate(Integer.parseInt(d.getValue()));
            }

            try {
                age = new Integer(getVariable("Age").getValue());
            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                throw new Error();
            }

            Collection<IntegerRange> ranges = new ArrayList<>();

            if(Objects.equals(forInput, "OB")) {
                ranges = inputStatistics.getOrderedBirthRates(date).getRowLabels();
            } else if(Objects.equals(forInput, "MB")) {
                ranges = inputStatistics.getMultipleBirthRates(date).getLabels();
            } else if(Objects.equals(forInput, "PART")) {
                ranges = inputStatistics.getPartneringRates(date).getLabels();
            }

            for(IntegerRange iR : ranges) {
                if(iR.contains(age)) {
                    setVariable("Age", iR.toString());
                    break;
                }
            }


        } else {
            throw new UnsupportedOperationException();
        }


    }

    public boolean tryAbsorbRow(CTRow<count> row) {

        boolean identicalRows = true;

        for(CTCell cell : cells) {
            String givenRowValue = null;
            try {
                givenRowValue = row.getVariable(cell.getVariable()).getValue();
            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                return false;
            }
            String thisRowValue = cell.getValue();

            if(!Objects.equals(givenRowValue, thisRowValue)) {
                identicalRows = false;
            }
        }

        if(identicalRows) {
            setCount(combineCount(getCount(), row.count));
        }

        return identicalRows;
    }

    abstract count combineCount(count a, count b);

    public String toString(String sep) {

        String s = "";

        for(CTCell cell : cells) {
            s += cell.getValue() + sep;
        }

        s += getCount() + "\n";

        return s;

    }

}
