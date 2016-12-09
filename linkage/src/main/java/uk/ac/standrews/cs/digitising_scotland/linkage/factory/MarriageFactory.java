package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

/**
 * Created by al on 03/10/2014.
 */
public class MarriageFactory extends TFactory<Marriage> implements ILXPFactory<Marriage> {


    public MarriageFactory(long marriagelabelID) {
        this.required_type_labelID = marriagelabelID;
    }


    @Override
    public Marriage create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        return new Marriage(persistent_object_id, reader, repository, bucket);
    }

}
