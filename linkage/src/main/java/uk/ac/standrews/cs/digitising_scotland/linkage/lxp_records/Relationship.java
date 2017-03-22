package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.StoreReference;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.types.LXP_REF;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

import static uk.ac.standrews.cs.storr.types.LXPBaseType.STRING;

/**
 * Created by al on 17/8/16
 */
public class Relationship extends LXP {

    public enum relationship_kind {fatherof, motherof, marriedto}

    @LXP_REF(type = "role")
    public static final String SUBJECT = "subject";

    @LXP_REF(type = "role")
    public static final String OBJECT = "object";

    @LXP_SCALAR(type = STRING)
    public static final String RELATIONSHIP = "relationship";

    @LXP_SCALAR(type = STRING)
    public static final String EVIDENCE = "evidence";

    public Relationship() {

        super();
    }

    public Relationship(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_object_id, reader, repository, bucket);
    }

    public Relationship(StoreReference<Role> subject, StoreReference<Role> object, relationship_kind relationship, String evidence) throws StoreException {

        this();

        try {
            put(SUBJECT, subject);
            put(OBJECT, object);
            put(RELATIONSHIP, relationship.name());
            put(EVIDENCE, evidence);
        }
        catch (IllegalKeyException e) {
            ErrorHandling.error("Illegal key in OID");
        }
    }

    public Role getSubject() {

        try {
            return (Role) getRef(SUBJECT).getReferend();
        }
        catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get subject from Relationship");
            return null;
        }
    }

    public Role getObject() {

        try {
            return (Role) getRef(OBJECT).getReferend();
        }
        catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get object from Relationship");
            return null;
        }
    }

    public relationship_kind getRelationship() {

        return relationship_kind.valueOf(getString(RELATIONSHIP));
    }

    public String getEvidence() {

        return getString(EVIDENCE);
    }
}
