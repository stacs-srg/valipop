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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.comparison;

import uk.ac.standrews.cs.digitising_scotland.verisim.config.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.exceptions.UnsupportedDateConversion;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.UnsupportedEventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.OneDimensionDataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.exceptions.StatisticalManipulationCalculationError;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.kaplanMeier.IKaplanMeierAnalysis;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.validation.summaryData.SummaryRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.IPopulationExtended;


import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

/**
 * The IComparativeAnalysis interface provides statistical tests to verify the simulated population against a given
 * population.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public interface IComparativeAnalysis {


    /**
     * Runs Kaplan-Meier analysis, see the provided {@link IKaplanMeierAnalysis} class.
     *
     * @param expectedEvents the expected events
     * @param observedEvents the observed events
     * @return the km analysis
     */
    static IKaplanMeierAnalysis runKaplanMeier(EventType event, CompoundTimeUnit timePeriod, OneDimensionDataDistribution expectedEvents, OneDimensionDataDistribution observedEvents) throws StatisticalManipulationCalculationError {
        return null;
    }

    Map<Date, Map<EventType, IKaplanMeierAnalysis>> getResults();

    SummaryRow outputResults(PrintStream resultOutput, SummaryRow summary) throws UnsupportedDateConversion;

    void runAnalysis(IPopulationExtended generatedPopulation, Config config) throws UnsupportedDateConversion, StatisticalManipulationCalculationError, IOException, UnsupportedEventType;

}
