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
package uk.ac.standrews.cs.digitising_scotland.population_model.version3.lifetable;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.version3.Person;
import uk.ac.standrews.cs.digitising_scotland.population_model.version3.lifetable.analysis.LifeTableRowShadow;
import uk.ac.standrews.cs.nds.util.ErrorHandling;
import uk.ac.standrews.cs.util.tools.FileManipulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by tsd4 on 11/11/2015.
 */
public class LifeTable {

    private static final int MAX_AGE = 110;

    private static final String TAB = "\t";
    private static final String COMMENT_INDICATOR = "%";
    private TreeMap<Integer, LifeTableRow> rows = new TreeMap<Integer, LifeTableRow>();
    private int year;
    private String line;
    private String tableResourceKey;

    public  LifeTable(int year, String tableKey) {

        tableResourceKey = tableKey;
        this.year = year;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(PopulationProperties.getProperties().getProperty(tableKey)), FileManipulation.FILE_CHARSET))) {

            while ((line = reader.readLine()) != null) {

                if (line.startsWith(COMMENT_INDICATOR)) {
                    continue;
                }

                String[] lC = line.split(TAB);

                if (lC.length == 4) {
//                    System.out.println("LT: " + lC[0] + " " + lC[1] + " " + lC[2] + " " + lC[3] + " ");
                    addRow(new LifeTableRow(Integer.valueOf(lC[0]), Integer.valueOf(lC[1]), Double.valueOf(lC[2]), Double.valueOf(lC[3])));

                } else {
                    System.err.println("LifeTable data " + tableKey + " line too short");
                }

            }

        } catch (NumberFormatException e) {
            ErrorHandling.exceptionError(e, "Could not process line:" + line);
            e.printStackTrace();
        } catch (IOException e) {
            ErrorHandling.exceptionError(e, "IO Exception");
            e.printStackTrace();
        }

//        LifeTableRow row = rows.get(rows.lastKey());
//        double tempNMX = row.getnMx();
//        double taperFactor = (1 - tempNMX) / new Double((MAX_AGE - row.getX()));
//        System.out.println(taperFactor);
//        tempNMX += taperFactor;
//
//        int n = 1;
//
//        for(int i = row.getX() + 1; i <= MAX_AGE; i += n) {
//            double nMX = tempNMX;
//            tempNMX += taperFactor;
//            double nqx = (n * nMX) / (1 + (n * 0.5 * nMX));
//            LifeTableRow r = new LifeTableRow(i, 1, nMX, nqx);
//            rows.put(i, r);
//        }

    }

    public void addRow(LifeTableRow row) {
        rows.put(row.getX(), row);
    }

    public int getYear() {
        return year;
    }


    public boolean toDieByNQX(Person p, int currentDay, Random random) {



        LifeTableRow r = rows.get(rows.floorKey(p.getAge(currentDay)));

//        if(rows.floorKey(p.getAge(currentDay)) == 0) {
//            System.out.println(rows.floorKey(p.getAge(currentDay)));
//            System.out.println("X: " + r.getX());
//        }

        return r.toDieByNQX();

    }

    public TreeMap<Integer, LifeTableRow> getCloneOfTreeMap() {
        return (TreeMap<Integer, LifeTableRow>) rows.clone();
    }

    public String getTableResourceKey() {
        return tableResourceKey;
    }
}
