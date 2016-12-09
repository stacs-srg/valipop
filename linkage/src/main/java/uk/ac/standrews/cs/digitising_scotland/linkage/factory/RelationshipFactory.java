package uk.ac.standrews.cs.digitising_scotland.linkage.factory;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Relationship;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;


/**
 * Created by al on 03/10/2014.
 */
public class RelationshipFactory extends TFactory<Relationship> implements ILXPFactory<Relationship> {


    public RelationshipFactory(long relationshiplabelID) {
        this.required_type_labelID = relationshiplabelID;
    }


    @Override
    public Relationship create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException {
        return new Relationship(persistent_object_id, reader, repository, bucket);
    }

}
