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

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.VariableNotFoundExcepction;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class CTRow<count extends Number> {

    private count count;

    public count getCount() {
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
            new CTCell("CIY", "NO"),
            new CTCell("NCIY", "0"),
            new CTCell("NCIP", "0"),
            new CTCell("Separated", "NA"),
            new CTCell("NPA", "na")
    };

    protected Collection<CTCell> cells = new ArrayList<>(Arrays.asList(c));

    public Collection<CTCell> getCells() {
        return cells;
    }

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

    public CTCell addDateVariable() throws VariableNotFoundExcepction {

        try {
            Integer yob = Integer.valueOf(getVariable("YOB").getValue());
            Integer age = Integer.valueOf(getVariable("Age").getValue());

            Integer date = yob + age;

            addVariable("Date", String.valueOf(date));

            return getVariable("Date");

        } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
            throw new Error(variableNotFoundExcepction.getMessage(), variableNotFoundExcepction);
        } catch (NumberFormatException e) {
            throw new VariableNotFoundExcepction("Unfilled Row");
        }
    }

    public void deleteVariable(String variable) {
        try {
            cells.remove(getVariable(variable));
        } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
            // this is okay - it's effectivly deleted as it ain't there in the first place
        }
    }

    public void discritiseVariable(String variable, String forInput, PopulationStatistics inputStatistics) {

        if(Objects.equals(variable, "Age")) {

            Date date;
            int age;

            try {
                Integer yob = Integer.parseInt(getVariable("YOB").getValue());
                age = Integer.parseInt(getVariable("Age").getValue());
                date = new YearDate(yob + age);
            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
                try {
                    age = Integer.parseInt(getVariable("Age").getValue());
                    date = new YearDate(Integer.parseInt(getVariable("Date").getValue()));
                } catch (VariableNotFoundExcepction variableNotFoundExcepction1) {
                    throw new Error();
                }

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

//    public boolean tryAbsorbRow(CTRow<count> row) {
//
//        boolean identicalRows = true;
//
//        for(CTCell cell : cells) {
//            String givenRowValue = null;
//            try {
//                givenRowValue = row.getVariable(cell.getVariable()).getValue();
//            } catch (VariableNotFoundExcepction variableNotFoundExcepction) {
//                return false;
//            }
//            String thisRowValue = cell.getValue();
//
//            if(!Objects.equals(givenRowValue, thisRowValue)) {
//                identicalRows = false;
//            }
//        }
//
//        if(identicalRows) {
//            setCount(combineCount(getCount(), row.count));
//        }
//
//        return identicalRows;
//    }

    public abstract count combineCount(count a, count b);

    public String toString(String sep) {

        StringBuilder s = new StringBuilder();

        for(CTCell cell : cells) {
            s.append(cell.getValue() + sep);
        }

        s.append(getCount() + "\n");

        return s.toString();

    }

    public String hash() {

        StringBuilder s = new StringBuilder();
        for(CTCell cell : cells) {
            s.append(cell.getVariable() + cell.getValue());
        }

        return s.toString();
    }

    public abstract int getIntegerCount();

    public abstract boolean countEqualToZero();

    public abstract boolean countGreaterThan(Double v);
}
