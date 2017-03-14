package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.storr.impl.StoreFactory;
import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public class KilmarnockMTreeMatcherGroundTruthChecker {

    // Repositories and stores

    protected static String input_repo_name = "BDM_repo";                             // input repository containing event records
    protected static String blocked_birth_repo_name = "blocked_birth_repo";           // repository for blocked BirthFamilyGT records
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

    protected IBucket<BirthFamilyGT> births;                     // Bucket containing birth records (inputs).
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

    protected HashMap<Long, Family> families = new HashMap<>(); // Maps from person id to family.


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

    protected void initialiseTypes() {

        TypeFactory tf = TypeFactory.getInstance();

        birthType = tf.createType(BirthFamilyGT.class, "birth");
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

    /**
     * Display the  families in CSV format
     * All generated family tags are empty for unmatched families
     * Tests that families do not appear in map more than once, which can occur in some experiments.
     */
    public void listFamilies() throws BucketException {

        IInputStream<BirthFamilyGT> stream = births.getInputStream();

        System.out.println("Generated fid\tDemographer fid\tRecord id\tForname\tSurname\tDOB\tPOM\tDOM\tFather's forename\tFather's surname\tMother's forename\tMother's maidenname" );


        for (BirthFamilyGT b : stream) {

            Long key = b.getId();
            Family f = families.get(key);
            if (f == null) {
                System.out.println("" + "\t" + b.getString(BirthFamilyGT.FAMILY) + "\t" + b.getString(BirthFamilyGT.ORIGINAL_ID) + "\t" + b.getString(BirthFamilyGT.FORENAME) + "\t" + b.getString(BirthFamilyGT.SURNAME) + "\t" + b.getDOB() + "\t" + b.getPlaceOfMarriage() + "\t" + b.getDateOfMarriage() + "\t" + b.getFathersForename() + "\t" + b.getFathersSurname() + "\t" + b.getMothersForename() + "\t" + b.getMothersMaidenSurname());
            } else {
                System.out.println(f.id + "\t" + b.getString(BirthFamilyGT.FAMILY) + "\t" + b.getString(BirthFamilyGT.ORIGINAL_ID) + "\t" + b.getString(BirthFamilyGT.FORENAME) + "\t" + b.getString(BirthFamilyGT.SURNAME) + "\t" + b.getDOB() + "\t" + b.getPlaceOfMarriage() + "\t" + b.getDateOfMarriage() + "\t" + b.getFathersForename() + "\t" + b.getFathersSurname() + "\t" + b.getMothersForename() + "\t" + b.getMothersMaidenSurname());
            }
        }
    }

    public void calculateLinkageStats() throws BucketException {

        IInputStream<BirthFamilyGT> stream = births.getInputStream();

        System.out.println("Calculating linkage stats");

        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

//      for each assigned family, how many members are there in the family
        HashMap<Integer, Integer> assignedFamilyCounts = new HashMap<Integer, Integer>();
//      for how many individuals do fail to assign a family id
        int assignedFamilyMissing = 0;

//      same for each real (=demographer) family.
        HashMap<String, Integer> realFamilyCounts = new HashMap<String, Integer>();
//      for how many individuals did the demographers fail to assign a family id
        int realFamilyMissing = 0;


        for (BirthFamilyGT b1 : stream) {
            Family b1AssignedFamily = families.get(b1.getId());
            if (b1AssignedFamily != null) {
                Integer b1AssignedCount = assignedFamilyCounts.get(b1AssignedFamily.id);
                if (b1AssignedCount == null) {
                    assignedFamilyCounts.put(b1AssignedFamily.id, 1);
                } else {
                    assignedFamilyCounts.put(b1AssignedFamily.id, b1AssignedCount + 1);
                }
            } else {
                assignedFamilyMissing++;
            }

            String b1RealFamilyId = b1.getString(BirthFamilyGT.FAMILY);
            if (b1RealFamilyId != "") {
                Integer b1RealCount = realFamilyCounts.get(b1RealFamilyId);
                if (b1RealCount  == null) {
                    realFamilyCounts.put(b1RealFamilyId, 1);
                } else {
                    realFamilyCounts.put(b1RealFamilyId, b1RealCount+1);
                }
            } else {
                realFamilyMissing++;
            }

            for (BirthFamilyGT b2 : stream) {
                Family b2AssignedFamily = families.get(b2.getId());
                String b2RealFamilyId = b2.getString(BirthFamilyGT.FAMILY);

                if (b1AssignedFamily != null && b2AssignedFamily != null && b1AssignedFamily.id == b2AssignedFamily.id) {
                    if (b1RealFamilyId != "" && b2RealFamilyId != "" && b2RealFamilyId == b2RealFamilyId) {
                        truePositives++;
                    }
                    else {
                        falsePositives++;
                    }
                }
                else {
                    if (b1RealFamilyId != "" && b2RealFamilyId != "" && b2RealFamilyId == b2RealFamilyId) {
                        falseNegatives++;
                    }
                }
            }
        }

        System.out.println("Assigned family stats");
        System.out.println("Number of families                : " + assignedFamilyCounts.size());
        System.out.println("Max-size of families              : " + Collections.max(assignedFamilyCounts.values()));
        System.out.println("Min-size of families              : " + Collections.min(assignedFamilyCounts.values()));
        System.out.println("Mean-size of families             : " + calcMean(assignedFamilyCounts.values()));
        System.out.println("Individuals with missing families : " + assignedFamilyMissing);
        System.out.println();
        System.out.println("Real family stats");
        System.out.println("Number of families                : " + realFamilyCounts.size());
        System.out.println("Max-size of families              : " + Collections.max(realFamilyCounts.values()));
        System.out.println("Min-size of families              : " + Collections.min(realFamilyCounts.values()));
        System.out.println("Mean-size of families             : " + calcMean(realFamilyCounts.values()));
        System.out.println("Individuals with missing families : " + realFamilyMissing);
        System.out.println();
        System.out.println("False Negatives : " + falseNegatives);
        System.out.println("True Positives  : " + truePositives);
        System.out.println("False Positives : " + falsePositives);
        System.out.println("False Negatives : " + falseNegatives);
        if ((truePositives + falsePositives) == 0 || (truePositives + falseNegatives) == 0) {
            System.out.println("Cannot calculate precision and recall.");
        }
        else {
            int precision = truePositives / (truePositives + falsePositives);
            int recall    = truePositives / (truePositives + falseNegatives);
            int f1measure = (2 * precision * recall) / (precision + recall);
            System.out.println("Precision       : " + precision);
            System.out.println("Recall          : " + recall);
            System.out.println("F1 Measure      : " + f1measure);
        }

    }

    private double calcMean(Collection<Integer> values) {
        int sum = 0;
        for (Integer i : values) {
            sum += i;
        }
        return (sum / values.size());
    }


}
