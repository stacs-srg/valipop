package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.KilmarnockCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.DeathFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.MarriageFactory;
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
import java.util.concurrent.Callable;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public abstract class KilmarnockMTreeMatcherGroundTruthChecker {

    // Repositories and stores

    private static final String input_repo_name = "BDM_repo";                             // input repository containing event records
    private static final String blocked_birth_repo_name = "blocked_birth_repo";           // repository for blocked BirthFamilyGT records
    private static final String FFNFLNMFNMMNPOMDOM_repo_name = "FFNFLNMFNMMNPOMDOM_repo";   // repository for blocked Marriage records
    private static final String FFNFLNMFNMMN_repo_name = "FFNFLNMFNMMN_repo";   // repository for blocked Marriage records

    private static final String linkage_repo_name = "linkage_repo";                       // repository for Relationship records

    // Bucket declarations

    protected IBucket<BirthFamilyGT> births;                     // Bucket containing birth records (inputs).
    protected IBucket<Marriage> marriages;               // Bucket containing marriage records (inputs).
    protected IBucket<Death> deaths;                     // Bucket containing death records (inputs).

    // Paths to sources

    private static final String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    private static final String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
    private static final String deaths_name = "death_records";                            // Name of bucket containing marriage records (inputs).

    // Names of buckets

    private static final String relationships_name = "relationships";                  // Name of bucket containing Relationship records

    private IReferenceType birthType;
    private IReferenceType deathType;
    private IReferenceType marriageType;
    private IReferenceType roleType;
    private IReferenceType relationshipType;

    private BirthFactory birthFactory;
    private DeathFactory deathFactory;
    private MarriageFactory marriageFactory;
    //    private RelationshipFactory relationshipFactory;
    private ArrayList<Long> oids = new ArrayList<>();

    // Maps

    protected Map<Long, Family> families = new HashMap<>(); // Maps from person id to family.

    KilmarnockMTreeMatcherGroundTruthChecker(String births_source_path, String deaths_source_path, String marriages_source_path) throws StoreException, JSONException, RecordFormatException, RepositoryException, IOException, BucketException {

        System.out.println("Initialising");
        initialise();

        System.out.println("Ingesting");
        ingestBDMRecords(births_source_path, deaths_source_path, marriages_source_path);
    }

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {

        Path store_path = Files.createTempDirectory(null);

        StoreFactory.setStorePath(store_path);
        IStore store = StoreFactory.makeStore();

        System.out.println("Store path = " + store_path);

        IRepository input_repo = store.makeRepository(input_repo_name);
//        IRepository blocked_births_repo = store.makeRepository(blocked_birth_repo_name);
//        IRepository FFNFLNMFNMMNPOMDOM_repo = store.makeRepository(FFNFLNMFNMMNPOMDOM_repo_name);
//        IRepository FFNFLNMFNMMN_repo = store.makeRepository(FFNFLNMFNMMN_repo_name);

        IRepository linkage_repo = store.makeRepository(linkage_repo_name);
        initialiseTypes();
        initialiseFactories();

        births = input_repo.makeBucket(births_name, BucketKind.DIRECTORYBACKED, birthFactory);
        deaths = input_repo.makeBucket(deaths_name, BucketKind.DIRECTORYBACKED, deathFactory);
        marriages = input_repo.makeBucket(marriages_name, BucketKind.DIRECTORYBACKED, marriageFactory);
//        IBucket<Relationship> relationships = linkage_repo.makeBucket(relationships_name, BucketKind.DIRECTORYBACKED, relationshipFactory);
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
//        RoleFactory roleFactory = new RoleFactory(roleType.getId());
//        relationshipFactory = new RelationshipFactory(relationshipType.getId());
    }

    /**
     * Import the birth,death, marriage records
     * Initialises the roles bucket with the roles injected - one record for each person referenced in the original record
     * Initialises the known(100% certain) relationships between roles and stores the relationships in the relationships bucket
     */
    private void ingestBDMRecords(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, BucketException, IOException {

        System.out.println("Importing BDM records");
        int birth_count = KilmarnockCommaSeparatedBirthImporter.importDigitisingScotlandBirths(births, births_source_path, oids);
        System.out.println("Imported " + birth_count + " birth records");
        int death_count = KilmarnockCommaSeparatedDeathImporter.importDigitisingScotlandDeaths(deaths, deaths_source_path, oids);
        System.out.println("Imported " + death_count + " death records");
        int marriage_count = KilmarnockCommaSeparatedMarriageImporter.importDigitisingScotlandMarriages(marriages, marriages_source_path, oids);
        System.out.println("Imported " + marriage_count + " marriage records");
    }

    /**
     * Display the  families in CSV format
     * All generated family tags are empty for unmatched families
     * Tests that families do not appear in map more than once, which can occur in some experiments.
     */
    void listFamilies() throws BucketException {

        IInputStream<BirthFamilyGT> stream = births.getInputStream();

        System.out.println("Generated fid\tDemographer fid\tRecord id\tForname\tSurname\tDOB\tPOM\tDOM\tFather's forename\tFather's surname\tMother's forename\tMother's maidenname");

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

    void calculateLinkageStats() throws BucketException {

//      first: person-id
//      second: assigned-family
//      third: real family

        List<SimpleTuple3<Long, Integer, String>> birthIDs = new ArrayList<>();

        for (BirthFamilyGT b : births.getInputStream()) {

            Family assignedFam = families.get(b.getId());
            Integer assignedFamId = null;
            if (assignedFam != null) {
                assignedFamId = assignedFam.id;
            }
            birthIDs.add(new SimpleTuple3<Long, Integer, String>(b.getId(), assignedFamId, b.getString(BirthFamilyGT.FAMILY)));
        }

        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

//      for each assigned family, how many members are there in the family
        Map<Integer, Integer> assignedFamilyCounts = new HashMap<>();
//      for how many individuals do fail to assign a family id
        int assignedFamilyMissing = 0;

//      same for each real (=demographer) family.
        Map<String, Integer> realFamilyCounts = new HashMap<>();
//      for how many individuals did the demographers fail to assign a family id
        int realFamilyMissing = 0;

        for (SimpleTuple3<Long, Integer, String> b1 : birthIDs) {

            Integer b1AssignedFamily = b1.second;
            String b1RealFamilyId = b1.third;

            if (b1RealFamilyId != null) {

                if (b1AssignedFamily != null) {
                    Integer b1AssignedCount = assignedFamilyCounts.get(b1AssignedFamily);
                    if (b1AssignedCount == null) {
                        assignedFamilyCounts.put(b1AssignedFamily, 1);
                    } else {
                        assignedFamilyCounts.put(b1AssignedFamily, b1AssignedCount + 1);
                    }
                } else {
                    assignedFamilyMissing++;
                }

                if (b1RealFamilyId.length() > 0) {
                    Integer b1RealCount = realFamilyCounts.get(b1RealFamilyId);
                    if (b1RealCount == null) {
                        realFamilyCounts.put(b1RealFamilyId, 1);
                    } else {
                        realFamilyCounts.put(b1RealFamilyId, b1RealCount + 1);
                    }
                } else {
                    realFamilyMissing++;
                }

                for (SimpleTuple3<Long, Integer, String> b2 : birthIDs) {

                    Integer b2AssignedFamily = b2.second;
                    String b2RealFamilyId = b2.third;

                    if (b1AssignedFamily != null && !b1AssignedFamily.equals(b2AssignedFamily)) {
                        if (b1RealFamilyId.length() > 0 && b1RealFamilyId.equals(b2RealFamilyId)) {
                            truePositives++;
                        } else {
                            falsePositives++;
                        }
                    } else {
                        if (b1RealFamilyId.length() > 0 && b1RealFamilyId.equals(b2RealFamilyId)) {
                            falseNegatives++;
                        }
                    }
                }
            }
        }

        System.out.println("Assigned family stats");
        printFamilyStats(assignedFamilyCounts.values());
        System.out.println("Individuals with missing families : " + assignedFamilyMissing);
        System.out.println();

        System.out.println("Real family stats");
        printFamilyStats(realFamilyCounts.values());
        System.out.println("Individuals with missing families : " + realFamilyMissing);
        System.out.println();

        System.out.println("False Negatives : " + falseNegatives);
        System.out.println("True Positives  : " + truePositives);
        System.out.println("False Positives : " + falsePositives);
        System.out.println("False Negatives : " + falseNegatives);

        if ((truePositives + falsePositives) == 0 || (truePositives + falseNegatives) == 0) {
            System.out.println("Cannot calculate precision and recall.");
        } else {
            double precision = truePositives / (truePositives + falsePositives);
            double recall = truePositives / (truePositives + falseNegatives);
            double f1measure = (2 * precision * recall) / (precision + recall);

            System.out.println("Precision       : " + precision);
            System.out.println("Recall          : " + recall);
            System.out.println("F1 Measure      : " + f1measure);
        }
    }

    private void printFamilyStats(Collection<Integer> values) {

        try {
            System.out.println("Number of families                : " + values.size());
            System.out.println("Max-size of families              : " + Collections.max(values));
            System.out.println("Min-size of families              : " + Collections.min(values));
            System.out.println("Mean-size of families             : " + calcMean(values));
        } catch (Exception e) {
            System.out.println("No families");
        }
    }

    void timedRun(String description, Callable<Void> func) throws Exception {

        System.out.println(description);
        long time = System.currentTimeMillis();
        func.call();
        long elapsed = (System.currentTimeMillis() - time) / 1000;
        System.out.println(description + " - took " + elapsed + " seconds.");
    }

    private double calcMean(Collection<Integer> values) {

        int sum = 0;
        for (Integer i : values) {
            sum += i;
        }
        return (sum / values.size());
    }

    class SimpleTuple3<X, Y, Z> {

        final X first;
        final Y second;
        final Z third;

        SimpleTuple3(X first, Y second, Z third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }
}
