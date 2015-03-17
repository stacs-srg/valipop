package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;


import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IInputStream;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IOutputStream;
import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPair;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Pair;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Person;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.SameAs;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.AbstractPairwiseLinker;

/**
 * Created by al on 19/06/2014.
 * <p/>
 * Links ILXP records with labels drawn from @link uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels
 * Attempts to find birth records with the same person in different roles (e.g. mother-baby and father-baby).
 */
public class BirthBirthLinker extends AbstractPairwiseLinker<Person> {

    public BirthBirthLinker(final IInputStream<Person> input, final IOutputStream<Pair<Person>> output) {

        super(input, output);
    }

    @Override
    public boolean compare(final IPair<Person> pair) {

        Person first = pair.first();
        Person second = pair.second();

        // Return true if we have person in different roles

            if ((first.get_role().equals("baby") && second.get_role().equals("mother")) ||
                    (first.get_role().equals("mother") && second.get_role().equals("baby")) ||
                    (first.get_role().equals("baby") && second.get_role().equals("father")) ||
                    (first.get_role().equals("father") && second.get_role().equals("baby"))) {
                return true;
            }
        return false;
    }

    @Override
    public void addToResults(final IPair pair, final IOutputStream results) { // TODO these are not typed properly - look at USES OF PAIR

        Person first = (Person) pair.first();  // TODO check dynamic casting
        Person second = (Person) pair.second();

        // getString the people in the right order parent first

        ILXP result_record = new SameAs(first, second, "???", 1.0f);

        results.add(result_record);


    }
}
