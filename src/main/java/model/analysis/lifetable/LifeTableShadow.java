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
import model.Population;
import model.statistics.lifetable.LifeTable;
import model.statistics.lifetable.LifeTableRow;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.*;

/**
 * Created by tsd4 on 12/11/2015.
 */
public class LifeTableShadow {

    private LifeTable table;
    private int endDay;
    private int startDay;

    private TreeMap<Integer, LifeTableRowShadow> rows = new TreeMap<Integer, LifeTableRowShadow>();


    public LifeTableShadow(LifeTable lifetable, int endYear, int startYear) {
        table = lifetable;
        endDay = DateManipulation.dateToDays(endYear-1, 0, 0) - 1;
        startDay = DateManipulation.dateToDays(startYear-1, 0, 0);

        TreeMap<Integer, LifeTableRow> tree = table.getCloneOfTreeMap();

        for (int r : tree.keySet()) {
            rows.put(r, new LifeTableRowShadow(tree.get(r)));
        }

//        rows.put(rows.lastKey()+1, new LifeTableRowShadow(new LifeTableRow(rows.lastKey()+1,1,0,0)));

//        System.out.println("R: " + rows.size());

    }

    public void reviewPopulation(List<Person> people) {

        people.sort(Comparator.comparing(Person::getDod));

        int ageingDay = startDay + ((endDay - startDay) / 2);

//        System.out.println(DateManipulation.daysToYear(startDay) + " > " + DateManipulation.daysToYear(endDay));


        for (Person p : people) {
            int age = p.getAge(ageingDay);

            if (age >= -1) {


                if (age <= 0) {
//                    System.out.println("I'm here");
                    int ageInDays = DateManipulation.differenceInDays(startDay, p.getDob());
                    int halfYear = Population.getDaysInYear() / 2;

                    if (-halfYear < ageInDays && ageInDays < halfYear) {
                        rows.get(rows.floorKey(0)).incPeopleInRow();

                        if (startDay <= p.getDod() && p.getDod() < endDay) {
                            rows.get(rows.floorKey(0)).incPeopleDieingInRow();
                        }

                    } else if (halfYear < ageInDays) {
                        if (age < p.getAge(p.getDod())) {
                            rows.get(rows.floorKey(1)).incPeopleInRow();
//                System.out.println("PIRinc: " + rows.get(rows.floorKey(age)).getPeopleInRow());
                        }

//            System.out.println(startDay + " | " + p.getDod() + " | " + endDay);

                        if (startDay <= p.getDod() && p.getDod() < endDay) {
                            rows.get(rows.floorKey(1)).incPeopleDieingInRow();
//                System.out.println("PDIRinc: " + rows.get(rows.floorKey(age)).getPeopleDieingInRow());
                        }
                    }

                } else {

                    if (age < p.getAge(p.getDod())) {
                        rows.get(rows.floorKey(age)).incPeopleInRow();
//                System.out.println("PIRinc: " + rows.get(rows.floorKey(age)).getPeopleInRow());
                    }

//            System.out.println(startDay + " | " + p.getDod() + " | " + endDay);

                    if (startDay <= p.getDod() && p.getDod() < endDay) {
                        rows.get(rows.floorKey(age)).incPeopleDieingInRow();
//                System.out.println("PDIRinc: " + rows.get(rows.floorKey(age)).getPeopleDieingInRow());
                    }

                }

            }

        }

    }

    private double getSSE() {

        System.out.println(DateManipulation.daysToYear(startDay));

        double sum = 0;

        for (Integer y : rows.keySet()) {
            sum += rows.get(y).getResidualSquared();
        }

        return sum;
    }

    private double getTSS() {

        double sum = 0;

        for (Integer y : rows.keySet()) {
            sum += rows.get(y).getExpectedNMX();
        }

        double mean = sum / rows.keySet().size();

        double tss = 0;

        for (Integer y : rows.keySet()) {
            tss += Math.pow(rows.get(y).getExpectedNMX() - mean, 2);
        }

        return tss;

    }

    public double calcRSquared() {

//        double n = rows.keySet().size() - 1;

        double sse = getSSE();
        double tss = getTSS();


        double rSquared = 1 - (sse / tss);

//        System.out.println(sse + " / " + tss + " = " + rSquared);


        return rSquared;

    }

    public int getYear() {
        return table.getYear();
    }
}
