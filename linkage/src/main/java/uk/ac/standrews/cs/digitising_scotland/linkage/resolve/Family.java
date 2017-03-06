package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.KillieBirth;

import java.util.Set;
import java.util.TreeSet;

/**
 * Essentially a set of siblings carrying an id.
 * Created by al on 28/02/2017.
 */
public class Family {

    public static int family_id = 1;

    public Set<KillieBirth> siblings;
    public final int id;


    public Family(KillieBirth child) {
        this.id = family_id++;
        this.siblings = new TreeSet<KillieBirth>();
        siblings.add(child);
    }
}
