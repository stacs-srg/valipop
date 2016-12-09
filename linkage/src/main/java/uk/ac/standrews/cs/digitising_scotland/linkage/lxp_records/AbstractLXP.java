package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;


import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

/**
 * Created by al on 14/10/2014.
 */
public abstract class AbstractLXP extends LXP {

//    public long required_type_labelID;

    public AbstractLXP() {

        super();
    }


    public AbstractLXP(long object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        super(object_id, reader, repository, bucket);
    }

}
