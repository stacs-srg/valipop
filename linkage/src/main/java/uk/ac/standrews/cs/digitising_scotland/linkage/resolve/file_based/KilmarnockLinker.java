package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.file_based;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.FFNFLNMFNMMNOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.FFNFLNMFNMMNPOMDOMOverBirth;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.FFNFLNMFNMMNPOMDOMOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.kilmarnock.KilmarnockCommaSeparatedBirthImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.kilmarnock.KilmarnockCommaSeparatedDeathImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.kilmarnock.KilmarnockCommaSeparatedMarriageImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.*;
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
 * Attempt to create a linking framework
 * Created by al on 6/8/2014.
 */
public class KilmarnockLinker {

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

    public KilmarnockLinker(String births_source_path, String deaths_source_path, String marriages_source_path) throws BucketException, RecordFormatException, IOException, RepositoryException, StoreException, JSONException {

        System.out.println("Initialising");
        initialise();

        System.out.println("Ingesting");
        ingestBDMRecords(births_source_path, deaths_source_path, marriages_source_path);
        //        checkBDMRecords();

        System.out.println("Blocking");
        block();

        System.out.println("Examining Blocks");
        printMarriages();
        printFamilies();
        // formFamilies();
        printStats();

        System.out.println("Finished");
    }

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {

        Path store_path = Files.createTempDirectory(null);

        store = new Store(store_path);

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

        TypeFactory tf = store.getTypeFactory();

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
        checkRoles();
    }

    private void checkIngestedBirths() {

        IInputStream<BirthFamilyGT> stream = null;
        try {
            stream = births.getInputStream();
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Births bucket");
            return;
        }

        System.out.println("Checking Births");
        // code should look like this:
        //        for (BirthFamilyGT birth_record : stream) {
        //            System.out.println( "BirthFamilyGT for: " + birth_record.get( BirthFamilyGT.FORENAME ) + " " + birth_record.get( BirthFamilyGT.SURNAME ) + " m: " + birth_record.get( BirthFamilyGT.MOTHERS_FORENAME ) + " " + birth_record.get( BirthFamilyGT.MOTHERS_SURNAME ) + " f: " + birth_record.get( BirthFamilyGT.FATHERS_FORENAME ) + " " + birth_record.get( BirthFamilyGT.FATHERS_SURNAME ) + " read OK");
        //        }

        for (LXP l : stream) {
            BirthFamilyGT birth_record = null;
            try {
                birth_record = (BirthFamilyGT) l;
                System.out.println("BirthFamilyGT for: " + birth_record.get(BirthFamilyGT.FORENAME) + " " + birth_record.get(BirthFamilyGT.SURNAME) + " m: " + birth_record.get(BirthFamilyGT.MOTHERS_FORENAME) + " " + birth_record.get(BirthFamilyGT.MOTHERS_SURNAME) + " f: " + birth_record.get(BirthFamilyGT.FATHERS_FORENAME) + " " + birth_record
                        .get(BirthFamilyGT.FATHERS_SURNAME) + " read OK");

            } catch (ClassCastException e) {
                System.out.println("LXP found (not birth): oid: " + l.getId() + "object: " + l);
                System.out.println("class of l: " + l.getClass().toString());
            }
        }
    }

    private void checkIngestedDeaths() {

        IInputStream<Death> stream = null;
        try {
            stream = deaths.getInputStream();
        } catch (BucketException e) {
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
        } catch (BucketException e) {
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

    private void createRoles(IBucket<BirthFamilyGT> births, IBucket<Death> deaths, IBucket<Marriage> marriages) {

        System.out.println("Creating roles from birth records");
        createRolesFromBirths(births);
        System.out.println("Creating roles from death records");
        createRolesFromDeaths(deaths);
        System.out.println("Creating roles from marriage records");
        createRolesFromMarriages(marriages);
    }

    private void checkRoles() {

        IInputStream<Role> stream = null;
        try {
            stream = roles.getInputStream();
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Births bucket");
            return;
        }

        for (LXP l : stream) {
            Role role = null;
            try {
                role = (Role) l;
                System.out.println("Role for person: " + role.getForename() + " " + role.getSurname() + " role: " + role.getRole());

            } catch (ClassCastException e) {
                System.out.println("LXP found (not role): oid: " + l.getId() + "object: " + l);
                System.out.println("class of l: " + l.getClass().toString());
            }
        }
    }

    /**
     * Blocks the Births and Marriages into buckets.
     */
    private void block() throws RepositoryException, BucketException, IOException {

        blockBirths();
        blockMarriages();
    }

    private void blockBirths() throws RepositoryException, BucketException, IOException {

        System.out.println("Blocking births...");

        IBlocker blocker = new FFNFLNMFNMMNPOMDOMOverBirth(births, blocked_births_repo, birthFactory);
        blocker.apply();
    }

    private void blockMarriages() throws RepositoryException, BucketException, IOException {

        blockMarriagesFFNFLNMFNMMNPOMDOM();
        blockMarriagesFFNFLNMFNMMN();
    }

    private void blockMarriagesFFNFLNMFNMMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Blocking marriages by FFNFLNMFNMMNPOMDOM...");

        IBlocker blocker = new FFNFLNMFNMMNPOMDOMOverMarriage(marriages, FFNFLNMFNMMNPOMDOM_repo, marriageFactory);
        blocker.apply();
    }

    private void blockMarriagesFFNFLNMFNMMN() throws RepositoryException, BucketException, IOException {

        System.out.println("Blocking marriages by FFNFLNMFNMMN...");

        IBlocker blocker = new FFNFLNMFNMMNOverMarriage(marriages, FFNFLNMFNMMN_repo, marriageFactory);
        blocker.apply();
    }

    /**
     * Display the blocks that are formed from the SFNLNFFNFLNMFNDoMOverBirth blocking process
     */
    private void printFamilies() {

        Iterator<IBucket<BirthFamilyGT>> iter = blocked_births_repo.getIterator(birthFactory);

        while (iter.hasNext()) {
            IBucket<BirthFamilyGT> bucket = iter.next();

            String bucket_name = bucket.getName();
            System.out.println("BirthFamilyGT bucket name: " + bucket_name);
            // Look for parents with same blocking key
            System.out.println("Parents: ");
            if (FFNFLNMFNMMNPOMDOM_repo.bucketExists(bucket_name)) {
                printParents(bucket_name);
                families_with_parents++;
            }
            System.out.println("Children: ");
            int children_count = 0;
            try {
                for (BirthFamilyGT birth : bucket.getInputStream()) {
                    System.out.println("\t" + birth.toString());
                    children_count++;
                }
            } catch (BucketException e) {
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
            } catch (BucketException e) {
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
        } catch (BucketException | RepositoryException e) {
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
        } catch (BucketException | RepositoryException e) {
            System.out.println("Exception whilst getting parents");
        }
        return parents;
    }

    /**
     * Try and form families from the blocked data from SFNLNFFNFLNMFNDoMOverRole
     */
    private void formFamilies() {

        Iterator<IBucket<BirthFamilyGT>> iter = blocked_births_repo.getIterator(birthFactory);

        while (iter.hasNext()) {

            IBucket<BirthFamilyGT> bucket = iter.next();
            String name = bucket.getName();
            List<BirthFamilyGT> siblings = new ArrayList<>();

            try {
                for (BirthFamilyGT birth : bucket.getInputStream()) {
                    siblings.add(birth);
                }
            } catch (BucketException e) {
                ErrorHandling.exceptionError(e, "Exception whilst getting stream of Roles");
            }
            if (FFNFLNMFNMMNPOMDOM_repo.bucketExists(name)) {
                List<Marriage> parents = getParents(name);
            }
            // create_family(siblings);
        }
    }

    /**
     * Try and create a family unit from the blocked data_array
     *
     * @param parents_marriage - a collection of marriage certificates of the potential parents of the family from Marriage blocking
     * @param children         - a collection of Births from SFNLNFFNFLNMFNDoMOverBirth blocking
     */
    private void create_family(List<Marriage> parents_marriage, List<BirthFamilyGT> children) {

    }

    /**
     * Unifies the Roles together so that equivalent Roles are in a linked list structure
     * This method doesn't actually do this.
     * This method is the equivalent of a wreck of a great sea vessal.
     * TODO this is old code from the Role based system
     */
    private void unify() {

        System.out.println("Unifying");
        try {
            IInputStream<Relationship> relationship_stream = relationships.getInputStream();

            // Try and find other instances of Role's from relationships that are the same as this one

            for (Relationship r : relationship_stream) {

                // Relationship r = null;
                // try {
                //    r = (Relationship) l;
                // } catch ( ClassCastException e ) {
                //      System.out.println( "class class found: " +l );
                //     System.out.println( "class of l: " + l.getClass().toString() );
                // }
                //    System.out.println( "Relationship is: " + r );
                // System.out.println( "class of l: " + l.getClass().toString() );
                Role subject = r.getSubject();
                Role object = r.getObject();
                Relationship.relationship_kind relation = r.getRelationship();

                //    System.out.println( "processed " + subject.get_surname() );

            }
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot open role input stream: ");
        }

    }

    /**
     * This method populates the roles bucket
     * For each record in the Births bucket there will be 3 roles created - e.g. mother, FATHER baby
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromBirths(IBucket<BirthFamilyGT> bucket) {

        IOutputStream<Role> role_stream = roles.getOutputStream();
        IInputStream<BirthFamilyGT> stream = null;
        try {
            stream = bucket.getInputStream();
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Births bucket");
            return;
        }

        for (BirthFamilyGT birth_record : stream) {

            StoreReference<BirthFamilyGT> birth_record_ref = new StoreReference<>(input_repo, bucket, birth_record);

            Role child = null;
            Role father = null;
            Role mother = null;

            try {
                child = Role.createPersonFromOwnBirth(birth_record_ref, birthType.getId());
                role_stream.add(child);
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding birth record: " + child);
            }

            try {
                father = Role.createFatherFromChildsBirth(birth_record_ref, birthType.getId());
                if (father != null) {
                    role_stream.add(father);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding birth record: " + father);
            }

            try {
                mother = Role.createMotherFromChildsBirth(birth_record_ref, birthType.getId());
                if (mother != null) {
                    role_stream.add(mother);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding birth record: " + mother);
            }
            createRelationship(father, child, Relationship.relationship_kind.fatherof, "Shared certificate1");
            createRelationship(mother, child, Relationship.relationship_kind.motherof, "Shared certificate2");
        }
    }

    /**
     * This method populates the roles bucket
     * For each record in the Deaths bucket there will be 3 roles created - e.g. mother, FATHER baby
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromDeaths(IBucket bucket) {

        IOutputStream<Role> role_stream = roles.getOutputStream();
        IInputStream<Death> stream = null;
        try {
            stream = bucket.getInputStream();
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Deaths bucket");
            return;
        }

        for (Death death_record : stream) {

            StoreReference<Death> death_record_ref = new StoreReference<>(input_repo, bucket, death_record);

            Role child = null;
            Role father = null;
            Role mother = null;

            try {
                child = Role.createPersonFromOwnDeath(death_record_ref, deathType.getId());
                role_stream.add(child);
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding death record: " + child);
            }

            try {
                father = Role.createFatherFromChildsDeath(death_record_ref, deathType.getId());
                if (father != null) {
                    role_stream.add(father);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding FATHER from death record: " + father);
            }

            try {
                mother = Role.createMotherFromChildsDeath(death_record_ref, deathType.getId());
                if (mother != null) {
                    role_stream.add(mother);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding mother from death record: " + mother);
            }

            createRelationship(father, child, Relationship.relationship_kind.fatherof, "Shared certificate3");
            createRelationship(mother, child, Relationship.relationship_kind.motherof, "Shared certificate4");
        }
    }

    /**
     * This method populates the roles bucket
     * For each record in the Marriages bucket there will be 6 roles created - e.g. bride, groom plus the parents of each
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromMarriages(IBucket<Marriage> bucket) {

        IOutputStream<Role> roles_stream = roles.getOutputStream();

        IInputStream<Marriage> stream = null;
        try {
            stream = bucket.getInputStream();
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Marriages bucket");
            return;
        }

        int count = 0;

        for (Marriage marriage_record : stream) {

            count++;

            StoreReference<Marriage> marriage_record_ref = new StoreReference<>(store, input_repo.getName(), bucket.getName(), marriage_record.getId());

            Role bride = null;
            Role groom = null;
            Role bf = null;
            Role bm = null;
            Role gf = null;
            Role gm = null;

            try {
                bride = Role.createBrideFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if (bride != null) {
                    roles_stream.add(bride);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding bride: " + bride);
            }

            try {
                groom = Role.createGroomFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if (groom != null) {
                    roles_stream.add(groom);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding groom: " + groom);
            }

            try {
                gm = Role.createGroomsMotherFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if (gm != null) {
                    roles_stream.add(gm);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding groom's mother: " + gm);
            }

            try {
                gf = Role.createGroomsFatherFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if (gf != null) {
                    roles_stream.add(gf);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding groom's FATHER: " + gf);
            }

            try {
                bm = Role.createBridesMotherFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if (bm != null) {
                    roles_stream.add(bm);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding bride's mother: " + bm);
            }
            try {
                bf = Role.createBridesFatherFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if (bf != null) {
                    roles_stream.add(bf);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding bride's FATHER: " + bf);
            }
            createRelationship(bf, bride, Relationship.relationship_kind.fatherof, "Shared certificate5");
            createRelationship(bm, bride, Relationship.relationship_kind.motherof, "Shared certificate6");
            createRelationship(gf, groom, Relationship.relationship_kind.fatherof, "Shared certificate7");
            createRelationship(gm, groom, Relationship.relationship_kind.motherof, "Shared certificate8");
        }

        System.out.println("Processed : " + count + " marriage records");
    }

    /**
     * Create a relationship between the parties and add to the relationship table.
     *
     * @param subject      - the subject
     * @param object       - the object
     * @param relationship - relationship between subject and object
     * @param evidence     - of the relationship
     */
    private void createRelationship(Role subject, Role object, Relationship.relationship_kind relationship, String evidence) {

        if (subject == null || object == null) {
            return;
        }
        StoreReference<Role> subject_ref = new StoreReference<>(store, role_repo.getName(), roles.getName(), subject.getId());
        StoreReference<Role> object_ref = new StoreReference<>(store, role_repo.getName(), roles.getName(), object.getId());

        Relationship r = null;
        try {
            r = new Relationship(subject_ref, object_ref, relationship, evidence);
            relationships.makePersistent(r);
        } catch (StoreException e) {
            ErrorHandling.exceptionError(e, "Store Error adding relationship: " + r);
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Bucket Error adding relationship: " + r);
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        String births_source_path = "/Digitising Scotland/KilmarnockBDM/births.csv";
        String deaths_source_path = "/Digitising Scotland/KilmarnockBDM/deaths.csv";
        String marriages_source_path = "/Digitising Scotland/KilmarnockBDM/marriages.csv";

        new KilmarnockLinker(births_source_path, deaths_source_path, marriages_source_path);
    }
}
