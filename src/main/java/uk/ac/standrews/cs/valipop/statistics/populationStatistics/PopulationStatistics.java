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
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SexOption;
import uk.ac.standrews.cs.valipop.statistics.distributions.general.EnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsKeys.*;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.EventRateTables;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.AgeDependantEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.InputMetaData;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ValiPopEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingTwoDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InputFileReader;
import uk.ac.standrews.cs.valipop.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.*;

/**
 * The PopulationStatistics holds data about the rate at which specified events occur to specified subsets of
 * members of the summative population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationStatistics implements EventRateTables {

    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath;
    private Map<YearDate, SelfCorrectingProportionalDistribution> partnering;
    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth;
    private Map<YearDate, ProportionalDistribution> multipleBirth;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth;
    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> marriage;
    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> separation;

    private Map<YearDate, Double> sexRatioBirth;

    private Map<YearDate, ValiPopEnumeratedDistribution> maleForename;
    private Map<YearDate, ValiPopEnumeratedDistribution> femaleForename;

    private Map<YearDate, ValiPopEnumeratedDistribution> surname;

    private Map<YearDate, AgeDependantEnumeratedDistribution> maleDeathCauses;
    private Map<YearDate, AgeDependantEnumeratedDistribution> femaleDeathCauses;

    // Population Constants
    private int minGestationPeriodDays = 147;
    private int minBirthSpacingDays = 147;
    private RandomGenerator randomGenerator;

    private static Logger log = new Logger(PopulationStatistics.class);

    private static final int DEFAULT_DETERMINISTIC_SEED = 56854687;

    /**
     * Creates a PopulationStatistics object.
     *
     * @return the quantified event occurrences
     */
    public PopulationStatistics(Config config) {

        try {
            log.info("Creating PopulationStatistics instance");

            this.randomGenerator = new JDKRandomGenerator();

            if (config.deterministic()) {
                randomGenerator.setSeed(config.getSeed() == 0 ? DEFAULT_DETERMINISTIC_SEED : config.getSeed());
            }

            Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath = readInSC1DDataFiles(config.getVarMaleLifetablePaths(), config);
            Map<YearDate, AgeDependantEnumeratedDistribution> maleDeathCauses = readInDeathCauseDataFiles(config.getVarMaleDeathCausesPaths(), config);
            Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath = readInSC1DDataFiles(config.getVarFemaleLifetablePaths(), config);
            Map<YearDate, AgeDependantEnumeratedDistribution> femaleDeathCauses = readInDeathCauseDataFiles(config.getVarFemaleDeathCausesPaths(), config);
            Map<YearDate, SelfCorrectingProportionalDistribution> partnering = readInAgeAndProportionalStatsInputFiles(config.getVarPartneringPaths(), config);
            Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth = readInSC2DDataFiles(config.getVarOrderedBirthPaths(), config);
            Map<YearDate, ProportionalDistribution> multipleBirth = readInAndAdaptAgeAndProportionalStatsInputFiles(config.getVarMultipleBirthPaths(), config);
            Map<YearDate, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth = readInSC1DDataFiles(config.getVarIllegitimateBirthPaths(), config);
            Map<YearDate, SelfCorrectingOneDimensionDataDistribution> marriage = readInSC1DDataFiles(config.getVarMarriagePaths(), config);
            Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> separation = readInSC2DDataFiles(config.getVarSeparationPaths(), config);
            Map<YearDate, Double> sexRatioBirth = readInSingleInputDataFile(config.getVarBirthRatioPath(), config);
            Map<YearDate, ValiPopEnumeratedDistribution> maleForename = readInNamesDataFiles(config.getVarMaleForenamePath(), config);
            Map<YearDate, ValiPopEnumeratedDistribution> femaleForename = readInNamesDataFiles(config.getVarFemaleForenamePath(), config);
            Map<YearDate, ValiPopEnumeratedDistribution> surname = readInNamesDataFiles(config.getVarSurnamePath(), config);

            init(maleDeath, maleDeathCauses, femaleDeath, femaleDeathCauses, partnering, orderedBirth, multipleBirth, illegitimateBirth,
                    marriage, separation, sexRatioBirth, maleForename, femaleForename, surname, config.getMinBirthSpacing(),
                    config.getMinGestationPeriodDays());

        } catch (IOException | InvalidInputFileException | InconsistentWeightException e) {
            throw new RuntimeException(e);
        }
    }

    public PopulationStatistics(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public PopulationStatistics(Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath,
                                Map<YearDate, AgeDependantEnumeratedDistribution> maleDeathCauses,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath,
                                Map<YearDate, AgeDependantEnumeratedDistribution> femaleDeathCauses,
                                Map<YearDate, SelfCorrectingProportionalDistribution> partnering,
                                Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                                Map<YearDate, ProportionalDistribution> multipleBirth,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth,
                                Map<YearDate, SelfCorrectingOneDimensionDataDistribution> marriage,
                                Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> separation,
                                Map<YearDate, Double> sexRatioBirths,
                                Map<YearDate, ValiPopEnumeratedDistribution> maleForename,
                                Map<YearDate, ValiPopEnumeratedDistribution> femaleForename,
                                Map<YearDate, ValiPopEnumeratedDistribution> surname,
                                int minBirthSpacingDays,
                                int minGestationPeriodDays,
                                RandomGenerator randomGenerator) {

        this.randomGenerator = randomGenerator;
        init(maleDeath, maleDeathCauses, femaleDeath, femaleDeathCauses, partnering, orderedBirth, multipleBirth, illegitimateBirth, marriage, separation, sexRatioBirths, maleForename, femaleForename, surname, minBirthSpacingDays, minGestationPeriodDays);
    }

    private void init(Map<YearDate, SelfCorrectingOneDimensionDataDistribution> maleDeath, Map<YearDate, AgeDependantEnumeratedDistribution> maleDeathCauses, Map<YearDate, SelfCorrectingOneDimensionDataDistribution> femaleDeath, Map<YearDate, AgeDependantEnumeratedDistribution> femaleDeathCauses, Map<YearDate, SelfCorrectingProportionalDistribution> partnering, Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> orderedBirth, Map<YearDate, ProportionalDistribution> multipleBirth, Map<YearDate, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth, Map<YearDate, SelfCorrectingOneDimensionDataDistribution> marriage, Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> separation, Map<YearDate, Double> sexRatioBirths, Map<YearDate, ValiPopEnumeratedDistribution> maleForename, Map<YearDate, ValiPopEnumeratedDistribution> femaleForename, Map<YearDate, ValiPopEnumeratedDistribution> surname, int minBirthSpacingDays, int minGestationPeriodDays) {

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

        this.maleForename = maleForename;
        this.femaleForename = femaleForename;
        this.surname = surname;

        this.minBirthSpacingDays = minBirthSpacingDays;
        this.minGestationPeriodDays = minGestationPeriodDays;
    }

    /*
    -------------------- EventRateTables interface methods --------------------
     */

    public DeterminedCount getDeterminedCount(StatsKey key, Config config) {

        if (key instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) key;
            return getDeathRates(k.getDate(), k.getSex()).determineCount(k, config);
        }

        if (key instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) key;
            return getOrderedBirthRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) key;
            return getMultipleBirthRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof IllegitimateBirthStatsKey) {
            IllegitimateBirthStatsKey k = (IllegitimateBirthStatsKey) key;
            return getIllegitimateBirthRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof MarriageStatsKey) {
            MarriageStatsKey k = (MarriageStatsKey) key;
            return getMarriageRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) key;
            return getSeparationByChildCountRates(k.getDate()).determineCount(k, config);
        }

        if (key instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) key;
            return getPartneringRates(k.getDate()).determineCount(k, config);
        }

        throw new Error("Key based access not implemented for key class: " + key.getClass().toGenericString());
    }

    public void returnAchievedCount(DeterminedCount achievedCount) {

        if (achievedCount.getKey() instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) achievedCount.getKey();
            getDeathRates(k.getDate(), k.getSex()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) achievedCount.getKey();
            getOrderedBirthRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) achievedCount.getKey();
            getMultipleBirthRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof IllegitimateBirthStatsKey) {
            IllegitimateBirthStatsKey k = (IllegitimateBirthStatsKey) achievedCount.getKey();
            getIllegitimateBirthRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof MarriageStatsKey) {
            MarriageStatsKey k = (MarriageStatsKey) achievedCount.getKey();
            getMarriageRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) achievedCount.getKey();
            getSeparationByChildCountRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) achievedCount.getKey();
            getPartneringRates(k.getDate()).returnAchievedCount(achievedCount);
            return;
        }

        throw new Error("Key based access not implemented for key class: "
                + achievedCount.getKey().getClass().toGenericString());
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getDeathRates(ValipopDate year, SexOption sex) {
        if (sex == SexOption.MALE) {
            return maleDeath.get(getNearestYearInMap(year.getYearDate(), maleDeath));
        } else {
            return femaleDeath.get(getNearestYearInMap(year.getYearDate(), femaleDeath));
        }
    }

    @Override
    public EnumeratedDistribution getDeathCauseRates(ValipopDate year, SexOption sex, int age) {
        if (sex == SexOption.MALE) {
            return maleDeathCauses.get(getNearestYearInMap(year.getYearDate(), maleDeathCauses)).getDistributionForAge(age);
        } else {
            return femaleDeathCauses.get(getNearestYearInMap(year.getYearDate(), femaleDeathCauses)).getDistributionForAge(age);
        }
    }

    @Override
    public SelfCorrectingProportionalDistribution getPartneringRates(ValipopDate year) {
        return partnering.get(getNearestYearInMap(year.getYearDate(), partnering));
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getIllegitimateBirthRates(ValipopDate year) {
        return illegitimateBirth.get(getNearestYearInMap(year.getYearDate(), illegitimateBirth));
    }

    @Override
    public SelfCorrectingOneDimensionDataDistribution getMarriageRates(ValipopDate year) {
        return marriage.get(getNearestYearInMap(year.getYearDate(), marriage));
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getOrderedBirthRates(ValipopDate year) {
        return orderedBirth.get(getNearestYearInMap(year.getYearDate(), orderedBirth));
    }

    @Override
    public ProportionalDistribution getMultipleBirthRates(ValipopDate year) {
        return multipleBirth.get(getNearestYearInMap(year.getYearDate(), multipleBirth));
    }

    @Override
    public SelfCorrectingTwoDimensionDataDistribution getSeparationByChildCountRates(ValipopDate year) {
        return separation.get(getNearestYearInMap(year.getYearDate(), separation));
    }

    @Override
    public EnumeratedDistribution getForenameDistribution(ValipopDate year, SexOption sex) {
        if (sex == SexOption.MALE) {
            return maleForename.get(getNearestYearInMap(year.getYearDate(), maleForename));
        } else {
            return femaleForename.get(getNearestYearInMap(year.getYearDate(), femaleForename));
        }
    }

    @Override
    public EnumeratedDistribution getSurnameDistribution(ValipopDate year) {
        return surname.get(getNearestYearInMap(year.getYearDate(), surname));
    }

    @Override
    public double getMaleProportionOfBirths(ValipopDate onDate) {
        return sexRatioBirth.get(getNearestYearInMap(onDate, sexRatioBirth));
    }

    private YearDate getNearestYearInMap(ValipopDate year, Map<YearDate, ?> map) {

        int minDifferenceInMonths = Integer.MAX_VALUE;
        YearDate nearestTableYear = null;

        ArrayList<YearDate> orderedKeySet = new ArrayList<>(map.keySet());
        Collections.sort(orderedKeySet);


        for (YearDate tableYear : orderedKeySet) {
            int difference = DateUtils.differenceInMonths(tableYear, year.getYearDate()).getCount();
            if (difference < minDifferenceInMonths) {
                minDifferenceInMonths = difference;
                nearestTableYear = tableYear;
            }
        }

        return nearestTableYear;
    }

    public int getMinBirthSpacing() {
        return minBirthSpacingDays;
    }

    public int getMinGestationPeriod() {
        return minGestationPeriodDays;
    }

    public RandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

    private static Map<YearDate, Double> readInSingleInputDataFile(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        int c = 0;

        Map<YearDate, Double> data = new TreeMap<>();

        for (Path p : paths) {
            if (c == 1) {
                throw new Error("Too many sex ratio files - there should only be one - remove any additional files from the ratio_birth directory");
            }

            data = InputFileReader.readInSingleInputFile(p, config);

            c++;
        }

        if (data.isEmpty()) {
            data.put(new YearDate(1600), 0.5);
        }

        return data;
    }

    private Map<YearDate, SelfCorrectingOneDimensionDataDistribution> readInSC1DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, SelfCorrectingOneDimensionDataDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingOneDimensionDataDistribution tempData = InputFileReader.readInSC1DDataFile(path, config, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<YearDate, ValiPopEnumeratedDistribution> readInNamesDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        Map<YearDate, ValiPopEnumeratedDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            ValiPopEnumeratedDistribution tempData = InputFileReader.readInNameDataFile(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<YearDate, AgeDependantEnumeratedDistribution> readInDeathCauseDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        Map<YearDate, AgeDependantEnumeratedDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            AgeDependantEnumeratedDistribution tempData = InputFileReader.readInDeathCauseDataFile(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> readInSC2DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, SelfCorrectingTwoDimensionDataDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingTwoDimensionDataDistribution tempData = InputFileReader.readInSC2DDataFile(path, config, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<YearDate, SelfCorrectingProportionalDistribution> readInAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, SelfCorrectingProportionalDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingProportionalDistribution tempData = InputFileReader.readInAgeAndProportionalStatsInput(path);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<YearDate, ProportionalDistribution> readInAndAdaptAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<YearDate, ProportionalDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            ProportionalDistribution tempData = InputFileReader.readInAndAdaptAgeAndProportionalStatsInput(path);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private static <T extends InputMetaData> Map<YearDate, T> insertDistributionsToMeetInputWidth(Config config, Map<YearDate, T> inputs) {

        CompoundTimeUnit inputWidth = config.getInputWidth();

        int stepBack = (int) (inputWidth.getCount() * Math.ceil((config.getT0().getYear() - config.getTS().getYear()) / (double) inputWidth.getCount()));

        YearDate prevInputDate = config.getT0().advanceTime(new CompoundTimeUnit(stepBack, TimeUnit.YEAR).negative()).getYearDate();

        int c = 1;
        ValipopDate curDate;

        YearDate[] years = inputs.keySet().toArray(new YearDate[inputs.keySet().size()]);
        Arrays.sort(years);

        if (years.length == 0) {
            return inputs;
        }

        while (DateUtils.dateBeforeOrEqual(curDate = prevInputDate.advanceTime(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())), years[0])) {
            inputs.put(curDate.getYearDate(), inputs.get(years[0]));
            c++;
        }

        prevInputDate = years[0];

        for (YearDate curInputDate : years) {


            while (DateUtils.dateBeforeOrEqual(curDate = prevInputDate.advanceTime(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())), curInputDate)) {
                YearDate duplicateFrom = getNearestDate(curDate, prevInputDate, curInputDate);
                inputs.put(curDate.getYearDate(), inputs.get(duplicateFrom));
                c++;
            }

            c = 1;
            prevInputDate = curInputDate;
        }

        while (DateUtils.dateBeforeOrEqual(curDate = prevInputDate.advanceTime(new CompoundTimeUnit(inputWidth.getCount() * c, inputWidth.getUnit())), config.getTE())) {
            inputs.put(curDate.getYearDate(), inputs.get(prevInputDate));
            c++;
        }

        return inputs;
    }

    private static YearDate getNearestDate(ValipopDate referenceDate, YearDate option1, YearDate option2) {

        int refTo1 = DateUtils.differenceInDays(referenceDate, option1);
        int refTo2 = DateUtils.differenceInDays(referenceDate, option2);

        return (refTo1 < refTo2) ? option1 : option2;
    }
}
