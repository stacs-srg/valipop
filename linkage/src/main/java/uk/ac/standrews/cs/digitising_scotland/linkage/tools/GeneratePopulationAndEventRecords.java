package uk.ac.standrews.cs.digitising_scotland.linkage.tools;

import uk.ac.standrews.cs.digitising_scotland.linkage.source_event_records.SourceRecordGenerator;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.database.DBPopulationAdapter;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class GeneratePopulationAndEventRecords {

    public static void main(final String[] args) throws Exception {

        IPopulation population = OrganicPopulation.runPopulationModel(20000, false, false, false);
        SourceRecordGenerator generator = new SourceRecordGenerator(population);
        generator.generateEventRecords(args);
    }

}
