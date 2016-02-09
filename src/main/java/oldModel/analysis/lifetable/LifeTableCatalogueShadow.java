/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
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
package oldModel.analysis.lifetable;

import oldModel.Person;
import oldModel.statistics.lifetable.LifeTable;
import oldModel.statistics.lifetable.LifeTableCatalogue;

import java.util.List;
import java.util.TreeMap;

/**
 * The LifeTableCatalogueShadow class shadows its contained @LifeTableCatalogue. It is initialised to create a data
 * structure which can be used post dataset generation to quantify a given population and return a statistical measure
 * of the similarity of the statistical measures of a generated population to the statistical inputs used to generate
 * it.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class LifeTableCatalogueShadow {

    private LifeTableCatalogue lifeTableCatalogue;

    // The shadow copy of the associated catalogues set of tables
    private TreeMap<Integer, LifeTableShadow> tables = new TreeMap<Integer, LifeTableShadow>();

    /**
     * Instantiates a new life table catalogue shadow.
     *
     * @param lifeTableCatalogue the life table catalogue
     * @param startYear          the start year of the data set generation
     * @param endYear            the end year
     */
    public LifeTableCatalogueShadow(LifeTableCatalogue lifeTableCatalogue, int startYear, int endYear) {
        this.lifeTableCatalogue = lifeTableCatalogue;

        TreeMap<Integer, LifeTable> tree = lifeTableCatalogue.getCloneOfTreeMap();

        for (int y = startYear + 1; y < endYear; y += 1) {
            tables.put(y, new LifeTableShadow(tree.get(tree.floorKey(y)), y + 1, y));
        }

    }

    /**
     * This method takes in a list of persons (i.e. all people generated in a population dataset simulation, populates
     * the set of shadow tables with the measured data from the given population and then returns a set of statistical
     * values that represent the similarity between the generated population and the data used to generate it.
     *
     * The statistical method currently calculates an R squared value for each shadow table where R squared is
     * calculated by dividing the sum of the squared errors by the sum of squared differences from the mean of the
     * original data set. The result of the division is then subtracted from 1. The closer the R squared value is to 1,
     * the greater the similarity beween the input statistics and the measured statistics.
     *
     * First resets shadow tables each time called.
     *
     * @param population The list of people in the generated data set.
     * @return the double [ ]
     */
    public Double[] analyse(List<Person> population) {

        resetShadowTables();

        Double[] rSquares = new Double[tables.size()];
        int p = 0;

        for (Integer i : tables.keySet()) {
            LifeTableShadow lts = tables.get(i);
            lts.reviewPopulation(population);
            rSquares[p++] = lts.calcRSquared();
        }

        return rSquares;
    }

    private void resetShadowTables() {

        for (Integer y : tables.keySet()) {
            tables.get(y).reset();
        }

    }

    public String[] getColumnHeadings() {

        int numberOfKeys = tables.keySet().size();
        String[] headings = new String[numberOfKeys * 4 + 1];

        int count = 0;

        headings[count++] = "Age";

        for(Integer i : tables.keySet()) {

            headings[count++] = i + "_DIR";
            headings[count++] = i + "_PIR";
            headings[count++] = i + "_TMR";
            headings[count++] = i + "_CMR";

        }

        return headings;

    }

    public double[][] getCatalogueData() {

        int key = tables.firstKey();
        int numberOfKeys = tables.keySet().size();

        int numberOfRows = tables.get(key).getNumberOfRows();

        double[][] data = new double[numberOfKeys * 4 + 1][numberOfRows + 1];

        for(int i = 1; i < data.length; i++) {
            data[i][0] = i;
        }

        int c = 0;

        for(Integer i : tables.keySet()) {

            for(int r = 0; r < numberOfRows; r++) {

                for(int column = (c*4)+1; column < ((c+1)*4)+1; column++) {

                }


            }

            c++;

        }

        return new double[][]{};
    }

}
