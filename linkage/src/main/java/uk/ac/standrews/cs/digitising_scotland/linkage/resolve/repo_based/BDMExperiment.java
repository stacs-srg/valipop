package uk.ac.standrews.cs.digitising_scotland.linkage.resolve.repo_based;

import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.DeathFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.MarriageFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.injesters.InitialiseBDMRepo;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.resolve.Family;
import uk.ac.standrews.cs.digitising_scotland.util.Metrics;
import uk.ac.standrews.cs.digitising_scotland.util.TimeManipulation;
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
import java.util.*;
import java.util.concurrent.Callable;

public abstract class BDMExperiment {

    // Bucket declarations

    protected IBucket<BirthFamilyGT> births;                                // Bucket containing birth records (inputs).
    protected IBucket<Marriage> marriages;                                  // Bucket containing marriage records (inputs).
    protected IBucket<Death> deaths;                                        // Bucket containing death records (inputs).

    protected Map<Long, Family> person_to_family_map = new HashMap<>();     // Maps from person id to family.

    public BDMExperiment( String store_path, String repo_name ) throws StoreException, IOException, RepositoryException {

        Path p = Paths.get( store_path );
        if( ! p.toFile().isDirectory() ) {
            throw new RepositoryException( "Illegal store root specified");
        }
        StoreFactory.setStorePath( p );
        IStore store = StoreFactory.getStore();

        IRepository input_repo = store.getRepo(repo_name);

        TypeFactory type_factory = TypeFactory.getInstance();

        IReferenceType birthType = type_factory.typeWithName(InitialiseBDMRepo.BIRTH_TYPE_NAME);
        IReferenceType deathType = type_factory.typeWithName(InitialiseBDMRepo.DEATH_TYPE_NAME);
        IReferenceType marriageType = type_factory.typeWithName(InitialiseBDMRepo.MARRIAGE_TYPE_NAME);

        births = input_repo.getBucket(InitialiseBDMRepo.BIRTHS_BUCKET_NAME, new BirthFactory(birthType.getId()));
        deaths = input_repo.getBucket(InitialiseBDMRepo.DEATHS_BUCKET_NAME, new DeathFactory(deathType.getId()));
        marriages = input_repo.getBucket(InitialiseBDMRepo.MARRIAGES_BUCKET_NAME, new MarriageFactory(marriageType.getId()));
    }

    public void printFamilies() throws BucketException {

        printFamilies(births, person_to_family_map);
    }

    public void printLinkageStats() throws BucketException {

        printLinkageStats(printLinkageStats(loadFamilyLinkResults(births, person_to_family_map)));
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

        throw new UnsupportedOperationException();
    }

    public int getMarriagesCount() {

        throw new UnsupportedOperationException();
    }

    public int getDeathsCount() {

        throw new UnsupportedOperationException();
    }

    protected Set<BirthFamilyGT> loadBirths() throws BucketException {

        return loadBirths(Integer.MAX_VALUE);
    }

    protected Set<BirthFamilyGT> loadBirths(int number_of_births_to_process) throws BucketException {

        Set<BirthFamilyGT> birth_set = new HashSet<>();

        int count = 0;

        for (BirthFamilyGT birth_record : births.getInputStream()) {
            birth_set.add(birth_record);
            if (++count >= number_of_births_to_process) break;
        }

        return birth_set;
    }

    public void timedRun(String description, Callable<Void> function) throws Exception {

        System.out.println();
        System.out.println(description);
        long time = System.currentTimeMillis();
        function.call();
        long elapsed = System.currentTimeMillis() - time;
        System.out.println(description + " - took " + TimeManipulation.formatMillis(elapsed) + " (h:m:s)");
    }

    /**
     * Display the  families in CSV format
     * All generated family tags are empty for unmatched families
     * Tests that families do not appear in map more than once, which can occur in some experiments.
     */
    private static void printFamilies(IBucket<BirthFamilyGT> births, Map<Long, Family> person_to_family_map) throws BucketException {

        System.out.println("Generated family id\tDemographer family id\tRecord id\tForname\tSurname\tDOB\tPOM\tDOM\tFather's forename\tFather's surname\tMother's forename\tMother's maidenname");

        for (BirthFamilyGT birth_record : births.getInputStream()) {

            Family family = person_to_family_map.get(birth_record.getId());

            System.out.println(( family != null ? family.id : "" ) + "\t" + birth_record.getString(BirthFamilyGT.FAMILY) + "\t" + birth_record.getString(BirthFamilyGT.ORIGINAL_ID) + "\t" + birth_record.getString(BirthFamilyGT.FORENAME) + "\t" + birth_record.getString(BirthFamilyGT.SURNAME) + "\t" + birth_record.getDOB() + "\t" + birth_record.getPlaceOfMarriage() + "\t" + birth_record.getDateOfMarriage() + "\t" + birth_record.getFathersForename() + "\t" + birth_record.getFathersSurname() + "\t" + birth_record.getMothersForename() + "\t" + birth_record.getMothersMaidenSurname());
        }
    }

    private static List<FamilyLinkResult> loadFamilyLinkResults(IBucket<BirthFamilyGT> births, Map<Long, Family> person_to_family_map) throws BucketException {

        List<FamilyLinkResult> results = new ArrayList<>();

        for (BirthFamilyGT birth_record : births.getInputStream()) {

            final long birth_record_id = birth_record.getId();
            final String assigned_family_id = person_to_family_map.containsKey(birth_record_id) ? String.valueOf(person_to_family_map.get(birth_record_id).id) : null;

            results.add(new FamilyLinkResult(String.valueOf(birth_record_id), assigned_family_id, birth_record.getString(BirthFamilyGT.FAMILY)));
        }
        return results;
    }

    private static LinkageStats printLinkageStats(List<FamilyLinkResult> family_link_results) throws BucketException {

        // Record the number of members in each assigned family.
        Map<String, Integer> assigned_family_member_counts = new HashMap<>();

        // Record the number of members in each real family (i.e. determined by demographers).
        Map<String, Integer> real_family_member_counts = new HashMap<>();

        // Count the individuals for which we failed to assign a family id.
        int number_of_people_missing_from_assigned_families = 0;

        // Count the individuals for which the demographers failed to assign a family id.
        int number_of_people_missing_from_real_families = 0;

        LinkageCounts linkage_counts = new LinkageCounts();

        for (FamilyLinkResult result1 : family_link_results) {

            if (present(result1.assigned_family_id)) {
                incrementFamilyCount(assigned_family_member_counts, result1.assigned_family_id);

            } else {
                number_of_people_missing_from_assigned_families++;
            }

            if (present(result1.real_family_id)) {
                incrementFamilyCount(real_family_member_counts, result1.real_family_id);

            } else {
                number_of_people_missing_from_real_families++;
            }

            for (FamilyLinkResult result2 : family_link_results) {
                if (result1 != result2) {
                    updateLinkageCounts(linkage_counts, result1, result2);
                }
            }
        }

        return new LinkageStats(linkage_counts.true_positives, linkage_counts.false_positives, linkage_counts.false_negatives, number_of_people_missing_from_assigned_families, number_of_people_missing_from_real_families, assigned_family_member_counts.values(), real_family_member_counts.values());
    }

    private static void printLinkageStats(LinkageStats linkage_stats) {

        System.out.println("Assigned family stats");
        printFamilyStats(linkage_stats.getSizesOfAssignedFamilies(), linkage_stats.getNumberOfPeopleMissingFromAssignedFamilies());

        System.out.println("Real family stats");
        printFamilyStats(linkage_stats.getSizesOfRealFamilies(), linkage_stats.getNumberOfPeopleMissingFromRealFamilies());

        System.out.println("True Positives  : " + linkage_stats.getTruePositives());
        System.out.println("False Positives : " + linkage_stats.getFalsePositives());
        System.out.println("False Negatives : " + linkage_stats.getFalseNegatives());

        if ((linkage_stats.getTruePositives() + linkage_stats.getFalsePositives()) == 0 || (linkage_stats.getTruePositives() + linkage_stats.getFalseNegatives()) == 0) {
            System.out.println("Cannot calculate precision and recall.");

        } else {

            System.out.println(String.format("Precision       : %.2f", Metrics.precision(linkage_stats.getTruePositives(), linkage_stats.getFalsePositives())));
            System.out.println(String.format("Recall          : %.2f", Metrics.recall(linkage_stats.getTruePositives(), linkage_stats.getFalseNegatives())));
            System.out.println(String.format("F1 Measure      : %.2f", Metrics.F1(linkage_stats.getTruePositives(), linkage_stats.getFalsePositives(), linkage_stats.getFalseNegatives())));
        }
    }

    private static void updateLinkageCounts(LinkageCounts linkage_counts, FamilyLinkResult result1, FamilyLinkResult result2) {

        boolean real_families_same = present(result1.real_family_id)
                                            && present(result2.real_family_id)
                                            && result1.real_family_id.equals(result2.real_family_id);
        boolean assigned_families_same = present(result1.assigned_family_id)
                                            && present(result2.assigned_family_id)
                                            && result1.assigned_family_id.equals(result2.assigned_family_id);

        if (assigned_families_same) {

            if (real_families_same) {
                linkage_counts.true_positives++;

            } else {
                linkage_counts.false_positives++;
            }
        } else {

            if (real_families_same) {
                linkage_counts.false_negatives++;
            }
        }
    }

    private static boolean present(String id) {

        return id != null && id.length() > 0;
    }

    private static void incrementFamilyCount(Map<String, Integer> family_member_counts, String id) {

        if (family_member_counts.containsKey(id)) {
            family_member_counts.put(id, family_member_counts.get(id) + 1);

        } else {
            family_member_counts.put(id, 1);
        }
    }

    private static void printFamilyStats(Collection<Integer> family_sizes, int missing) {

        try {
            System.out.println(String.format("Number of families                : %d", family_sizes.size()));
            System.out.println(String.format("Max-size of families              : %d", Collections.max(family_sizes)));
            System.out.println(String.format("Min-size of families              : %d", Collections.min(family_sizes)));
            System.out.println(String.format("Mean-size of families             : %.1f", mean(family_sizes)));
            System.out.println(String.format("Individuals with missing families : %d", missing));
            System.out.println();

        } catch (Exception e) {
            System.out.println("No families");
        }
    }

    private static double mean(Collection<Integer> values) {

        int sum = 0;
        for (int i : values) {
            sum += i;
        }
        return ((double) sum / values.size());
    }

    private static class LinkageStats {

        private final int true_positives;
        private final int false_positives;
        private final int false_negatives;
        private final int number_of_people_missing_from_assigned_families;
        private final int number_of_people_missing_from_real_families;

        private final Collection<Integer> sizes_of_assigned_families;
        private final Collection<Integer> sizes_of_real_families;

        private LinkageStats(int true_positives, int false_positives, int false_negatives, int number_of_people_missing_from_assigned_families, int number_of_people_missing_from_real_families, Collection<Integer> sizes_of_assigned_families, Collection<Integer> sizes_of_real_families) {

            this.true_positives = true_positives;
            this.false_positives = false_positives;
            this.false_negatives = false_negatives;
            this.number_of_people_missing_from_assigned_families = number_of_people_missing_from_assigned_families;
            this.number_of_people_missing_from_real_families = number_of_people_missing_from_real_families;
            this.sizes_of_assigned_families = sizes_of_assigned_families;
            this.sizes_of_real_families = sizes_of_real_families;
        }

        int getTruePositives() {
            return true_positives;
        }

        int getFalsePositives() {
            return false_positives;
        }

        int getFalseNegatives() {
            return false_negatives;
        }

        int getNumberOfPeopleMissingFromAssignedFamilies() {
            return number_of_people_missing_from_assigned_families;
        }

        int getNumberOfPeopleMissingFromRealFamilies() {
            return number_of_people_missing_from_real_families;
        }

        Collection<Integer> getSizesOfAssignedFamilies() {
            return sizes_of_assigned_families;
        }

        Collection<Integer> getSizesOfRealFamilies() {
            return sizes_of_real_families;
        }
    }

    private static class LinkageCounts {

        int true_positives;
        int false_positives;
        int false_negatives;
    }

    private static class FamilyLinkResult {

        final String birth_record_id;
        final String assigned_family_id;
        final String real_family_id;

        FamilyLinkResult(String birth_record_id, String assigned_family_id, String real_family_id) {

            this.birth_record_id = birth_record_id;
            this.assigned_family_id = assigned_family_id;
            this.real_family_id = real_family_id;
        }
    }
}
