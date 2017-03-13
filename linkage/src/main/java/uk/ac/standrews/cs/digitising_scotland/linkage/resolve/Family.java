package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;

import java.util.*;

/**
 * Essentially a set of siblings carrying an id.
 * Created by al on 28/02/2017.
 */
public class Family implements Comparable<Family> {

    public static int family_id = 1;

    public HashMap<BirthFamilyGT,List<DataDistance<BirthFamilyGT>>> distances; // strictly we do not need both distances and siblings but keep for now.
    public Set<BirthFamilyGT> siblings;
    public final int id;

    private String pom;
    private String day_of_marriage;
    private String month_of_marriage;
    private String year_of_marriage;
    private String mothers_maiden_surname;
    private String mothers_forenname;
    private String fathers_surname;
    private String fathers_forename;


    private Family() {
        this.id = family_id++;
        this.distances = new HashMap<>();
        this.siblings = new HashSet<>();
    }

    public Family(BirthFamilyGT child) {
        this();
        siblings.add(child);
        init_parents( child );
    }

    public void addDistance(BirthFamilyGT sibling, DataDistance<BirthFamilyGT> distance ) {
        if( distances.containsKey( sibling ) ) {
            distances.get( sibling ).add( distance );
        } else {
            List<DataDistance<BirthFamilyGT>> l = new ArrayList<DataDistance<BirthFamilyGT>>();
            l.add( distance );
            distances.put( sibling,l );
        }
    }

    public String getPlaceOfMarriage() {
        return pom;
    }

    public String getDayOfMarriage() {
        return day_of_marriage;
    }

    public String getMonthOfMarriage() {
        return month_of_marriage;
    }

    public String getYearOfMarriage() {
        return year_of_marriage;
    }

    public String getMothersMaidenSurname() {
        return mothers_maiden_surname;
    }

    public String getMothersForename() {
        return mothers_forenname;
    }

    public String getFathersSurname() {
        return fathers_surname;
    }

    public String getFathersForename() {
        return fathers_forename;
    }

    @Override
    public int compareTo(Family that) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this.family_id == that.family_id) return EQUAL;
        if (this.family_id  < that.family_id) return BEFORE;
        return AFTER;
    }

    private void init_parents(BirthFamilyGT child) {
        this.pom = child.getPlaceOfMarriage();
        this.day_of_marriage = child.getString( BirthFamilyGT.PARENTS_DAY_OF_MARRIAGE );
        this.month_of_marriage = child.getString( BirthFamilyGT.PARENTS_MONTH_OF_MARRIAGE );
        this.year_of_marriage = child.getString( BirthFamilyGT.PARENTS_YEAR_OF_MARRIAGE );


        this.mothers_maiden_surname = child.getMothersMaidenSurname();
        this.mothers_forenname = child.getMothersForename();
        this.fathers_surname = child.getFathersSurname();
        this.fathers_forename = child.getFathersForename();
    }
}
