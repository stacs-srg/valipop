package uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder;


import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.NoSuitableBucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * Blocker takes a stream and blocks it into buckets based on the assigner
 * Created by al on 29/04/2014.
 */
public abstract class Blocker<T extends ILXP> implements IBlocker<T> {

    private final IInputStream<T> input;
    private final IRepository output_repo;
    private ILXPFactory<T> factory;

    /**
     * @param input       the stream over which to block
     * @param output_repo - the repository into which results are written
     */
    public Blocker(final IInputStream<T> input, final IRepository output_repo, ILXPFactory<T> factory) {

        this.input = input;
        this.output_repo = output_repo;
        this.factory = factory;
    }

    public IInputStream getInput() {
        return input;
    }

    /**
     * Apply the method assign to all (non-null) records in the stream
     */
    @Override
    public void apply() {

        for (T record : input) {
            if (record != null) {
                assign(record);
            }
        }
    }

    @Override
    public void assign(final T record) {

 //       System.out.println( "LXP in assign: oid: " +  record.getId() + "object: " + record );
 //       System.out.println( "class of l: " + record.getClass().toString() );

        String[] bucket_names = null;
            try {
                bucket_names = determineBlockedBucketNamesForRecord(record);
            } catch (NoSuitableBucketException e) {
                ErrorHandling.error("No suitable bucket for record: " + record);
                return;
            }
            for (String bucket_name : bucket_names ) {

                if( bucket_name == null || bucket_name.equals( "" ) ) {
                    ErrorHandling.error( "Illegal (empty or null) name encountered whilst creating bucket for: " + record);
                    return;
                }
                if (output_repo.bucketExists(bucket_name)) {
                    try {
                        output_repo.getBucket(bucket_name, factory).getOutputStream().add(record);
                    } catch (RepositoryException | BucketException e) {
                        ErrorHandling.exceptionError(e, "Exception obtaining bucket instance for record: " + record );
                    }
                } else { // need to create it
                    try {
                        output_repo.makeBucket(bucket_name, BucketKind.INDIRECT, factory).getOutputStream().add(record);
                    } catch (RepositoryException | BucketException e) {
                        ErrorHandling.exceptionError(e, "Exception creating bucket for record: " + record);
                    }
                }
            }
    }

    public abstract String[] determineBlockedBucketNamesForRecord(T record) throws NoSuitableBucketException;
}
