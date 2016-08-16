package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.FNLNOverRole;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.jstore.impl.StoreFactory;
import uk.ac.standrews.cs.jstore.impl.StoreReference;
import uk.ac.standrews.cs.jstore.impl.TypeFactory;
import uk.ac.standrews.cs.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.jstore.interfaces.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Attempt to create a linking framework
 * Created by al on 6/8/2014.
 */
public class AlLinker {


    // Repositories and stores

    private static String input_repo_name = "BDM_repo";                         // input repository containing event records
    private static String role_repo_name = "role_repo";                         // repository for Role records
    private static String blocked_role_repo_name = "blocked_role_repo";         // repository for blocked Role records

    private IStore store;
    private IRepository input_repo;             // Repository containing buckets of BDM records
    private IRepository role_repo;
    private IRepository blocked_role_repo;

    // Bucket declarations

    private IBucket<Birth> births;                     // Bucket containing birth records (inputs).
    private IBucket<Marriage> marriages;               // Bucket containing marriage records (inputs).
    private IBucket<Death> deaths;                     // Bucket containing death records (inputs).

    private IBucket<Role> roles;                      // Bucket containing roles extracted from BDM records

    // Paths to sources

    private static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    private static String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
    private static String deaths_name = "death_records";                            // Name of bucket containing marriage records (inputs).

    // Names of buckets

    private static String role_name = "roles";                                   // Name of bucket containing roles extracted from BDM records

    private IReferenceType birthType;
    private IReferenceType deathType;
    private IReferenceType marriageType;
    private IReferenceType roleType;

    private BirthFactory birthFactory;
    private DeathFactory deathFactory;
    private MarriageFactory marriageFactory;
    private RoleFactory roleFactory;
    private ArrayList<Long> oids = new ArrayList<>();


    public AlLinker(String births_source_path, String deaths_source_path, String marriages_source_path) throws BucketException {

        System.out.println("Initialising");
        try {
            initialise();
        } catch (StoreException | JSONException | RecordFormatException | RepositoryException | IOException e) {
            ErrorHandling.exceptionError( e, "Error initialising system");
            return;
        }
        System.out.println("Injesting");
        injestBDMRecords(births_source_path, deaths_source_path, marriages_source_path);

        System.out.println("Blocking");
        block();

    }

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {

        Path store_path = Files.createTempDirectory(null);

        StoreFactory.setStorePath(store_path);
        store = StoreFactory.makeStore();

        System.out.println( "Store path = " + store_path );

        input_repo = store.makeRepository(input_repo_name);
        role_repo = store.makeRepository(role_repo_name);
        blocked_role_repo = store.makeRepository(blocked_role_repo_name);  // a repo of Role Buckets of records blocked by  first name, last name
        initialiseTypes();
        initialiseFactories();

        births = input_repo.makeBucket(births_name, BucketKind.DIRECTORYBACKED, birthFactory);
        deaths = input_repo.makeBucket(deaths_name, BucketKind.DIRECTORYBACKED, deathFactory);
        marriages = input_repo.makeBucket(marriages_name, BucketKind.DIRECTORYBACKED, marriageFactory);
        roles = role_repo.makeBucket(role_name, BucketKind.DIRECTORYBACKED, roleFactory);
    }

    private void initialiseTypes() {

        TypeFactory tf = TypeFactory.getInstance();

        birthType = tf.createType(Birth.class, "birth");
        deathType = tf.createType(Death.class, "death");
        marriageType = tf.createType(Marriage.class, "marriage");
        roleType = tf.createType(Role.class, "role");
    }

    private void initialiseFactories() {
        birthFactory = new BirthFactory(birthType.getId());
        deathFactory = new DeathFactory(deathType.getId());
        marriageFactory = new MarriageFactory(marriageType.getId());
        roleFactory = new RoleFactory(roleType.getId());
    }

    /**
     * Import the birth,death, marriage records
     * Initialises the roles bucket with the roles injected - one record for each person referenced in the original record
     * Initialises the known(100% certain) relationships between roles and stores the relationships in the relationships bucket
     * @param births_source_path
     * @param deaths_source_path
     * @param marriages_source_path
     */
    private void injestBDMRecords(String births_source_path, String deaths_source_path, String marriages_source_path) {

        try {
            System.out.println("Importing BDM records");
            int count = 0;
            count = EventImporter.importDigitisingScotlandBirths(births, births_source_path, birthType);
            System.out.println("Imported " + count + " birth records");
            count = EventImporter.importDigitisingScotlandDeaths(deaths, deaths_source_path, deathType);
            System.out.println("Imported " + count + " death records");
            count = EventImporter.importDigitisingScotlandMarriages(marriages, marriages_source_path, marriageType, oids );
            System.out.println("Imported " + count + " marriage records");
        } catch (RecordFormatException | IOException | BucketException e) {
            ErrorHandling.exceptionError(e, "Error whilst injecting records");
        }

        System.out.println("Creating roles from birth records");
        createRolesFromBirths(births);
        System.out.println("Creating roles from death records");
        createRolesFromDeaths(deaths);
        System.out.println("Creating roles from marriage records");
        createRolesFromMarriages(marriages);
    }


    private void block() {
        try {
            IBlocker blocker = new FNLNOverRole(roles, blocked_role_repo, roleFactory);
            blocker.apply();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (BucketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method populates the roles bucket
     * For each record in the Births bucket there will be 3 roles created - e.g. mother, father baby
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromBirths(IBucket bucket) {

        IOutputStream<Role> people_stream = roles.getOutputStream();
        IInputStream<Birth> stream = null;
        try {
            stream = bucket.getInputStream();
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Births bucket");
            return;
        }

        for (Birth birth_record : stream) {

            StoreReference<Birth> birth_record_ref = new StoreReference<Birth>(input_repo, bucket, birth_record);

            Role role = null;
            try {
                role = Role.createPersonFromOwnBirth(birth_record_ref, birthType.getId());
                people_stream.add(role);
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding birth record: " + role );
            }

            try {
                role = Role.createFatherFromChildsBirth(birth_record_ref, birthType.getId());
                if (role != null) {
                    people_stream.add(role);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding birth record: " + role );
            }

            try {
                role = Role.createMotherFromChildsBirth(birth_record_ref, birthType.getId());
                if (role != null) {
                    people_stream.add(role);
                }
            } catch (StoreException | BucketException e) {
                    ErrorHandling.exceptionError(e, "Error adding birth record: " + role );
            }
        }
    }

    /**
     * This method populates the roles bucket
     * For each record in the Deaths bucket there will be 3 roles created - e.g. mother, father baby
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromDeaths(IBucket bucket)  {

        IOutputStream<Role> people_stream = roles.getOutputStream();
        IInputStream<Death> stream = null;
        try {
            stream = bucket.getInputStream();
        } catch (BucketException e) {
            ErrorHandling.exceptionError(e, "Cannot get stream for Deaths bucket");
            return;
        }

        for (Death death_record : stream) {

            StoreReference<Death> death_record_ref = new StoreReference<Death>(input_repo, bucket, death_record);

            Role role = null;
            try {
                role = Role.createPersonFromOwnDeath(death_record_ref, deathType.getId());
                people_stream.add(role);
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding death record: " + role );
            }

            try {
                role = Role.createFatherFromChildsDeath(death_record_ref, deathType.getId());
                if (role != null) {
                    people_stream.add(role);
                }
            }
            catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding father from death record: " + role);
            }

            try {
                role = Role.createMotherFromChildsDeath(death_record_ref, deathType.getId());
                if (role != null) {
                    people_stream.add(role);
                }
            }
            catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding mother from death record: " + role);
            }
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

//            if( oids.contains(marriage_record.getId() )) {
//                System.out.println( "Found oid in oids list for oid: " + marriage_record.getId() + ":" + marriage_record);
//
//            } else {
//                System.out.println( "Did not find oid in oids list for oid: " + marriage_record.getId() + ":" + marriage_record);
//            }

//            System.out.println( "Got marriage record from stream with oid: " + marriage_record.getId() );
            StoreReference<Marriage> marriage_record_ref = new StoreReference<Marriage>(input_repo.getName(), bucket.getName(), marriage_record.getId());
//            System.out.println( "Created store reference to record: " + marriage_record_ref );

            Role role = null;
            try {
                role = Role.createBrideFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if( role != null ) {
                    roles_stream.add(role);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding bride: " + role);
            }

            try {
                role = Role.createGroomFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if( role != null ) {
                    roles_stream.add(role);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding groom: " + role);
            }

            try {
                role = Role.createGroomsMotherFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if( role != null ) {
                    roles_stream.add(role);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding groom's mother: " + role);
            }

            try {
                role = Role.createGroomsFatherFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if( role != null ) {
                    roles_stream.add(role);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding groom's father: " + role);
            }

            try {
                role = Role.createBridesMotherFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if( role != null ) {
                    roles_stream.add(role);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding bride's mother: " + role);
            }
            try {
                role = Role.createBridesFatherFromMarriageRecord(marriage_record_ref, marriageType.getId());
                if( role != null ) {
                    roles_stream.add(role);
                }
            } catch (StoreException | BucketException e) {
                ErrorHandling.exceptionError(e, "Error adding bride's father: " + role);
            }

        }

        System.out.println( "Processed : " + count + " marriage records" );
    }

    /**
     * **************************************************************************************************************
     */

    public static void main(String[] args) throws Exception, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException {

        String births_source_path =    "/Users/al/Documents/intelliJ/BDMSet1/birth_records.txt";
        String deaths_source_path =    "/Users/al/Documents/intelliJ/BDMSet1/death_records.txt";
        String marriages_source_path = "/Users/al/Documents/intelliJ/BDMSet1/marriage_records.txt";

        new AlLinker(births_source_path, deaths_source_path, marriages_source_path);
    }

}
