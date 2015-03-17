package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXPFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Pair;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Person;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class PairPersonFactory extends TFactory<Pair<Person>> implements ILXPFactory<Pair<Person>> {

    public PairPersonFactory(long required_label_id) {
        this.required_type_labelID = required_label_id;
    }

    @Override
    public Pair<Person> create(long persistent_object_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {
        return new Pair<Person>(persistent_object_id, reader, required_type_labelID);
    }


}
