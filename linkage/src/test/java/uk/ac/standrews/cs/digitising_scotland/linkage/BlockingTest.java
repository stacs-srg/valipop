package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.StoreFactory;
import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.FNLFFMFOverBirths;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by al on 02/05/2014.
 */
public class BlockingTest {

    private static final String repo_path = "test_buckets";
    private static final String births_bucket_name = "births";

    private static IStore store;
    private static IBucket<Birth> births;

    private IRepository repo;
    private IReferenceType birthlabel;


    @Before
    public void setUpEachTest() throws RepositoryException, StoreException, IOException, URISyntaxException {

        Path tempStore = Files.createTempDirectory(null);

        StoreFactory.setStorePath(tempStore);
        store = StoreFactory.makeStore();

        repo = store.makeRepository(repo_path);
        TypeFactory tf = TypeFactory.getInstance();
        birthlabel = tf.createType(Birth.class, "Birth");
        births = repo.makeBucket(births_bucket_name, BucketKind.DIRECTORYBACKED, new BirthFactory(birthlabel.getId()));
    }


    @Test
    public synchronized void testPFPLMFFF() throws Exception, RepositoryException, IllegalKeyException {

        String births_source_path = new File(BlockingTest.class.getResource("/BDMSet1/birth_records.txt").toURI()).getAbsolutePath(); // TODO Make other tests follow this.

        int count = BarSeparatedEventImporter.importDigitisingScotlandBirths(births, births_source_path, birthlabel);
        System.out.println("read in " + count + " records.");
        FNLFFMFOverBirths blocker = new FNLFFMFOverBirths(births, repo);

        blocker.apply();
    }
}

