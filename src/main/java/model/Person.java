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
package model;

import model.utils.DateManipulation;

import java.util.Date;

/**
 * Created by tsd4 on 10/11/2015.
 */
public class Person {

    private int dob;
    private boolean sex;
    private int dod = Integer.MAX_VALUE;

    public Person(int dob, boolean sex) {
        this.dob = dob;
        this.sex = sex;
    }

    public Person(Date dob, boolean sex) {
        this(DateManipulation.dateToDays(dob), sex);
    }

    public static void main(String[] args) {

        Person p = new Person(1600 * 365, true);
        System.out.println(DateManipulation.differenceInDays(p.getDob(), 1600 * 365 + 100));
        System.out.println(p.getAge(1600 * 365 + 100));
        System.out.println(DateManipulation.differenceInDays(p.getDob(), 1600 * 365 + 365 / 2));
        System.out.println(p.getAge(1600 * 365 + 365 / 2));
        System.out.println(DateManipulation.differenceInDays(p.getDob(), 1600 * 365 + 365 / 2 + 1));
        System.out.println(p.getAge(1600 * 365 + 365 / 2 + 1));
        System.out.println(DateManipulation.differenceInDays(p.getDob(), 1600 * 365 + 350));
        System.out.println(p.getAge(1600 * 365 + 364));


    }

    public int getAge(int currentDay) {
//        if(currentDay - dob < 365*2) {
//            if (currentDay < dob) {
//                System.out.println(DateManipulation.differenceInYears(dob, currentDay) + "   Age in days: (-) " + DateManipulation.differenceInDays(dob, currentDay));
//            } else {
//                System.out.println(DateManipulation.differenceInYears(dob, currentDay) + "   Age in days: " + DateManipulation.differenceInDays(dob, currentDay));
//            }
//        }
        int age = (currentDay - dob) / Population.getDaysInYear();

        return age;
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

    public int getDod() {
        return dod;
    }

    public boolean isSex() {
        return sex;
    }


}
