package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.BirthFamilyGT;
import uk.ac.standrews.cs.digitising_scotland.util.Metrics;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;

import java.io.IOException;
import java.util.*;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from KilmarnockLinker.
 * Created by al on 17/2/1017
 */
public abstract class KilmarnockMTreeMatcherGroundTruthChecker extends KilmarnockExperiment {

    protected Map<Long, Family> families = new HashMap<>(); // Maps from person id to family.

    protected KilmarnockMTreeMatcherGroundTruthChecker() throws StoreException, IOException, RepositoryException {
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

        List<SimpleTuple3<Long, Integer, String>> birthIDs = loadFamilies();

        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

        // For each assigned family, how many members are there in the family
        Map<Integer, Integer> assignedFamilyCounts = new HashMap<>();

        // For how many individuals do we fail to assign a family id
        int assignedFamilyMissing = 0;

        // Same for each real (=demographer) family.
        Map<String, Integer> realFamilyCounts = new HashMap<>();

        // For how many individuals did the demographers fail to assign a family id
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

                    if (b1 != b2) {

                        boolean real_families_same = b1RealFamilyId.length() > 0 && b1RealFamilyId.equals(b2.third);
                        boolean assigned_families_same = b1AssignedFamily != null && b1AssignedFamily.equals(b2.second);

                        if (assigned_families_same) {
                            if (real_families_same) {
                                truePositives++;
                            } else {
                                falsePositives++;
                            }
                        } else {
                            if (real_families_same) {
                                falseNegatives++;
                            }
                        }
                    }
                }
            }
        }

        printLinkageStats(truePositives, falsePositives, falseNegatives, assignedFamilyMissing, realFamilyMissing, assignedFamilyCounts.values(), realFamilyCounts.values());
    }

    private void printLinkageStats(int truePositives, int falsePositives, int falseNegatives, int assignedFamilyMissing, int realFamilyMissing, Collection<Integer> assignedFamilySizes, Collection<Integer> realFamilySizes) {

        System.out.println("Assigned family stats");
        printFamilyStats(assignedFamilySizes, assignedFamilyMissing);

        System.out.println("Real family stats");
        printFamilyStats(realFamilySizes, realFamilyMissing);

        System.out.println("True Positives  : " + truePositives);
        System.out.println("False Positives : " + falsePositives);
        System.out.println("False Negatives : " + falseNegatives);

        if ((truePositives + falsePositives) == 0 || (truePositives + falseNegatives) == 0) {
            System.out.println("Cannot calculate precision and recall.");

        } else {

            System.out.println(String.format("Precision       : %.2f", Metrics.precision(truePositives, falsePositives)));
            System.out.println(String.format("Recall          : %.2f", Metrics.recall(truePositives, falseNegatives)));
            System.out.println(String.format("F1 Measure      : %.2f", Metrics.F1(truePositives, falsePositives, falseNegatives)));
        }
    }

    private List<SimpleTuple3<Long, Integer, String>> loadFamilies() throws BucketException {

        List<SimpleTuple3<Long, Integer, String>> birthIDs = new ArrayList<>();

        for (BirthFamilyGT birthRecord : births.getInputStream()) {

            Family assignedFamily = families.get(birthRecord.getId());
            Integer assignedFamilyId = null;
            if (assignedFamily != null) {
                assignedFamilyId = assignedFamily.id;
            }
            birthIDs.add(new SimpleTuple3<>(birthRecord.getId(), assignedFamilyId, birthRecord.getString(BirthFamilyGT.FAMILY)));
        }
        return birthIDs;
    }

    private void printFamilyStats(Collection<Integer> family_sizes, int missing) {

        try {
            System.out.println("Number of families                : " + family_sizes.size());
            System.out.println("Max-size of families              : " + Collections.max(family_sizes));
            System.out.println("Min-size of families              : " + Collections.min(family_sizes));
            System.out.println("Mean-size of families             : " + calcMean(family_sizes));
            System.out.println("Individuals with missing families : " + missing);
            System.out.println();

        } catch (Exception e) {
            System.out.println("No families");
        }
    }

    private double calcMean(Collection<Integer> values) {

        int sum = 0;
        for (int i : values) {
            sum += i;
        }
        return ((double) sum / values.size());
    }

    static class SimpleTuple3<X, Y, Z> {

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
