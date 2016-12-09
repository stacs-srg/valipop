package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;


import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPair;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Pair;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.SameAs;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.AbstractPairwiseUnifier;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IOutputStream;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * Created by al on 19/06/2014.
 * <p/>
 * Links ILXP records with labels drawn from @link uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels
 * Attempts to unify Person records representing the same person.
 */
public class PeopleUnifier extends AbstractPairwiseUnifier<Role> {

    public PeopleUnifier(final IInputStream<Role> input, final IOutputStream<Pair<Role>> output) {

        super(input, output);
    }

    @Override
    protected boolean similarEnough(float differentness) {
        return true; // TODO write me
    }

    @Override
    public float compare(final Role first, final Role second ) {


        // Return true if we have person in different roles


        // TODO This is all wrong - need a way of comparing two people - not about roles ..


            if (    first.getRole() == Role.RolePlayed.PRINCIPAL && second.getRole() == Role.RolePlayed.MOTHER ||
                    first.getRole() == Role.RolePlayed.MOTHER && second.getRole() == Role.RolePlayed.PRINCIPAL ||
                    first.getRole() == Role.RolePlayed.PRINCIPAL && second.getRole() == Role.RolePlayed.FATHER ||
                    first.getRole() == Role.RolePlayed.FATHER && second.getRole() == Role.RolePlayed.PRINCIPAL) {

                return 1.0f;
            }
        return 1.0f;
    }

    @Override
    public void addToResults(final IPair pair, float differentness, final IOutputStream results) { // TODO these are not typed properly - look at USES OF PAIR

        Role first = (Role) pair.first();  // TODO check dynamic casting
        Role second = (Role) pair.second();

        // getString the people in the right order parent first

        ILXP result_record = new SameAs(first, second, "???", differentness); // TODO maybe should be simlarity in here

        try {
            results.add(result_record);
        } catch( BucketException e ) {
            ErrorHandling.exceptionError(e, "Exception adding record to stream for record: " + result_record );

        }



    }
}
