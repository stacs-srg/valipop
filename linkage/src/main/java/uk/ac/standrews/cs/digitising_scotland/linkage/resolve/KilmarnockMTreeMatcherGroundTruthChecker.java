package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
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
import java.util.HashMap;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeMatcherGroundTruthChecker {

    // Repositories and stores

    protected static String input_repo_name = "BDM_repo";                             // input repository containing event records
    protected static String blocked_birth_repo_name = "blocked_birth_repo";           // repository for blocked KillieBirth records
    protected static String FFNFLNMFNMMNPOMDOM_repo_name = "FFNFLNMFNMMNPOMDOM_repo";   // repository for blocked Marriage records
    protected static String FFNFLNMFNMMN_repo_name = "FFNFLNMFNMMN_repo";   // repository for blocked Marriage records

    protected static String linkage_repo_name = "linkage_repo";                       // repository for Relationship records

    protected IStore store;
    protected IRepository input_repo;             // Repository containing buckets of BDM records
    protected IRepository role_repo;
    protected IRepository blocked_births_repo;
    protected IRepository FFNFLNMFNMMNPOMDOM_repo;
    protected IRepository FFNFLNMFNMMN_repo;
    protected IRepository linkage_repo;

    // Bucket declarations

    protected IBucket<KillieBirth> births;                     // Bucket containing birth records (inputs).
    protected IBucket<Marriage> marriages;               // Bucket containing marriage records (inputs).
    protected IBucket<Death> deaths;                     // Bucket containing death records (inputs).

    protected IBucket<Role> roles;                      // Bucket containing roles extracted from BDM records
    protected IBucket<Relationship> relationships;      // Bucket containing relationships between Roles

    // Paths to sources

    protected static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    protected static String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
    protected static String deaths_name = "death_records";                            // Name of bucket containing marriage records (inputs).

    // Names of buckets

    protected static String role_name = "roles";                                   // Name of bucket containing roles extracted from BDM records
    protected static String relationships_name = "relationships";                  // Name of bucket containing Relationship records

    protected IReferenceType birthType;
    protected IReferenceType deathType;
    protected IReferenceType marriageType;
    protected IReferenceType roleType;
    protected IReferenceType relationshipType;

    protected BirthFactory birthFactory;
    protected DeathFactory deathFactory;
    protected MarriageFactory marriageFactory;
    protected RoleFactory roleFactory;
    protected RelationshipFactory relationshipFactory;
    protected ArrayList<Long> oids = new ArrayList<>();
    protected int birth_count;
    protected int death_count;
    protected int marriage_count;

    // Maps

    protected HashMap< String, Family > family_ground_truth_map = new HashMap<>(); // Maps from FAMILY in birth record to a family unit using ground truth
    protected HashMap< String, Family > inferred_family_map = new HashMap<>(); // Maps from FAMILY in birth record to a family unit using M tree derived data.
    protected HashMap< String, Family > unmatched_map = new HashMap<>(); // Unmatched families

    // Trees

    private  MTree<KillieBirth> birthMTree;


    public KilmarnockMTreeMatcherGroundTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path ) throws StoreException, JSONException, RecordFormatException, RepositoryException, IOException, BucketException {

        System.out.println("Initialising");
        initialise();

        System.out.println("Ingesting");
        ingestBDMRecords(births_source_path, deaths_source_path, marriages_source_path);
        //        checkBDMRecords();
    }

    protected void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {

        Path store_path = Files.createTempDirectory(null);

        StoreFactory.setStorePath(store_path);
        store = StoreFactory.makeStore();

        System.out.println("Store path = " + store_path);

        input_repo = store.makeRepository(input_repo_name);
        blocked_births_repo = store.makeRepository(blocked_birth_repo_name);  // a repo of KillieBirth Buckets of records blocked by parents names, DOM, Place of Marriage.
        FFNFLNMFNMMNPOMDOM_repo = store.makeRepository(FFNFLNMFNMMNPOMDOM_repo_name);  // a repo of Marriage Buckets
        FFNFLNMFNMMN_repo = store.makeRepository(FFNFLNMFNMMN_repo_name);  // a repo of Marriage Buckets

        linkage_repo = store.makeRepository(linkage_repo_name);
        initialiseTypes();
        initialiseFactories();

        births = input_repo.makeBucket(births_name, BucketKind.DIRECTORYBACKED, birthFactory);
        deaths = input_repo.makeBucket(deaths_name, BucketKind.DIRECTORYBACKED, deathFactory);
        marriages = input_repo.makeBucket(marriages_name, BucketKind.DIRECTORYBACKED, marriageFactory);
        relationships = linkage_repo.makeBucket(relationships_name, BucketKind.DIRECTORYBACKED, relationshipFactory);
    }

    protected void initialiseTypes() {

        TypeFactory tf = TypeFactory.getInstance();

        birthType = tf.createType(KillieBirth.class, "birth");
        deathType = tf.createType(Death.class, "death");
        marriageType = tf.createType(Marriage.class, "marriage");
        roleType = tf.createType(Role.class, "role");
        relationshipType = tf.createType(Relationship.class, "relationship");
    }

    protected void initialiseFactories() {

        birthFactory = new BirthFactory(birthType.getId());
        deathFactory = new DeathFactory(deathType.getId());
        marriageFactory = new MarriageFactory(marriageType.getId());
        roleFactory = new RoleFactory(roleType.getId());
        relationshipFactory = new RelationshipFactory(relationshipType.getId());
    }

    /**
     * Import the birth,death, marriage records
     * Initialises the roles bucket with the roles injected - one record for each person referenced in the original record
     * Initialises the known(100% certain) relationships between roles and stores the relationships in the relationships bucket
     *
     * @param births_source_path
     * @param deaths_source_path
     * @param marriages_source_path
     */
    protected void ingestBDMRecords(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, BucketException, IOException {

        System.out.println("Importing BDM records");
        birth_count = KilmarnockCommaSeparatedBirthImporter.importDigitisingScotlandBirths(births, births_source_path, oids);
        System.out.println("Imported " + birth_count + " birth records");
        death_count = KilmarnockCommaSeparatedDeathImporter.importDigitisingScotlandDeaths(deaths, deaths_source_path, oids);
        System.out.println("Imported " + death_count + " death records");
        marriage_count = KilmarnockCommaSeparatedMarriageImporter.importDigitisingScotlandMarriages(marriages, marriages_source_path, oids);
        System.out.println("Imported " + marriage_count + " marriage records");

    }

    private void createBirthMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Creating M Tree of births by GFNGLNBFNBMNPOMDOMDistanceOverBirth...");

        birthMTree = new MTree<KillieBirth>( new GFNGLNBFNBMNPOMDOMDistanceOverBirth() );

        IInputStream<KillieBirth> stream = births.getInputStream();

        for (KillieBirth birth : stream) {

            birthMTree.add( birth );
        }

    }

    /**
     * Display the  families in CSV format
     * All generated family tags are empty for unmatched families
     * Tests that families do not appear in map more than once, which can occur in some experiments.
     */
    public void listFamilies() throws BucketException {

        IInputStream<KillieBirth> stream = births.getInputStream();

        System.out.println("Generated fid\tDemographer fid\tRecord id\tForname\tSurname\tDOB\tPOM\tDOM\tFather's forename\tFather's surname\tMother's forename\tMother's maidenname" );


        for (KillieBirth b : stream) {

            String key = String.valueOf(b.getId());
            Family f = inferred_family_map.get(key);
            if (f == null) {
                System.out.println("" + "\t" + b.getString(KillieBirth.FAMILY) + "\t" + b.getString(KillieBirth.ORIGINAL_ID) + "\t" + b.getString(KillieBirth.FORENAME) + "\t" + b.getString(KillieBirth.SURNAME) + "\t" + b.getDOB() + "\t" + b.getPlaceOfMarriage() + "\t" + b.getDateOfMarriage() + "\t" + b.getFathersForename() + "\t" + b.getFathersSurname() + "\t" + b.getMothersForename() + "\t" + b.getMothersMaidenSurname());
            } else {
                System.out.println(f.id + "\t" + b.getString(KillieBirth.FAMILY) + "\t" + b.getString(KillieBirth.ORIGINAL_ID) + "\t" + b.getString(KillieBirth.FORENAME) + "\t" + b.getString(KillieBirth.SURNAME) + "\t" + b.getDOB() + "\t" + b.getPlaceOfMarriage() + "\t" + b.getDateOfMarriage() + "\t" + b.getFathersForename() + "\t" + b.getFathersSurname() + "\t" + b.getMothersForename() + "\t" + b.getMothersMaidenSurname());
            }
        }
    }

}
