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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.populationTransformation;


import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.database.DBPopulationAdapter;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.source_event_records.SourceRecordGenerator;


/**
 * Generates birth/death/marriage records for all the people in the database.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (al@st-andrews.ac.uk)
 */
public class GenerateEventRecordsFromDB {

    public static void main(final String[] args) throws Exception {

        IPopulation population = new DBPopulationAdapter();
        SourceRecordGenerator generator = new SourceRecordGenerator(population);
        generator.generateEventRecords(args);
    }
}
