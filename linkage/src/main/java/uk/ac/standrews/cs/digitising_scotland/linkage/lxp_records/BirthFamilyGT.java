package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

/**
 * Birth Record extended wih Family ground truth for processing Kilmarnock and Isle of Skye datasets
 * <p>
 * Created by al on 03/10/2014.
 */
public class BirthFamilyGT extends Birth {

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FAMILY = "family";

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FAMILY_BEWARE = "family beware";

    public BirthFamilyGT() {

        super();
    }

    public BirthFamilyGT(long persistent_Object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_Object_id, reader, repository, bucket);
    }
}
