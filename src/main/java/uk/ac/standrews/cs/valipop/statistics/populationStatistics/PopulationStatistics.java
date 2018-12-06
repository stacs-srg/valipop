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
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.AgeDependantEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.InputMetaData;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ProportionalDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.ValiPopEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingOneDimensionDataDistribution;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.selfCorrecting.SelfCorrectingProportionalDistribution;
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

    private Map<Year, SelfCorrectingOneDimensionDataDistribution> maleDeath;
    private Map<Year, SelfCorrectingOneDimensionDataDistribution> femaleDeath;
    private Map<Year, SelfCorrectingProportionalDistribution> partnering;
    private Map<Year, SelfCorrectingTwoDimensionDataDistribution> orderedBirth;
    private Map<Year, ProportionalDistribution> multipleBirth;
    private Map<Year, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth;
    private Map<Year, SelfCorrectingOneDimensionDataDistribution> marriage;
    private Map<Year, SelfCorrectingTwoDimensionDataDistribution> separation;

    private Map<Year, Double> sexRatioBirth;

    private Map<Year, ValiPopEnumeratedDistribution> maleForenames;
    private Map<Year, ValiPopEnumeratedDistribution> femaleForenames;
    private Map<Year, ValiPopEnumeratedDistribution> surnames;

    private Map<Year, AgeDependantEnumeratedDistribution> maleDeathCauses;
    private Map<Year, AgeDependantEnumeratedDistribution> femaleDeathCauses;

    private Period minGestationPeriod;
    private Period minBirthSpacing;
    private RandomGenerator randomGenerator;

    private static Logger log = Logger.getLogger(PopulationStatistics.class.getName());

    public PopulationStatistics(Config config) {

        try {
            randomGenerator = new JDKRandomGenerator();

            if (config.deterministic()) {
                randomGenerator.setSeed(config.getSeed());
            }

            Map<Year, SelfCorrectingOneDimensionDataDistribution> maleDeath = readInSC1DDataFiles(config.getVarMaleLifetablePaths(), config);
            Map<Year, AgeDependantEnumeratedDistribution> maleDeathCauses = readInDeathCauseDataFiles(config.getVarMaleDeathCausesPaths(), config);
            Map<Year, SelfCorrectingOneDimensionDataDistribution> femaleDeath = readInSC1DDataFiles(config.getVarFemaleLifetablePaths(), config);
            Map<Year, AgeDependantEnumeratedDistribution> femaleDeathCauses = readInDeathCauseDataFiles(config.getVarFemaleDeathCausesPaths(), config);
            Map<Year, SelfCorrectingProportionalDistribution> partnering = readInAgeAndProportionalStatsInputFiles(config.getVarPartneringPaths(), config);
            Map<Year, SelfCorrectingTwoDimensionDataDistribution> orderedBirth = readInSC2DDataFiles(config.getVarOrderedBirthPaths(), config);
            Map<Year, ProportionalDistribution> multipleBirth = readInAndAdaptAgeAndProportionalStatsInputFiles(config.getVarMultipleBirthPaths(), config);
            Map<Year, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth = readInSC1DDataFiles(config.getVarIllegitimateBirthPaths(), config);
            Map<Year, SelfCorrectingOneDimensionDataDistribution> marriage = readInSC1DDataFiles(config.getVarMarriagePaths(), config);
            Map<Year, SelfCorrectingTwoDimensionDataDistribution> separation = readInSC2DDataFiles(config.getVarSeparationPaths(), config);
            Map<Year, Double> sexRatioBirth = readInSingleInputDataFile(config.getVarBirthRatioPath());
            Map<Year, ValiPopEnumeratedDistribution> maleForename = readInNamesDataFiles(config.getVarMaleForenamePath(), config);
            Map<Year, ValiPopEnumeratedDistribution> femaleForename = readInNamesDataFiles(config.getVarFemaleForenamePath(), config);
            Map<Year, ValiPopEnumeratedDistribution> surname = readInNamesDataFiles(config.getVarSurnamePath(), config);

            init(maleDeath, maleDeathCauses, femaleDeath, femaleDeathCauses, partnering, orderedBirth, multipleBirth, illegitimateBirth,
                    marriage, separation, sexRatioBirth, maleForename, femaleForename, surname, config.getMinBirthSpacing(),
                    config.getMinGestationPeriod());

        } catch (IOException | InvalidInputFileException | InconsistentWeightException e) {
            throw new RuntimeException(e);
        }
    }

    public PopulationStatistics(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public PopulationStatistics(Map<Year, SelfCorrectingOneDimensionDataDistribution> maleDeath,
                                Map<Year, AgeDependantEnumeratedDistribution> maleDeathCauses,
                                Map<Year, SelfCorrectingOneDimensionDataDistribution> femaleDeath,
                                Map<Year, AgeDependantEnumeratedDistribution> femaleDeathCauses,
                                Map<Year, SelfCorrectingProportionalDistribution> partnering,
                                Map<Year, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                                Map<Year, ProportionalDistribution> multipleBirth,
                                Map<Year, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth,
                                Map<Year, SelfCorrectingOneDimensionDataDistribution> marriage,
                                Map<Year, SelfCorrectingTwoDimensionDataDistribution> separation,
                                Map<Year, Double> sexRatioBirths,
                                Map<Year, ValiPopEnumeratedDistribution> maleForenames,
                                Map<Year, ValiPopEnumeratedDistribution> femaleForenames,
                                Map<Year, ValiPopEnumeratedDistribution> surnames,
                                Period minBirthSpacing,
                                Period minGestationPeriod,
                                RandomGenerator randomGenerator) {

        this.randomGenerator = randomGenerator;
        init(maleDeath, maleDeathCauses, femaleDeath, femaleDeathCauses, partnering, orderedBirth, multipleBirth, illegitimateBirth, marriage, separation, sexRatioBirths, maleForenames, femaleForenames, surnames, minBirthSpacing, minGestationPeriod);
    }

    private void init(Map<Year, SelfCorrectingOneDimensionDataDistribution> maleDeath, Map<Year, AgeDependantEnumeratedDistribution> maleDeathCauses, Map<Year, SelfCorrectingOneDimensionDataDistribution> femaleDeath,
                      Map<Year, AgeDependantEnumeratedDistribution> femaleDeathCauses, Map<Year, SelfCorrectingProportionalDistribution> partnering, Map<Year, SelfCorrectingTwoDimensionDataDistribution> orderedBirth,
                      Map<Year, ProportionalDistribution> multipleBirth, Map<Year, SelfCorrectingOneDimensionDataDistribution> illegitimateBirth, Map<Year, SelfCorrectingOneDimensionDataDistribution> marriage,
                      Map<Year, SelfCorrectingTwoDimensionDataDistribution> separation, Map<Year, Double> sexRatioBirths, Map<Year, ValiPopEnumeratedDistribution> maleForename, Map<Year, ValiPopEnumeratedDistribution> femaleForename,
                      Map<Year, ValiPopEnumeratedDistribution> surname, Period minBirthSpacing, Period minGestationPeriod) {

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

        this.minBirthSpacing = minBirthSpacing;
        this.minGestationPeriod = minGestationPeriod;
    }

    /*
    -------------------- EventRateTables interface methods --------------------
     */

    public DeterminedCount getDeterminedCount(StatsKey key, Config config) {

        if (key instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) key;
            return getDeathRates(k.getYear(), k.getSex()).determineCount(k, config);
        }

        if (key instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) key;
            return getOrderedBirthRates(k.getYear()).determineCount(k, config);
        }

        if (key instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) key;
            return getMultipleBirthRates(k.getYear()).determineCount(k, config);
        }

        if (key instanceof IllegitimateBirthStatsKey) {
            IllegitimateBirthStatsKey k = (IllegitimateBirthStatsKey) key;
            return getIllegitimateBirthRates(k.getYear()).determineCount(k, config);
        }

        if (key instanceof MarriageStatsKey) {
            MarriageStatsKey k = (MarriageStatsKey) key;
            return getMarriageRates(k.getYear()).determineCount(k, config);
        }

        if (key instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) key;
            return getSeparationByChildCountRates(k.getYear()).determineCount(k, config);
        }

        if (key instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) key;
            return getPartneringRates(k.getYear()).determineCount(k, config);
        }

        throw new Error("Key based access not implemented for key class: " + key.getClass().toGenericString());
    }

    public void returnAchievedCount(DeterminedCount achievedCount) {

        if (achievedCount.getKey() instanceof DeathStatsKey) {
            DeathStatsKey k = (DeathStatsKey) achievedCount.getKey();
            getDeathRates(k.getYear(), k.getSex()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof BirthStatsKey) {
            BirthStatsKey k = (BirthStatsKey) achievedCount.getKey();
            getOrderedBirthRates(k.getYear()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof MultipleBirthStatsKey) {
            MultipleBirthStatsKey k = (MultipleBirthStatsKey) achievedCount.getKey();
            getMultipleBirthRates(k.getYear()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof IllegitimateBirthStatsKey) {
            IllegitimateBirthStatsKey k = (IllegitimateBirthStatsKey) achievedCount.getKey();
            getIllegitimateBirthRates(k.getYear()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof MarriageStatsKey) {
            MarriageStatsKey k = (MarriageStatsKey) achievedCount.getKey();
            getMarriageRates(k.getYear()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof SeparationStatsKey) {
            SeparationStatsKey k = (SeparationStatsKey) achievedCount.getKey();
            getSeparationByChildCountRates(k.getYear()).returnAchievedCount(achievedCount);
            return;
        }

        if (achievedCount.getKey() instanceof PartneringStatsKey) {
            PartneringStatsKey k = (PartneringStatsKey) achievedCount.getKey();
            getPartneringRates(k.getYear()).returnAchievedCount(achievedCount);
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
    public SelfCorrectingProportionalDistribution getPartneringRates(Year year) {
        return partnering.get(getNearestYearInMap(year, partnering));
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
    public ProportionalDistribution getMultipleBirthRates(Year year) {
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
    public EnumeratedDistribution getSurnameDistribution(Year year) {
        return surnames.get(getNearestYearInMap(year, surnames));
    }

    @Override
    public double getMaleProportionOfBirths(Year onDate) {
        return sexRatioBirth.get(getNearestYearInMap(onDate, sexRatioBirth));
    }

    private Year getNearestYearInMap(Year year, Map<Year, ?> map) {

        long minDifference = Long.MAX_VALUE;
        Year nearestTableYear = null;

        ArrayList<Year> orderedKeySet = new ArrayList<>(map.keySet());
        Collections.sort(orderedKeySet); // TODO why necessary?

        for (Year tableYear : orderedKeySet) {

            long difference = Math.abs(year.until(tableYear, ChronoUnit.YEARS));

            if (difference < minDifference) {
                minDifference = difference;
                nearestTableYear = tableYear;
            }
        }

        return nearestTableYear;
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

    private static Map<Year, Double> readInSingleInputDataFile(DirectoryStream<Path> paths) throws IOException, InvalidInputFileException {

        int c = 0;

        Map<Year, Double> data = new TreeMap<>();

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

        return data;
    }

    private Map<Year, SelfCorrectingOneDimensionDataDistribution> readInSC1DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<Year, SelfCorrectingOneDimensionDataDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingOneDimensionDataDistribution tempData = InputFileReader.readInSC1DDataFile(path, config, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<Year, ValiPopEnumeratedDistribution> readInNamesDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        Map<Year, ValiPopEnumeratedDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            ValiPopEnumeratedDistribution tempData = InputFileReader.readInNameDataFile(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<Year, AgeDependantEnumeratedDistribution> readInDeathCauseDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException, InconsistentWeightException {

        Map<Year, AgeDependantEnumeratedDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            AgeDependantEnumeratedDistribution tempData = InputFileReader.readInDeathCauseDataFile(path, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<Year, SelfCorrectingTwoDimensionDataDistribution> readInSC2DDataFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<Year, SelfCorrectingTwoDimensionDataDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingTwoDimensionDataDistribution tempData = InputFileReader.readInSC2DDataFile(path, config, randomGenerator);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<Year, SelfCorrectingProportionalDistribution> readInAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<Year, SelfCorrectingProportionalDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            SelfCorrectingProportionalDistribution tempData = InputFileReader.readInAgeAndProportionalStatsInput(path);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private Map<Year, ProportionalDistribution> readInAndAdaptAgeAndProportionalStatsInputFiles(DirectoryStream<Path> paths, Config config) throws IOException, InvalidInputFileException {

        Map<Year, ProportionalDistribution> data = new TreeMap<>();

        for (Path path : paths) {
            // read in each file
            ProportionalDistribution tempData = InputFileReader.readInAndAdaptAgeAndProportionalStatsInput(path);
            data.put(tempData.getYear(), tempData);
        }
        return insertDistributionsToMeetInputWidth(config, data);
    }

    private static <T extends InputMetaData> Map<Year, T> insertDistributionsToMeetInputWidth(Config config, Map<Year, T> inputs) {

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
