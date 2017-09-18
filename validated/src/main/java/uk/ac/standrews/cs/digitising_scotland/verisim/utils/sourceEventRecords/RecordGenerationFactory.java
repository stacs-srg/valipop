package uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.oldDSformat.SourceRecordGenerator;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.processingVisuliserFormat.RelationshipsTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.processingVisuliserFormat.SimplifiedSourceRecordGenerator;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class RecordGenerationFactory {

    public static final Logger log = LogManager.getLogger(RecordGenerationFactory.class);

    public static void outputRecords(RecordFormat recordFormat, String recordsOutputDir,  PeopleCollection population) {

        switch(recordFormat) {
            case DS:
                extractBMDRecords(population, recordsOutputDir);
                break;
            case VIS_PROCESSING:
                extractSimplifiedBMDRecords(population, recordsOutputDir);
                break;
            case NONE:
                break;
            default:
                break;


        }

    }

    private static void extractSimplifiedBMDRecords(PeopleCollection population, String recordsDirPath) {
        log.info("OBDModel --- Outputting Simplified BMD records");

        try {
            new SimplifiedSourceRecordGenerator(population, recordsDirPath).generateEventRecords(new String[0]);
            RelationshipsTable.outputData(recordsDirPath);
        } catch (Exception e) {
            log.info("Record generation failed");
            e.printStackTrace();
            log.info(e.getMessage());
        }

    }

    private static void extractBMDRecords(PeopleCollection population, String recordsDirPath) {
        log.info("OBDModel --- Outputting BMD records");

        try {
            new SourceRecordGenerator(population, recordsDirPath).generateEventRecords(new String[0]);
        } catch (Exception e) {
            log.info("Record generation failed");
            e.printStackTrace();
            log.info(e.getMessage());
        }
    }

}
