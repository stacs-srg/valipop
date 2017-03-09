package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.KillieBirth;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;

import java.util.*;

/**
 * Essentially a set of siblings carrying an id.
 * Created by al on 28/02/2017.
 */
public class Family implements Comparable<Family> {

    public static int family_id = 1;

    public HashMap<KillieBirth,List<DataDistance<KillieBirth>>> distances; // strictly we do not need both distances and siblings but keep for now.
    public Set<KillieBirth> siblings;
    public final int id;

    public Family() {
        this.id = family_id++;
        this.distances = new HashMap<KillieBirth,List<DataDistance<KillieBirth>>>();
        this.siblings = new TreeSet<KillieBirth>();
    }

    public Family(KillieBirth child) {
        this();
        siblings.add(child);
    }

    public void addDistance( KillieBirth sibling, DataDistance<KillieBirth> distance ) {
        if( distances.containsKey( sibling ) ) {
            distances.get( sibling ).add( distance );
        } else {
            List<DataDistance<KillieBirth>> l = new ArrayList<DataDistance<KillieBirth>>();
            l.add( distance );
            distances.put( sibling,l );
        }
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
}
