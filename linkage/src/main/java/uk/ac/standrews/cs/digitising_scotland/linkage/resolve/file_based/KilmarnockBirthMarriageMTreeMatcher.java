package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.file_based;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.kilmarnock.KilmarnockCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.kilmarnock.KilmarnockCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.kilmarnock.KilmarnockCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.storr.impl.LXP;
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

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public abstract class KilmarnockBirthMarriageMTreeMatcher {

    // Repositories and stores

    private static String input_repo_name = "BDM_repo";                             // input repository containing event records
    private static String blocked_birth_repo_name = "blocked_birth_repo";           // repository for blocked BirthFamilyGT records
    private static String FFNFLNMFNMMNPOMDOM_repo_name = "FFNFLNMFNMMNPOMDOM_repo";   // repository for blocked Marriage records
    private static String FFNFLNMFNMMN_repo_name = "FFNFLNMFNMMN_repo";   // repository for blocked Marriage records

    private static String linkage_repo_name = "linkage_repo";                       // repository for Relationship records

    private IStore store;
    private IRepository input_repo;             // Repository containing buckets of BDM records
    private IRepository role_repo;
    private IRepository blocked_births_repo;
    private IRepository FFNFLNMFNMMNPOMDOM_repo;
    private IRepository FFNFLNMFNMMN_repo;
    private IRepository linkage_repo;

    // Bucket declarations

    private IBucket<BirthFamilyGT> births;                     // Bucket containing birth records (inputs).
    private IBucket<Marriage> marriages;               // Bucket containing marriage records (inputs).
    private IBucket<Death> deaths;                     // Bucket containing death records (inputs).

    private IBucket<Role> roles;                      // Bucket containing roles extracted from BDM records
    private IBucket<Relationship> relationships;      // Bucket containing relationships between Roles

    // Paths to sources

    private static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    private static String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
    private static String deaths_name = "death_records";                            // Name of bucket containing marriage records (inputs).

    // Names of buckets

    private static String role_name = "roles";                                   // Name of bucket containing roles extracted from BDM records
    private static String relationships_name = "relationships";                  // Name of bucket containing Relationship records

    private IReferenceType birthType;
    private IReferenceType deathType;
    private IReferenceType marriageType;
    private IReferenceType roleType;
    private IReferenceType relationshipType;

    private BirthFactory birthFactory;
    private DeathFactory deathFactory;
    private MarriageFactory marriageFactory;
    private RoleFactory roleFactory;
    private RelationshipFactory relationshipFactory;
    private ArrayList<Long> oids = new ArrayList<>();
    private int birth_count;
    private int death_count;
    private int marriage_count;
    private int families_with_parents;
    private int families_with_children;
    private int single_children;
    private int children_in_groups;

    // Trees

    private  MTree<Marriage> marriageMtree;

    public KilmarnockBirthMarriageMTreeMatcher(String births_source_path, String deaths_source_path, String marriages_source_path) throws BucketException, RecordFormatException, IOException, RepositoryException, StoreException, JSONException {

        System.out.println("Running KilmarnockBirthMarriageMTreeMatcher" );
        System.out.println("Initialising");
        initialise();

        System.out.println("Ingesting");
        ingestBDMRecords(births_source_path, deaths_source_path, marriages_source_path);
        //        checkBDMRecords();

    }

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {

        Path store_path = Files.createTempDirectory(null);

        StoreFactory.setStorePath(store_path);
        store = StoreFactory.makeStore();

        System.out.println("Store path = " + store_path);

        input_repo = store.makeRepository(input_repo_name);
        blocked_births_repo = store.makeRepository(blocked_birth_repo_name);  // a repo of BirthFamilyGT Buckets of records blocked by parents names, DOM, Place of Marriage.
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

    private void initialiseTypes() {

        TypeFactory tf = TypeFactory.getInstance();

        birthType = tf.createType(BirthFamilyGT.class, "birth");
        deathType = tf.createType(Death.class, "death");
        marriageType = tf.createType(Marriage.class, "marriage");
        roleType = tf.createType(Role.class, "role");
        relationshipType = tf.createType(Relationship.class, "relationship");
    }

    private void initialiseFactories() {

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
    private void ingestBDMRecords(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, BucketException, IOException {

        System.out.println("Importing BDM records");

        birth_count = new KilmarnockCommaSeparatedBirthImporter().importDigitisingScotlandBirths(births, births_source_path);
        System.out.println("Imported " + birth_count + " birth records");
        death_count = new KilmarnockCommaSeparatedDeathImporter().importDigitisingScotlandDeaths(deaths, deaths_source_path);
        System.out.println("Imported " + death_count + " death records");
        marriage_count = new KilmarnockCommaSeparatedMarriageImporter().importDigitisingScotlandMarriages(marriages, marriages_source_path);
        System.out.println("Imported " + marriage_count + " marriage records");

        // createRoles( births, deaths, marriages );
    }

    private void checkBDMRecords() {

        System.out.println("Checking");
        checkIngestedBirths();
        checkIngestedDeaths();
        checkIngestedMarriages();
    }

    private void checkIngestedBirths() {

        IInputStream<BirthFamilyGT> stream = null;
        try {
            stream = births.getInputStream();
        }
        catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Births bucket");
            return;
        }

        for (LXP l : stream) {
            BirthFamilyGT birth_record = null;
            try {
                birth_record = (BirthFamilyGT) l;
                System.out.println("BirthFamilyGT for: " + birth_record.get(BirthFamilyGT.FORENAME) + " " + birth_record.get(BirthFamilyGT.SURNAME) + " m: " + birth_record.get(BirthFamilyGT.MOTHERS_FORENAME) + " " + birth_record.get(BirthFamilyGT.MOTHERS_SURNAME) + " f: " + birth_record.get(BirthFamilyGT.FATHERS_FORENAME) + " " + birth_record
                                .get(BirthFamilyGT.FATHERS_SURNAME) + " read OK");

            }
            catch (ClassCastException e) {
                System.out.println("LXP found (not birth): oid: " + l.getId() + "object: " + l);
                System.out.println("class of l: " + l.getClass().toString());
            }
        }
    }

    private void checkIngestedDeaths() {

        IInputStream<Death> stream = null;
        try {
            stream = deaths.getInputStream();
        }
        catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Death bucket");
            return;
        }

        System.out.println("Checking Deaths");

        for (Death death_record : stream) {
            System.out.println("Death for: " + death_record.get(Death.FORENAME) + " " + death_record.get(Death.SURNAME) + " m: " + death_record.get(Death.MOTHERS_FORENAME) + " " + death_record.get(Death.MOTHERS_SURNAME) + " f: " + death_record.get(Death.FATHERS_FORENAME) + " " + death_record
                            .get(Death.FATHERS_SURNAME) + " read OK");
        }
    }

    private void checkIngestedMarriages() {

        IInputStream<Marriage> stream = null;
        try {
            stream = marriages.getInputStream();
        }
        catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Death bucket");
            return;
        }

        System.out.println("Checking Marriages");

        for (Marriage marriage_record : stream) {
            System.out.println("Marriage for b: " + marriage_record.get(Marriage.BRIDE_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_SURNAME) + " g: " + marriage_record.get(Marriage.GROOM_FORENAME) + " " + marriage_record.get(Marriage.GROOM_SURNAME));
            System.out.println("\tbm: " + marriage_record.get(Marriage.BRIDE_MOTHERS_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME) + " bf: " + marriage_record.get(Marriage.BRIDE_FATHERS_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_FATHERS_SURNAME));
            System.out.println("\tgm: " + marriage_record.get(Marriage.GROOM_MOTHERS_FORENAME) + " " + marriage_record.get(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME) + " gf: " + marriage_record.get(Marriage.GROOM_FATHERS_FORENAME) + " " + marriage_record.get(Marriage.GROOM_FATHERS_SURNAME));
        }
    }

}
