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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.WeightedDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.version3.Person;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Random;

/**
 * Created by tsd4 on 11/11/2015.
 */
public class LifeTableRow {

    public int survivers = 0;
    public int deathPoint;
    // Age
    private int x;
    // Span of years this table pertains to
    private int n;
    // The rate of death of the group of people aged x in the n following years
    private double nMx;
    // The risk (proability) of death of an individual aged x in the next n years
    private double nqx;


    public LifeTableRow(int x, int n, double nMx, double nqx) {

        this.x = x;
        this.n = n;
        this.nMx = nMx;
        this.nqx = nqx;
        deathPoint = (int) (1 / nqx);
    }


    public int getX() {
        return x;
    }

    public int getN() {
        return n;
    }

    public double getnMx() {
        return nMx;
    }

    public boolean toDieByNQX() {
        if (survivers >= deathPoint) {
            survivers = 0;
            return true;
        } else {
            survivers++;
            return false;
        }

    }

    public double getNqx() {
        return nqx;
    }

}
