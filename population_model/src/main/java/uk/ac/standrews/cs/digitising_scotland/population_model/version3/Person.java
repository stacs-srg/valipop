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
package uk.ac.standrews.cs.digitising_scotland.population_model.version3;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;

/**
 * Created by tsd4 on 10/11/2015.
 */
public class Person {

    private int dob;
    private boolean sex;
    private int dod = -1;

    public Person(int dob, boolean sex) {
        this.dob = dob;
        this.sex = sex;
    }

    public Person(Date dob, boolean sex) {
        this(DateManipulation.dateToDays(dob), sex);
    }

    public int getAge(int currentDay) {
        return DateManipulation.differenceInYears(dob, currentDay);
    }

    public void die(int currentDay) {
        dod = currentDay;
    }

    public Date getDobDate() {
        return DateManipulation.daysToDate(dob);
    }

    public int getDob() {
        return dob;
    }

    public boolean isSex() {
        return sex;
    }
}
