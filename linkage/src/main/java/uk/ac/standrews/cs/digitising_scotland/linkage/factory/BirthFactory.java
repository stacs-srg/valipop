package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

/**
 * Created by al on 03/10/2014.
 */
public class BirthFactory extends TFactory<BirthFamilyGT> implements ILXPFactory<BirthFamilyGT> {


    public BirthFactory(long required_label_id) {
        this.required_type_labelID = required_label_id;
    }

    @Override
    public BirthFamilyGT create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        return new BirthFamilyGT(persistent_object_id, reader, repository, bucket);
    }

}
