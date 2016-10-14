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

        int popSize = Integer.parseInt(args[0]);
        System.out.println("Generating population to output to source records of size: " + popSize);


        IPopulation population = OrganicPopulation.runPopulationModel(popSize, false, false, false);
        SourceRecordGenerator generator = new SourceRecordGenerator(population);
        generator.generateEventRecords(args);
    }

}
