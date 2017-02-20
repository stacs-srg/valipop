package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.Distance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.MTree;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.StoreFactory;
import uk.ac.standrews.cs.storr.impl.StoreReference;
import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeMatcher {

    // Repositories and stores

    private static String input_repo_name = "BDM_repo";                             // input repository containing event records
    private static String blocked_birth_repo_name = "blocked_birth_repo";           // repository for blocked Birth records
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

    private IBucket<Birth> births;                     // Bucket containing birth records (inputs).
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

    public KilmarnockMTreeMatcher(String births_source_path, String deaths_source_path, String marriages_source_path) throws BucketException, RecordFormatException, IOException, RepositoryException, StoreException, JSONException {

        System.out.println("Initialising");
        initialise();

        System.out.println("Ingesting");
        ingestBDMRecords(births_source_path, deaths_source_path, marriages_source_path);
        //        checkBDMRecords();

        System.out.println("Creating Marriage MTree");
        long time = System.currentTimeMillis();
        createMarriageMTreeOverGFNGLNBFNBMNPOMDOM();
        long elapsed =  ( System.currentTimeMillis() - time ) / 1000 ;
        System.out.println("Created Marriage MTree in " + elapsed + "s");

        formFamilies();

        System.out.println("Finished");
    }

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {

        Path store_path = Files.createTempDirectory(null);

        StoreFactory.setStorePath(store_path);
        store = StoreFactory.makeStore();

        System.out.println("Store path = " + store_path);

        input_repo = store.makeRepository(input_repo_name);
        blocked_births_repo = store.makeRepository(blocked_birth_repo_name);  // a repo of Birth Buckets of records blocked by parents names, DOM, Place of Marriage.
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

        birthType = tf.createType(Birth.class, "birth");
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
        birth_count = KilmarnockCommaSeparatedBirthImporter.importDigitisingScotlandBirths(births, births_source_path, oids);
        System.out.println("Imported " + birth_count + " birth records");
        death_count = KilmarnockCommaSeparatedDeathImporter.importDigitisingScotlandDeaths(deaths, deaths_source_path, oids);
        System.out.println("Imported " + death_count + " death records");
        marriage_count = KilmarnockCommaSeparatedMarriageImporter.importDigitisingScotlandMarriages(marriages, marriages_source_path, oids);
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

        IInputStream<Birth> stream = null;
        try {
            stream = births.getInputStream();
        }
        catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Births bucket");
            return;
        }

        for (LXP l : stream) {
            Birth birth_record = null;
            try {
                birth_record = (Birth) l;
                System.out.println("Birth for: " + birth_record.get(Birth.FORENAME) + " " + birth_record.get(Birth.SURNAME) + " m: " + birth_record.get(Birth.MOTHERS_FORENAME) + " " + birth_record.get(Birth.MOTHERS_SURNAME) + " f: " + birth_record.get(Birth.FATHERS_FORENAME) + " " + birth_record
                                .get(Birth.FATHERS_SURNAME) + " read OK");

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

    private void createMarriageMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Creating M Tree of marriages by GFNGLNBFNBMNPOMDOMDistance...");

        marriageMtree = new MTree<Marriage>( new GFNGLNBFNBMNPOMDOMDistance() );

        IInputStream<Marriage> stream = marriages.getInputStream();

        for (Marriage marriage : stream) {

            marriageMtree.add( marriage );
        }

    }


    /**
     * Display the blocks that are formed from the SFNLNFFNFLNMFNDoMOverBirth blocking process
     */
    private void printFamilies() {

        Iterator<IBucket<Birth>> iter = blocked_births_repo.getIterator(birthFactory);

        while (iter.hasNext()) {
            IBucket<Birth> bucket = iter.next();

            String bucket_name = bucket.getName();
            System.out.println("Birth bucket name: " + bucket_name);
            // Look for parents with same blocking key
            System.out.println("Parents: ");
            if (FFNFLNMFNMMNPOMDOM_repo.bucketExists(bucket_name)) {
                printParents(bucket_name);
                families_with_parents++;
            }
            System.out.println("Children: ");
            int children_count = 0;
            try {
                for (Birth birth : bucket.getInputStream()) {
                    System.out.println("\t" + birth.toString());
                    children_count++;
                }
            }
            catch (BucketException e) {
                System.out.println("Exception whilst getting stream");
            }
            if (children_count > 1) {
                families_with_children++;
                children_in_groups += children_count;
            }
            if (children_count == 1) {
                single_children++;
            }
        }
    }

    /**
     * Display the blocks that are formed from the SFNLNFFNFLNMFNDoMOverMarriage blocking process
     */
    private void printMarriages() {

        Iterator<IBucket<Marriage>> iter = FFNFLNMFNMMNPOMDOM_repo.getIterator(marriageFactory);

        while (iter.hasNext()) {
            IBucket<Marriage> bucket = iter.next();

            System.out.println("Marriage: " + bucket.getName());
            try {
                for (Marriage m : bucket.getInputStream()) {
                    System.out.println("\t" + m.toString());
                }
            }
            catch (BucketException e) {
                System.out.println("Exception whilst getting stream");
            }
        }
    }

    /**
     * Display the blocks that are formed from the SFNLNFFNFLNMFNDoMOverParents blocking process
     */
    private void printParents(String bucket_name) {

        try {
            IBucket<Marriage> bucket = FFNFLNMFNMMNPOMDOM_repo.getBucket(bucket_name);
            IInputStream<Marriage> stream = bucket.getInputStream();
            for (Marriage m : stream) {
                System.out.println("\t PPPPPPP " + m.toString());
            }
        }
        catch (BucketException | RepositoryException e) {
            System.out.println("Exception whilst getting parents");
        }

    }

    private void printStats() {

        System.out.println("Stats:");
        System.out.println("Births: " + birth_count);
        System.out.println("Deaths: " + death_count);
        System.out.println("Marriages: " + marriage_count);

        System.out.println("Parents in families: " + families_with_parents);
        System.out.println("Sibling groups (>1 child) " + families_with_children);
        System.out.println("Single children: " + single_children);

    }

    /**
     * Return a list of parents formed from the SFNLNFFNFLNMFNDoMOverParents blocking process for some blocking key
     */
    private List<Marriage> getParents(String bucket_name) {

        List<Marriage> parents = new ArrayList<Marriage>();
        try {
            IBucket<Marriage> bucket = FFNFLNMFNMMNPOMDOM_repo.getBucket(bucket_name);
            IInputStream<Marriage> stream = bucket.getInputStream();
            for (Marriage m : stream) {
                parents.add(m);
            }
        }
        catch (BucketException | RepositoryException e) {
            System.out.println("Exception whilst getting parents");
        }
        return parents;
    }

    /**
     * Try and form families from Marriage M Tree data
     */
    private void formFamilies() {

        IInputStream<Birth> stream;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        int count = 0;

        for (Birth b : stream) {

            Marriage fake_marriage = new Marriage();
            fake_marriage.put( Marriage.GROOM_FORENAME,b.getFathersForename() );
            fake_marriage.put( Marriage.GROOM_SURNAME,b.getFathersSurname() );
            fake_marriage.put( Marriage.BRIDE_FORENAME,b.getMothersForename() );
            fake_marriage.put( Marriage.BRIDE_SURNAME,b.getMothersMaidenSurname() );
            fake_marriage.put( Marriage.PLACE_OF_MARRIAGE,b.getPlaceOfMarriage() );

            fake_marriage.put( Marriage.MARRIAGE_DAY,b.getString( Birth.PARENTS_DAY_OF_MARRIAGE ) );
            fake_marriage.put( Marriage.MARRIAGE_MONTH, b.getString( Birth.PARENTS_MONTH_OF_MARRIAGE ) );
            fake_marriage.put( Marriage.MARRIAGE_YEAR, b.getString( Birth.PARENTS_YEAR_OF_MARRIAGE ) );

            Marriage result = marriageMtree.nearestNeighbour(fake_marriage);

            count++;

        }
    }


    /**
     * Create a relationship between the parties and add to the relationship table.
     *
     * @param subject - the subject
     * @param object - the object
     * @param relationship - relationship between subject and object
     * @param evidence - of the relationship
     */
    private void createRelationship(Role subject, Role object, Relationship.relationship_kind relationship, String evidence) {

        if (subject == null || object == null) {
            //            ErrorHandling.error( "createRelationship passed null Role for (" + relationship.name() + ") subject: " + subject + " object: " + object );
            return;
        }
        StoreReference<Role> subject_ref = new StoreReference<Role>(role_repo.getName(), roles.getName(), subject.getId());
        StoreReference<Role> object_ref = new StoreReference<Role>(role_repo.getName(), roles.getName(), object.getId());

        Relationship r = null;
        try {
            r = new Relationship(subject_ref, object_ref, relationship, evidence);
            relationships.makePersistent(r);
        }
        catch (StoreException e) {
            ErrorHandling.exceptionError(e, "Store Error adding relationship: " + r);
        }
        catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Bucket Error adding relationship: " + r);
        }
    }


    private class GFNGLNBFNBMNPOMDOMDistance implements Distance<Marriage> {

        Levenshtein levenshtein = new Levenshtein();

        @Override
        public float distance(Marriage m1, Marriage m2) {

            return GFNdistance(m1,m2) + GLNdistance(m1,m2) + BFNdistance(m1,m2) + BLNdistance(m1,m2) + POMdistance(m1,m2) + DOMdistance(m1,m2);
        }

        private float GFNdistance(Marriage m1, Marriage m2) {
            return levenshtein.distance( m1.getGroomsForename(), m2.getGroomsForename() );
        }

        private float GLNdistance(Marriage m1, Marriage m2) {
            return levenshtein.distance( m1.getGroomsSurname(), m2.getGroomsSurname() );
        }

        private float BFNdistance(Marriage m1, Marriage m2) {
            return levenshtein.distance( m1.getBridesForename(), m2.getBridesForename() );
        }

        private float BLNdistance(Marriage m1, Marriage m2) {
            return levenshtein.distance( m1.getBridesSurname(), m2.getBridesSurname() );
        }

        private float POMdistance(Marriage m1, Marriage m2) {
            return ( m1.getPlaceOfMarriage().equals( "ng") || m2.getPlaceOfMarriage().equals( "ng" ) ? 0 : levenshtein.distance( m1.getPlaceOfMarriage(), m2.getPlaceOfMarriage() ) );
        }

        private float DOMdistance(Marriage m1, Marriage m2) {
            return m1.getDateOfMarriage().equals( "ng") || m2.getDateOfMarriage().equals( "ng" ) ? 0 : levenshtein.distance( m1.getDateOfMarriage(), m2.getDateOfMarriage() );
        }

        protected String normalisePlace(String place) {

            if (place.equals("") || place.equals("na") || place.equals("ng")) {
                return "___";
            }
            else {
                return place;
            }
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        String births_source_path = "/Digitising Scotland/KilmarnockBDM/births.csv";
        String deaths_source_path = "/Digitising Scotland/KilmarnockBDM/deaths.csv";
        String marriages_source_path = "/Digitising Scotland/KilmarnockBDM/marriages.csv";

        new KilmarnockMTreeMatcher(births_source_path, deaths_source_path, marriages_source_path);
    }
}
