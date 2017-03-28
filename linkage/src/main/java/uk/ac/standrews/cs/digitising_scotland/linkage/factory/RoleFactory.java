package uk.ac.standrews.cs.digitising_scotland.linkage.factory;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.TFactory;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;


/**
 * Created by al on 03/10/2014.
 */
public class RoleFactory extends TFactory<Role> implements ILXPFactory<Role> {

    public RoleFactory(long rolelabelID) {
        this.required_type_labelID = rolelabelID;
    }

    @Override
    public Role create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException {
        return new Role(persistent_object_id, reader, repository, bucket);
    }
}
