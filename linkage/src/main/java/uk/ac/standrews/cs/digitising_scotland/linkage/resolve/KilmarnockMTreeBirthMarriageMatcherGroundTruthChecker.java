package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.DataDistance;
import uk.ac.standrews.cs.digitising_scotland.util.MTree.Distance;
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
import java.util.Collection;
import java.util.HashMap;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeBirthMarriageMatcherGroundTruthChecker {

    // Repositories and stores

    private static String input_repo_name = "BDM_repo";                             // input repository containing event records
    private static String blocked_birth_repo_name = "blocked_birth_repo";           // repository for blocked KillieBirth records
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

    private IBucket<KillieBirth> births;                     // Bucket containing birth records (inputs).
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

    // Trees

    private  MTree<Marriage> marriageMtree;

    // Maps

    private HashMap< String, Family > family_ground_truth_map = new HashMap<>(); // Maps from FAMILY in birth record to a family unit using ground truth
    private HashMap< String, Family > inferred_family_map = new HashMap<>(); // Maps from FAMILY in birth record to a family unit using M tree derived data.


    public KilmarnockMTreeBirthMarriageMatcherGroundTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path) throws BucketException, RecordFormatException, IOException, RepositoryException, StoreException, JSONException {

        System.out.println("Running KilmarnockMTreeBirthMarriageMatcherGroundTruthChecker" );
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

        System.out.println("Forming families from Marriage-Birth links");
        formFamilies();
        showFamilies();

        System.out.println("Finished");
    }

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {

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

    private void initialiseTypes() {

        TypeFactory tf = TypeFactory.getInstance();

        birthType = tf.createType(KillieBirth.class, "birth");
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
     * Try and form families from Marriage M Tree data_array
     */
    private void formFamilies() {

        IInputStream<KillieBirth> stream;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        for (KillieBirth b : stream) {

            Marriage marriage_query = new Marriage();
            marriage_query.put( Marriage.GROOM_FORENAME,b.getFathersForename() );
            marriage_query.put( Marriage.GROOM_SURNAME,b.getFathersSurname() );
            marriage_query.put( Marriage.BRIDE_FORENAME,b.getMothersForename() );
            marriage_query.put( Marriage.BRIDE_SURNAME,b.getMothersMaidenSurname() );
            marriage_query.put( Marriage.PLACE_OF_MARRIAGE,b.getPlaceOfMarriage() );

            marriage_query.put( Marriage.MARRIAGE_DAY,b.getString( KillieBirth.PARENTS_DAY_OF_MARRIAGE ) );
            marriage_query.put( Marriage.MARRIAGE_MONTH, b.getString( KillieBirth.PARENTS_MONTH_OF_MARRIAGE ) );
            marriage_query.put( Marriage.MARRIAGE_YEAR, b.getString( KillieBirth.PARENTS_YEAR_OF_MARRIAGE ) );

            DataDistance<Marriage> result = marriageMtree.nearestNeighbour( marriage_query );

            if( result.distance < 8.0F ) {
                add_birth_to_map(inferred_family_map, Long.toString(result.value.getId()), b); // used the marriage id as a unique identifier.
            }

        }
    }

    private void showFamilies() {
        showFamilies( inferred_family_map.values() );
    }

    /**
     * Display the generated families in CSV format
     */
    private void showFamilies( Collection<Family> families ) {

        System.out.println( "Number of families formed:" + families.size() );
        System.out.println( "Family id\tDemographer family id\tPerson id\tForename\tSurname\tDOB\tPOM\tDOM\tFFN\tFSN\tMFN\tMMN" );
        for( Family f : families ) {
            for( KillieBirth b : f.siblings ) {
                System.out.println( f.id + "\t" + b.getString( KillieBirth.FAMILY ) + "\t" + b.getId() + "\t" + b.getString( KillieBirth.FORENAME) + "\t" + b.getString( KillieBirth.SURNAME) + "\t" + b.getDOB() + "\t" + b.getPlaceOfMarriage() + "\t" + b.getDateOfMarriage() + "\t" + b.getFathersForename() + "\t" + b.getFathersSurname() + "\t" + b.getMothersForename() + "\t" + b.getMothersMaidenSurname() );
            }
        }
    }

    /**
     * Adds a birth record to a family map.
     * @param map the map to which the record should be added
     * @param birth_record the record to add to the map
     */
    private void add_birth_to_map( HashMap< String, Family > map, String key,  KillieBirth birth_record ) {
        if( map.containsKey( key ) ) { // have already seen a member of this family - so just add the birth to the family map
            // could check here to ensure parents are the same etc.
            Family f = map.get( key );
            f.siblings.add( birth_record );
        } else { // a new family we have not seen before
            Family new_family = new Family( birth_record );
            map.put( key, new_family );
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
            float day_dist = m1.getString( Marriage.MARRIAGE_DAY ).equals( "--") || m2.getString( Marriage.MARRIAGE_DAY ).equals( "--" ) ? 0 : levenshtein.distance( m1.getString( Marriage.MARRIAGE_DAY ), m2.getString( Marriage.MARRIAGE_DAY ) );
            float month_dist = m1.getString( Marriage.MARRIAGE_MONTH ).equals( "---") || m2.getString( Marriage.MARRIAGE_MONTH ).equals( "---" ) ? 0 : levenshtein.distance( m1.getString( Marriage.MARRIAGE_MONTH ), m2.getString( Marriage.MARRIAGE_MONTH ) );
            float year_dist = m1.getString( Marriage.MARRIAGE_YEAR ).equals( "----") || m2.getString( Marriage.MARRIAGE_YEAR ).equals( "----" ) ? 0 : levenshtein.distance( m1.getString( Marriage.MARRIAGE_YEAR ), m2.getString( Marriage.MARRIAGE_YEAR ) );
            return day_dist + month_dist + year_dist;
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        String births_source_path = "/Digitising Scotland/KilmarnockBDM/births.csv";
        String deaths_source_path = "/Digitising Scotland/KilmarnockBDM/deaths.csv";
        String marriages_source_path = "/Digitising Scotland/KilmarnockBDM/marriages.csv";

        new KilmarnockMTreeBirthMarriageMatcherGroundTruthChecker(births_source_path, deaths_source_path, marriages_source_path);
    }
}
