package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import org.json.JSONException;
import org.json.JSONWriter;
import uk.ac.standrews.cs.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.jstore.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IReferenceType;
import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPair;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.util.Set;

/**
 * Created by al on 19/06/2014.
 */
public class Pair<T extends ILXP> extends AbstractLXP implements IPair<T> { //TODO fix up this - conside typing and empty methods
    private T first;
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public Pair(long persistent_Object_id, JSONReader reader, long required_type_labelID) throws PersistentObjectException, IllegalKeyException {
        super(persistent_Object_id, reader);
    }

    public T first() {
        return first;
    }

    public T second() {
        return second;
    }

    // TODO all bodged below here

    @Override
    public long getTypeLabel() {
        return -1;
    }    // TODO write these!

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public void serializeToJSON(JSONWriter writer) throws JSONException {
    }

    @Override
    public Object get(String label) throws KeyNotFoundException {
        return null;
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public double getDouble(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        return 0;
    }

    @Override
    public int getInt(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        return 0;
    }

    @Override
    public boolean getBoolean(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        return false;
    }

    @Override
    public long getLong(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        return 0;
    }

    @Override
    public void put(String key, String value) {
    }

    @Override
    public void put(String label, double value) {

    }

    @Override
    public void put(String label, int value) {

    }

    @Override
    public void put(String label, boolean value) {

    }

    @Override
    public void put(String label, long value) {

    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public Set<String> getLabels() {
        return null;
    }

    @Override
    public void addTypeLabel(IReferenceType personlabel) throws Exception {

    }


    @Override
    public ILXP create(long label_id, JSONReader reader) throws PersistentObjectException {
        return null;
    }


}
