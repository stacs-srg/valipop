/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.statistics.populationStatistics;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.distributions.EnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.*;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.EventRateTables;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.*;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrecting2DIntegerRangeProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.utils.InputFileReader;
import uk.ac.standrews.cs.valipop.utils.InvalidInputFileException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

/**
 * The PopulationStatistics holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatistics implements EventRateTables {

    private TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> maleDeath;
    private TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> femaleDeath;
    private TreeMap<Year, SelfCorrecting2DIntegerRangeProportionalDistribution> partnering;
    private TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> orderedBirth;
    private TreeMap<Year, SelfCorrectingProportionalDistribution> multipleBirth;
    private TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth;
    private TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> marriage;
    private TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> separation;

    private TreeMap<Year, Double> sexRatioBirth;

    private TreeMap<Year, ValiPopEnumeratedDistribution> maleForenames;
    private TreeMap<Year, ValiPopEnumeratedDistribution> femaleForenames;
    private TreeMap<Year, ValiPopEnumeratedDistribution> surnames;

    private TreeMap<Year, ValiPopEnumeratedDistribution> migrantMaleForenames;
    private TreeMap<Year, ValiPopEnumeratedDistribution> migrantFemaleForenames;
    private TreeMap<Year, ValiPopEnumeratedDistribution> migrantSurnames;
    private TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> migrationRate;

    private TreeMap<Year, AgeDependantEnumeratedDistribution> maleDeathCauses;
    private TreeMap<Year, AgeDependantEnumeratedDistribution> femaleDeathCauses;

    private Period minGestationPeriod;
    private Period minBirthSpacing;
    private RandomGenerator randomGenerator;

    private static Logger log = Logger.getLogger(PopulationStatistics.class.getName());

    private TreeMap<Year, AgeDependantEnumeratedDistribution> maleOccupation;
    private TreeMap<Year, AgeDependantEnumeratedDistribution> femaleOccupation;

    private TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> maleOccupationChange;
    private TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> femaleOccupationChange;

    public PopulationStatistics(Config config) {

        try {
            randomGenerator = new JDKRandomGenerator();

            if (!config.deterministic()) {
                // sets a seed based on time so that such can be logged for recreation of simulation
                config.setSeed((int) System.nanoTime());
            }

            randomGenerator.setSeed(config.getSeed());

            TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> maleDeath = readInSC1DDataFiles(config.getVarMaleLifetablePaths(), config);
            TreeMap<Year, AgeDependantEnumeratedDistribution> maleDeathCauses = readInAgeDependantEnumeratedDistributionDataFiles(config.getVarMaleDeathCausesPaths(), config);
            TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> femaleDeath = readInSC1DDataFiles(config.getVarFemaleLifetablePaths(), config);
            TreeMap<Year, AgeDependantEnumeratedDistribution> femaleDeathCauses = readInAgeDependantEnumeratedDistributionDataFiles(config.getVarFemaleDeathCausesPaths(), config);
            TreeMap<Year, SelfCorrecting2DIntegerRangeProportionalDistribution> partnering = readInAgeAndProportionalStatsInputFiles(config.getVarPartneringPaths(), config);
            TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> orderedBirth = readInSC2DDataFiles(config.getVarOrderedBirthPaths(), config);
            TreeMap<Year, SelfCorrectingProportionalDistribution> multipleBirth = readInAndAdaptAgeAndProportionalStatsInputFiles(config.getVarMultipleBirthPaths(), config);
            TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth = readInSC1DDataFiles(config.getVarIllegitimateBirthPaths(), config);
            TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> marriage = readInSC1DDataFiles(config.getVarMarriagePaths(), config);
            TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> separation = readInSC2DDataFiles(config.getVarSeparationPaths(), config);
            TreeMap<Year, Double> sexRatioBirth = readInSingleInputDataFile(config.getVarBirthRatioPath());
            TreeMap<Year, ValiPopEnumeratedDistribution> maleForename = readInNamesDataFiles(config.getVarMaleForenamePath(), config);
            TreeMap<Year, ValiPopEnumeratedDistribution> femaleForename = readInNamesDataFiles(config.getVarFemaleForenamePath(), config);
            TreeMap<Year, ValiPopEnumeratedDistribution> surname = readInNamesDataFiles(config.getVarSurnamePath(), config);
            TreeMap<Year, ValiPopEnumeratedDistribution> migrantMaleForename = readInNamesDataFiles(config.getVarMigrantMaleForenamePath(), config);
            TreeMap<Year, ValiPopEnumeratedDistribution> migrantFemaleForename = readInNamesDataFiles(config.getVarMigrantFemaleForenamePath(), config);
            TreeMap<Year, ValiPopEnumeratedDistribution> migrantSurname = readInNamesDataFiles(config.getVarMigrantSurnamePath(), config);
            TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> migrationRate = readInSC1DDataFiles(config.getVarMigrationRatePath(), config);

            TreeMap<Year, AgeDependantEnumeratedDistribution> maleOccupation = readInAgeDependantEnumeratedDistributionDataFiles(config.getVarMaleOccupationPaths(), config);
            TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> maleOccupationChange = readInStringAndProportionalStatsInputFiles(config.getVarMaleOccupationChangePaths(), config);

            TreeMap<Year, AgeDependantEnumeratedDistribution> femaleOccupation = readInAgeDependantEnumeratedDistributionDataFiles(config.getVarFemaleOccupationPaths(), config);
            TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> femaleOccupationChange = readInStringAndProportionalStatsInputFiles(config.getVarFemaleOccupationChangePaths(), config);

            init(maleDeath, maleDeathCauses, femaleDeath, femaleDeathCauses, partnering, orderedBirth, multipleBirth, illegitimateBirth,
                    marriage, separation, sexRatioBirth, maleForename, femaleForename, surname,
                    migrantMaleForename, migrantFemaleForename, migrantSurname, migrationRate,
                    maleOccupation, femaleOccupation, maleOccupationChange, femaleOccupationChange,
                    config.getMinBirthSpacing(), config.getMinGestationPeriod());

        } catch (IOException | InvalidInputFileException | InconsistentWeightException e) {
            throw new RuntimeException(e);
        }
    }

    public PopulationStatistics(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public PopulationStatistics(TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> maleDeath,
                                TreeMap<Year, AgeDependantEnumeratedDistribution> maleDeathCauses,
                                TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> femaleDeath,
                                TreeMap<Year, AgeDependantEnumeratedDistribution> femaleDeathCauses,
                                TreeMap<Year, SelfCorrecting2DIntegerRangeProportionalDistribution> partnering,
                                TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                                TreeMap<Year, SelfCorrectingProportionalDistribution> multipleBirth,
                                TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth,
                                TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> marriage,
                                TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> separation,
                                TreeMap<Year, Double> sexRatioBirths,
                                TreeMap<Year, ValiPopEnumeratedDistribution> maleForenames,
                                TreeMap<Year, ValiPopEnumeratedDistribution> femaleForenames,
                                TreeMap<Year, ValiPopEnumeratedDistribution> surnames,
                                TreeMap<Year, ValiPopEnumeratedDistribution> migrantMaleForename,
                                TreeMap<Year, ValiPopEnumeratedDistribution> migrantFemaleForename,
                                TreeMap<Year, ValiPopEnumeratedDistribution> migrantSurname,
                                TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> migrationRate,
                                TreeMap<Year, AgeDependantEnumeratedDistribution> maleOccupation,
                                TreeMap<Year, AgeDependantEnumeratedDistribution> femaleOccupation,
                                TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> maleOccupationChange,
                                TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> femaleOccupationChange,
                                Period minBirthSpacing,
                                Period minGestationPeriod,
                                RandomGenerator randomGenerator) {

        this.randomGenerator = randomGenerator;
        init(maleDeath, maleDeathCauses, femaleDeath, femaleDeathCauses, partnering, orderedBirth, multipleBirth,
                illegitimateBirth, marriage, separation, sexRatioBirths, maleForenames, femaleForenames, surnames,
                migrantMaleForename, migrantFemaleForename, migrantSurname, migrationRate, maleOccupation, femaleOccupation,
                maleOccupationChange, femaleOccupationChange, minBirthSpacing, minGestationPeriod);
    }

    private void init(TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> maleDeath, TreeMap<Year, AgeDependantEnumeratedDistribution> maleDeathCauses, TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> femaleDeath,
                      TreeMap<Year, AgeDependantEnumeratedDistribution> femaleDeathCauses, TreeMap<Year, SelfCorrecting2DIntegerRangeProportionalDistribution> partnering, TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                      TreeMap<Year, SelfCorrectingProportionalDistribution> multipleBirth, TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth, TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> marriage,
                      TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> separation, TreeMap<Year, Double> sexRatioBirths, TreeMap<Year, ValiPopEnumeratedDistribution> maleForename, TreeMap<Year, ValiPopEnumeratedDistribution> femaleForename,
                      TreeMap<Year, ValiPopEnumeratedDistribution> surname, TreeMap<Year, ValiPopEnumeratedDistribution> migrantMaleForenames,
                      TreeMap<Year, ValiPopEnumeratedDistribution> migrantFemaleForenames, TreeMap<Year, ValiPopEnumeratedDistribution> migrantSurname, TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> migrationRate,
                      TreeMap<Year, AgeDependantEnumeratedDistribution> maleOccupation, TreeMap<Year, AgeDependantEnumeratedDistribution> femaleOccupation,
                      TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> maleOccupationChange, TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> femaleOccupationChange,
                      Period minBirthSpacing, Period minGestationPeriod) {

        this.maleDeath = maleDeath;
        this.maleDeathCauses = maleDeathCauses;
        this.femaleDeath = femaleDeath;
        this.femaleDeathCauses = femaleDeathCauses;
        this.partnering = partnering;
        this.orderedBirth = orderedBirth;
        this.multipleBirth = multipleBirth;
        this.illegitimateBirth = illegitimateBirth;
        this.marriage = marriage;
        this.separation = separation;
        this.sexRatioBirth = sexRatioBirths;

        this.maleForenames = maleForename;
        this.femaleForenames = femaleForename;
        this.surnames = surname;

        this.migrantMaleForenames = migrantMaleForenames;
        this.migrantFemaleForenames = migrantFemaleForenames;
        this.migrantSurnames = migrantSurname;

        this.migrationRate = migrationRate;

        this.minBirthSpacing = minBirthSpacing;
        this.minGestationPeriod = minGestationPeriod;

        this.maleOccupation = maleOccupation;
        this.femaleOccupation = femaleOccupation;

        this.maleOccupationChange = maleOccupationChange;
        this.femaleOccupationChange = femaleOccupationChange;
    }

    /*
    -------------------- EventRateTables interface methods --------------------
     */

    public DeterminedCount getDeterminedCount(StatsKey key, Config config) {

        if (key instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) key;
            return getDeathRates(k.getYear(), k.getSex()).determineCount(k, config, randomGenerator);
        }

        if (key instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) key;
            return getOrderedBirthRates(k.getYear()).determineCount(k, config, randomGenerator);
        }

        if (key instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) key;
            return getMultipleBirthRates(k.getYear()).determineCount(k, config, randomGenerator);
        }

        if (key instanceof IllegitimateBirthStatsKey) {
            IllegitimateBirthStatsKey k = (IllegitimateBirthStatsKey) key;
            return getIllegitimateBirthRates(k.getYear()).determineCount(k, config, randomGenerator);
        }

        if (key instanceof MarriageStatsKey) {
            MarriageStatsKey k = (MarriageStatsKey) key;
            return getMarriageRates(k.getYear()).determineCount(k, config, randomGenerator);
        }

        if (key instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) key;
            return getSeparationByChildCountRates(k.getYear()).determineCount(k, config, randomGenerator);
        }

        if (key instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) key;
            return getPartneringProportions(k.getYear()).determineCount(k, config, randomGenerator);
        }

        if(key instanceof OccupationChangeStatsKey) {
            OccupationChangeStatsKey k = (OccupationChangeStatsKey) key;
            return getOccupationChangeProportions(k.getYear(), k.getSex()).determineCount(k, config, randomGenerator);
        }

        throw new Error("Key based access not implemented for key class: " + key.getClass().toGenericString());
    }

    public void returnAchievedCount(DeterminedCount achievedCount) {

        if (achievedCount.getKey() instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) achievedCount.getKey();
            getDeathRates(k.getYear(), k.getSex()).returnAchievedCount(achievedCount, randomGenerator);
            return;
        }

        if (achievedCount.getKey() instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) achievedCount.getKey();
            getOrderedBirthRates(k.getYear()).returnAchievedCount(achievedCount, randomGenerator);
            return;
        }

        if (achievedCount.getKey() instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) achievedCount.getKey();
            getMultipleBirthRates(k.getYear()).returnAchievedCount(achievedCount, randomGenerator);
            return;
        }

        if (achievedCount.getKey() instanceof IllegitimateBirthStatsKey) {
            IllegitimateBirthStatsKey k = (IllegitimateBirthStatsKey) achievedCount.getKey();
            getIllegitimateBirthRates(k.getYear()).returnAchievedCount(achievedCount, randomGenerator);
            return;
        }

        if (achievedCount.getKey() instanceof MarriageStatsKey) {
            MarriageStatsKey k = (MarriageStatsKey) achievedCount.getKey();
            getMarriageRates(k.getYear()).returnAchievedCount(achievedCount, randomGenerator);
            return;
        }

        if (achievedCount.getKey() instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) achievedCount.getKey();
            getSeparationByChildCountRates(k.getYear()).returnAchievedCount(achievedCount, randomGenerator);
            return;
        }

        if (achievedCount.getKey() instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) achievedCount.getKey();
            getPartneringProportions(k.getYear()).returnAchievedCount(achievedCount, randomGenerator);
            return;
        }

        if(achievedCount.getKey() instanceof OccupationChangeStatsKey) {
            OccupationChangeStatsKey k = (OccupationChangeStatsKey) achievedCount.getKey();
            getOccupationChangeProportions(k.getYear(), k.getSex()).returnAchievedCount(achievedCount, randomGenerator);
            return;
        }

        throw new Error("Key based access not implemented for key class: " + achievedCount.getKey().getClass().toGenericString());
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getDeathRates(Year year, SexOption sex) {

        if (sex == SexOption.MALE) {
            return maleDeath.get(getNearestYearInMap(year, maleDeath));
        } else {
            return femaleDeath.get(getNearestYearInMap(year, femaleDeath));
        }
    }

    @Override
    public EnumeratedDistribution getDeathCauseRates(Year year, SexOption sex, int age) {

        if (sex == SexOption.MALE) {
            return maleDeathCauses.get(getNearestYearInMap(year, maleDeathCauses)).getDistributionForAge(age);
        } else {
            return femaleDeathCauses.get(getNearestYearInMap(year, femaleDeathCauses)).getDistributionForAge(age);
        }
    }

    @Override
    public SelfCorrecting2DIntegerRangeProportionalDistribution getPartneringProportions(Year year) {
        return partnering.get(getNearestYearInMap(year, partnering));
    }

    @Override
    public SelfCorrecting2DEnumeratedProportionalDistribution getOccupationChangeProportions(Year year, SexOption sex) {

        if (sex == SexOption.MALE) {
            return maleOccupationChange.get(getNearestYearInMap(year, maleOccupationChange));
        } else {
            return femaleOccupationChange.get(getNearestYearInMap(year, femaleOccupationChange));
        }
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getIllegitimateBirthRates(Year year) {
        return illegitimateBirth.get(getNearestYearInMap(year, illegitimateBirth));
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getMarriageRates(Year year) {
        return marriage.get(getNearestYearInMap(year, marriage));
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getOrderedBirthRates(Year year) {
        return orderedBirth.get(getNearestYearInMap(year, orderedBirth));
    }

    @Override
    public SelfCorrectingProportionalDistribution getMultipleBirthRates(Year year) {
        return multipleBirth.get(getNearestYearInMap(year, multipleBirth));
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getSeparationByChildCountRates(Year year) {
        return separation.get(getNearestYearInMap(year, separation));
    }

    @Override
    public EnumeratedDistribution getForenameDistribution(Year year, SexOption sex) {

        if (sex == SexOption.MALE) {
            return maleForenames.get(getNearestYearInMap(year, maleForenames));
        } else {
            return femaleForenames.get(getNearestYearInMap(year, femaleForenames));
        }
    }

    @Override
    public EnumeratedDistribution getMigrantForenameDistribution(Year year, SexOption sex) {

        if (sex == SexOption.MALE) {
            return migrantMaleForenames.get(getNearestYearInMap(year, migrantMaleForenames));
        } else {
            return migrantFemaleForenames.get(getNearestYearInMap(year, migrantFemaleForenames));
        }
    }

    @Override
    public EnumeratedDistribution getSurnameDistribution(Year year) {
        return surnames.get(getNearestYearInMap(year, surnames));
    }

    @Override
    public EnumeratedDistribution getMigrantSurnameDistribution(Year year) {
        return migrantSurnames.get(getNearestYearInMap(year, migrantSurnames));
    }

    @Override
    public AgeDependantEnumeratedDistribution getOccupation(Year year, SexOption sex) {
        if (sex == SexOption.MALE) {
            return maleOccupation.get(getNearestYearInMap(year, maleOccupation));
        } else {
            return femaleOccupation.get(getNearestYearInMap(year, femaleOccupation));
        }
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getMigrationRateDistribution(Year year) {
        return migrationRate.get(getNearestYearInMap(year, migrationRate));
    }

    @Override
    public double getMaleProportionOfBirths(Year onDate) {
        return sexRatioBirth.get(getNearestYearInMap(onDate, sexRatioBirth));
    }

    private Year getNearestYearInMap(Year year, TreeMap<Year, ?> map) {

        // the fast way
        Year ceiling = map.ceilingKey(year);
        Year floor = map.floorKey(year);

        if(ceiling == null) return floor;
        if(floor == null) return ceiling;

        int yearInt = year.getValue();

        if(ceiling.getValue() - yearInt > yearInt - floor.getValue())
            return floor;
        else {
            return ceiling;
        }

        // the pretty way
//        long minDifference = Long.MAX_VALUE;
//        Year nearestTableYear = null;
//
//        ArrayList<Year> orderedKeySet = new ArrayList<>(map.keySet());
//        Collections.sort(orderedKeySet); // TODO why necessary?
//
//        for (Year tableYear : orderedKeySet) {
//
//            long difference = Math.abs(year.until(tableYear, ChronoUnit.YEARS));
//
//            if (difference < minDifference) {
//                minDifference = difference;
//                nearestTableYear = tableYear;
//            }
//        }
//
//        return nearestTableYear;
    }

    public Period getMinBirthSpacing() {
        return minBirthSpacing;
    }

    public Period getMinGestationPeriod() {
        return minGestationPeriod;
    }

    public RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

    private static TreeMap<Year, Double> readInSingleInputDataFile(DirectoryStream<Path> paths) throws IOException, InvalidInputFileException {

        int c = 0;

        TreeMap<Year, Double> data = new TreeMap<>();

        for (Path p : paths) {
            if (c == 1) {
                throw new RuntimeException("Too many sex ratio files - there should only be one - remove any additional files from the ratio_birth directory");
            }

            data = InputFileReader.readInSingleInputFile(p);
            c++;
        }

        // TODO shouldn't be hard-wired

        if (data.isEmpty()) {
            data.put(Year.of(1600), 0.5);
        }

        paths.close();

        return data;
    }

    private TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> readInSC1DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        TreeMap<Year, SelfCorrectingOneDimensionDataDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingOneDimensionDataDistribution tempData = InputFileReader.readInSC1DDataFile(path, config, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }

        paths.close();

        return insertDistributionsToMeetInputWidth(config, data);
    }

    private TreeMap<Year, ValiPopEnumeratedDistribution> readInNamesDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        TreeMap<Year, ValiPopEnumeratedDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            ValiPopEnumeratedDistribution tempData = InputFileReader.readInNameDataFile(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        paths.close();
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private TreeMap<Year, AgeDependantEnumeratedDistribution> readInAgeDependantEnumeratedDistributionDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        TreeMap<Year, AgeDependantEnumeratedDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            AgeDependantEnumeratedDistribution tempData = InputFileReader.readInDeathCauseDataFile(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }

        paths.close();
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> readInSC2DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        TreeMap<Year, SelfCorrectingTwoDimensionDataDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingTwoDimensionDataDistribution tempData = InputFileReader.readInSC2DDataFile(path, config, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }

        paths.close();

        return insertDistributionsToMeetInputWidth(config, data);
    }

    private TreeMap<Year, SelfCorrecting2DIntegerRangeProportionalDistribution> readInAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        TreeMap<Year, SelfCorrecting2DIntegerRangeProportionalDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrecting2DIntegerRangeProportionalDistribution tempData = InputFileReader.readInAgeAndProportionalStatsInput(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        paths.close();
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> readInStringAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        TreeMap<Year, SelfCorrecting2DEnumeratedProportionalDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrecting2DEnumeratedProportionalDistribution tempData = InputFileReader.readInStringAndProportionalStatsInput(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        paths.close();
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private TreeMap<Year, SelfCorrectingProportionalDistribution> readInAndAdaptAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        TreeMap<Year, SelfCorrectingProportionalDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingProportionalDistribution tempData = InputFileReader.readInAndAdaptAgeAndProportionalStatsInput(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        paths.close();
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private static <T extends InputMetaData> TreeMap<Year, T> insertDistributionsToMeetInputWidth(Config config, TreeMap<Year, T> inputs) {

        Period inputWidth = config.getInputWidth();

        int diff = config.getT0().getYear() - config.getTS().getYear();
        int stepBack = (int) (inputWidth.getYears() * Math.ceil(diff / (double) inputWidth.getYears()));

//        config.getT0().minus(Period.ofYears(stepBack)).getYear()

//        MonthDate monthDate = config.getT0().advanceTime(new CompoundTimeUnit(stepBack, TimeUnit.YEAR).negative());
        Year prevInputDate = Year.of(config.getT0().minus(Period.ofYears(stepBack)).getYear());

        int c = 1;

        Year[] years = inputs.keySet().toArray(new Year[0]);
        Arrays.sort(years);

        if (years.length == 0) {
            return inputs;
        }

        Year curDate;
        while (true) {

            curDate = prevInputDate.plus(Period.ofYears(inputWidth.getYears() * c));
            if (curDate.isAfter(years[0])) break;
            inputs.put(curDate, inputs.get(years[0]));
            c++;
        }

//        while (DateUtils.dateBeforeOrEqual(curDate = prevInputDate.plus(makePeriod(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())))), years[0])) {
//            inputs.put(curDate.getYear(), inputs.get(years[0]));
//            c++;
//        }

        prevInputDate = years[0];

        for (Year curInputDate : years) {


//            while (DateUtils.dateBeforeOrEqual(curDate = prevInputDate.advanceTime(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())), curInputDate)) {
//                Year duplicateFrom = getNearestDate(curDate, prevInputDate, curInputDate);
//                inputs.put(curDate.getYear(), inputs.get(duplicateFrom));
//                c++;
//            }

            while (true) {
                curDate = prevInputDate.plus(Period.ofYears(inputWidth.getYears() * c));
                if (curDate.isAfter(curInputDate)) break;
                Year duplicateFrom = getNearestDate(curDate, prevInputDate, curInputDate);
                inputs.put(curDate, inputs.get(duplicateFrom));
                c++;
            }

            c = 1;
            prevInputDate = curInputDate;
        }

//        while (DateUtils.dateBeforeOrEqual(curDate = prevInputDate.advanceTime(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())), config.getTE())) {
//            inputs.put(curDate.getYear(), inputs.get(prevInputDate));
//            c++;
//        }

        while (true) {
            curDate = prevInputDate.plus(Period.ofYears(inputWidth.getYears() * c));
            if (curDate.isAfter(Year.of(config.getTE().getYear()))) break;
//            inputs.put(curDate.getYear(), inputs.get(prevInputDate));
            c++;
        }

        return inputs;
    }

//    private static Period makePeriod(CompoundTimeUnit compoundTimeUnit) {
//
//        if (compoundTimeUnit.getUnit() == TimeUnit.MONTH) {
//            return Period.ofMonths(compoundTimeUnit.getCount());
//        } else {
//            return Period.ofYears(compoundTimeUnit.getCount());
//        }
//    }

    private static Year getNearestDate(Year referenceDate, Year option1, Year option2) {

        int refTo1 = Math.abs(referenceDate.getValue() - option1.getValue());
        int refTo2 = Math.abs(referenceDate.getValue() - option2.getValue());

        return (refTo1 < refTo2) ? option1 : option2;
    }
}
