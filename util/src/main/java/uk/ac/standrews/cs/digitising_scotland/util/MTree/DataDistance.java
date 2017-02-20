package uk.ac.standrews.cs.digitising_scotland.util.MTree;

/**
 * Used by MTree to return data and distance from the data.
 * Created by al on 20/02/2017.
 */
public class DataDistance<T> {

    public T value;
    public float distance;

    public DataDistance( T data, float distance ) {
        this.value = data;
        this.distance = distance;
    }
}
