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
package model.analysis.lifetable;

import model.Person;
import model.statistics.lifetable.LifeTable;
import model.statistics.lifetable.LifeTableCatalogue;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by tsd4 on 12/11/2015.
 */
public class LifeTableCatalogueShadow {

    private LifeTableCatalogue lifeTableCatalogue;

    private TreeMap<Integer, LifeTableShadow> tables = new TreeMap<Integer, LifeTableShadow>();


    public LifeTableCatalogueShadow(LifeTableCatalogue lifeTableCatalogue, int startYear, int endYear) {
        this.lifeTableCatalogue = lifeTableCatalogue;

        TreeMap<Integer, LifeTable> tree = lifeTableCatalogue.getCloneOfTreeMap();

        for (int y = startYear+1; y < endYear; y += 1) {
            tables.put(y, new LifeTableShadow(tree.get(tree.floorKey(y)), y + 1, y));
        }


//        Iterator<Integer> iter = tree.keySet().iterator();
//
//        while(iter.hasNext()) {
//
//            Integer i = iter.next();
//
//            if(iter.hasNext()) {
//                tables.put(i, new LifeTableShadow(tree.get(i), DateManipulation.dateToDays(tree.ceilingKey(i+1) - 1,0,0)));
//            } else {
//                tables.put(i, new LifeTableShadow(tree.get(i), Integer.MAX_VALUE));
//            }
//
//        }

//        System.out.println(tables.size());

    }

    public Double[] analyse(List<Person> population) {

//        System.out.println("HHH");
//        System.out.println(DateManipulation.differenceInYears(2000, 2010));
//        System.out.println(DateManipulation.differenceInYears(2100, 2000));


        Double[] rSquares = new Double[tables.size()];
        int p = 0;

        for (Integer i : tables.keySet()) {
//            System.out.println("Y: " + i);
            LifeTableShadow lts = tables.get(i);
//            System.out.println(lts.toString().split("@")[1]);
            lts.reviewPopulation(population);
            rSquares[p++] = lts.calcRSquared();
        }

        return rSquares;

    }


}
