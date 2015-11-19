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
package uk.ac.standrews.cs.digitising_scotland.population_model.version3.lifetable.analysis;

import uk.ac.standrews.cs.digitising_scotland.population_model.version3.lifetable.LifeTableRow;

/**
 * Created by tsd4 on 12/11/2015.
 */
public class LifeTableRowShadow {

    private LifeTableRow row;

    private int peopleInRowAtMidInterval = 0;
    private int peopleInRowDieingInInterval = 0;

    public LifeTableRowShadow(LifeTableRow row) {
        this.row = row;
    }

    public LifeTableRow getRow() {
        return row;
    }

    public double getCalculatedNMX() {
        if (peopleInRowAtMidInterval == 0) {
//            System.out.println("Zero in row --- " + row.getX());
            return 0;
        } else {
            System.out.println(peopleInRowDieingInInterval + "\t" + peopleInRowAtMidInterval);
            return peopleInRowDieingInInterval / (double) peopleInRowAtMidInterval;
        }
    }

    public double getResidualSquared() {
        double v = Math.pow((row.getnMx() - getCalculatedNMX()), 2);
//        System.out.println(row.getX() + "-" + (row.getX()+row.getN()) + " --- sur{" + row.survivers + "/" + row.deathPoint + "}");
//        System.out.println(row.getnMx() + " -m " + getCalculatedNMX() + " = " + (row.getnMx() - getCalculatedNMX()));
        return v;
    }

    public void incPeopleInRow() {
        peopleInRowAtMidInterval++;
    }

    public void incPeopleDieingInRow() {
        peopleInRowDieingInInterval++;
    }


    public double getExpectedNMX() {
        return row.getnMx();
    }

    public int getPeopleInRow() {
        return peopleInRowAtMidInterval;
    }

    public int getPeopleDieingInRow() {
        return peopleInRowDieingInInterval;
    }

}
