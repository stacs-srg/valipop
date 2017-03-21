package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.DeathFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.MarriageFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.storr.impl.StoreFactory;
import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by graham on 21/03/2017.
 */
public class KilmarnockExperiment {

    private static final String input_repo_name = "BDM_repo";               // Repository containing event records

    // Bucket declarations

    protected IBucket<BirthFamilyGT> births;                                // Bucket containing birth records (inputs).
    protected IBucket<Marriage> marriages;                                  // Bucket containing marriage records (inputs).
    protected IBucket<Death> deaths;                                        // Bucket containing death records (inputs).

    private int births_count;
    private int marriages_count;
    private int deaths_count;

    // Paths to sources

    private static final String births_name = "birth_records";              // Name of bucket containing birth records (inputs).
    private static final String marriages_name = "marriage_records";        // Name of bucket containing marriage records (inputs).
    private static final String deaths_name = "death_records";              // Name of bucket containing death records (inputs).

    private IReferenceType birthType;
    private IReferenceType deathType;
    private IReferenceType marriageType;

    private BirthFactory birthFactory;
    private DeathFactory deathFactory;
    private MarriageFactory marriageFactory;
    private List<Long> oids = new ArrayList<>();

    public KilmarnockExperiment() throws StoreException, IOException, RepositoryException {

        Path store_path = Files.createTempDirectory(null);

        StoreFactory.setStorePath(store_path);
        IStore store = StoreFactory.makeStore();

        IRepository input_repo = store.makeRepository(input_repo_name);

        initialiseTypes();
        initialiseFactories();

        births = input_repo.makeBucket(births_name, BucketKind.DIRECTORYBACKED, birthFactory);
        deaths = input_repo.makeBucket(deaths_name, BucketKind.DIRECTORYBACKED, deathFactory);
        marriages = input_repo.makeBucket(marriages_name, BucketKind.DIRECTORYBACKED, marriageFactory);
    }

    /**
     * Import the birth,death, marriage records
     */
    public void ingestRecords(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, BucketException, IOException {

        births_count = KilmarnockCommaSeparatedBirthImporter.importDigitisingScotlandBirths(births, births_source_path);
        System.out.println("Imported " + births_count + " birth records");

        deaths_count = KilmarnockCommaSeparatedDeathImporter.importDigitisingScotlandDeaths(deaths, deaths_source_path);
        System.out.println("Imported " + deaths_count + " death records");

        marriages_count = KilmarnockCommaSeparatedMarriageImporter.importDigitisingScotlandMarriages(marriages, marriages_source_path);
        System.out.println("Imported " + marriages_count + " marriage records");
        System.out.println();
    }

    public IBucket<BirthFamilyGT> getBirths() {

        return births;
    }

    public IBucket<Marriage> getMarriages() {

        return marriages;
    }

    public IBucket<Death> getDeaths() {

        return deaths;
    }

    public int getBirthsCount() {

        return births_count;
    }

    public int getMarriagesCount() {

        return marriages_count;
    }

    public int getDeathsCount() {

        return deaths_count;
    }

    public void timedRun(String description, Callable<Void> func) throws Exception {

        System.out.println(description);
        long time = System.currentTimeMillis();
        func.call();
        long elapsed = (System.currentTimeMillis() - time) / 1000;
        System.out.println(description + " - took " + elapsed + " seconds.");
    }

    private void initialiseTypes() {

        TypeFactory tf = TypeFactory.getInstance();

        birthType = tf.createType(BirthFamilyGT.class, "birth");
        deathType = tf.createType(Death.class, "death");
        marriageType = tf.createType(Marriage.class, "marriage");
    }

    private void initialiseFactories() {

        birthFactory = new BirthFactory(birthType.getId());
        deathFactory = new DeathFactory(deathType.getId());
        marriageFactory = new MarriageFactory(marriageType.getId());
    }
}
