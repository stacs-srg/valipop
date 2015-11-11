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
