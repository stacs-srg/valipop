package uk.ac.standrews.cs.digitising_scotland.linkage.injesters;

import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.DeathFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.MarriageFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.skye.SkyeCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.skye.SkyeCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.skye.SkyeCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.storr.impl.StoreFactory;
import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Module to injest BBM records into a store initialised by class such as InitialiseStorr in this package
 * Created by al on 25/3/2017.
 * @author al@st-andrews.ac.uk
 */

public class SkyeInjestor {

    private static final String births_name = "birth_records";              // Name of bucket containing birth records (inputs).
    private static final String marriages_name = "marriage_records";        // Name of bucket containing marriage records (inputs).
    private static final String deaths_name = "death_records";              // Name of bucket containing death records (inputs).

    // Bucket declarations

    protected IBucket<BirthFamilyGT> births;                                // Bucket containing birth records (inputs).
    protected IBucket<Marriage> marriages;                                  // Bucket containing marriage records (inputs).
    protected IBucket<Death> deaths;                                        // Bucket containing death records (inputs).

    private int births_count;
    private int marriages_count;
    private int deaths_count;

    private static final String[] ARG_NAMES = {"store_path","repo_name","skye_births_path","skye_deaths_path","skye_marriages_path"};


    public SkyeInjestor(String store_path, String repo_name ) throws StoreException, RepositoryException, IOException {
        System.out.println( "Injesting records into repo: " +  repo_name );
        initialise( store_path, repo_name );
    }

    private void initialise( String store_path, String repo_name ) throws StoreException, IOException, RepositoryException {

        Path p = Paths.get( store_path);
        if( ! p.toFile().isDirectory() ) {
            throw new RepositoryException( "Illegal store root specified");
        }
        StoreFactory.setStorePath( p );
        IStore store = StoreFactory.getStore();

        IRepository input_repo = store.getRepo(repo_name);

        TypeFactory type_factory = TypeFactory.getInstance();

        IReferenceType birthType = type_factory.typeWithname("birth");
        IReferenceType deathType = type_factory.typeWithname("death");
        IReferenceType marriageType = type_factory.typeWithname("marriage");

        births = input_repo.getBucket(births_name, new BirthFactory(birthType.getId()));
        deaths = input_repo.getBucket(deaths_name, new DeathFactory(deathType.getId()));
        marriages = input_repo.getBucket(marriages_name, new MarriageFactory(marriageType.getId()));

    }

    /**
     * Import the birth,death, marriage records
     */
    public void ingestRecords(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, BucketException, IOException {

        births_count = new SkyeCommaSeparatedBirthImporter().importDigitisingScotlandBirths(births, births_source_path);
        System.out.println("Imported " + births_count + " birth records");

        deaths_count = new SkyeCommaSeparatedDeathImporter().importDigitisingScotlandDeaths(deaths, deaths_source_path);
        System.out.println("Imported " + deaths_count + " death records");

        marriages_count = new SkyeCommaSeparatedMarriageImporter().importDigitisingScotlandMarriages(marriages, marriages_source_path);
        System.out.println("Imported " + marriages_count + " marriage records");
        System.out.println();
        System.out.println( "Injest of records complete" );
    }

    //***********************************************************************************

    public static void usage() {

        System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            String births_source_path = args[2];
            String deaths_source_path = args[3];
            String marriages_source_path = args[4];

            SkyeInjestor injestor = new SkyeInjestor( store_path, repo_name );
            injestor.ingestRecords(births_source_path,deaths_source_path,marriages_source_path);

        } else {
            usage();
        }
    }

}
