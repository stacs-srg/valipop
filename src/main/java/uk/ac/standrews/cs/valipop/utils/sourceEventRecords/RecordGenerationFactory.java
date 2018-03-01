package uk.ac.standrews.cs.valipop.utils.sourceEventRecords;

import uk.ac.standrews.cs.valipop.utils.Logger;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat.EGSkyeSourceRecordGenerator;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecordGenerator;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisuliserFormat.RelationshipsTable;
import uk.ac.standrews.cs.valipop.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisuliserFormat.SimplifiedSourceRecordGenerator;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class RecordGenerationFactory {

    public static final Logger log = new Logger(RecordGenerationFactory.class);

    public static void outputRecords(RecordFormat recordFormat, String recordsOutputDir,  PeopleCollection population, Date startDate) {

        switch(recordFormat) {
            case DS:
                extractBMDRecords(population, recordsOutputDir);
                break;
            case EG_SKYE:
                extractEGSkyeRecords(population, recordsOutputDir, startDate);
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

    private static void extractEGSkyeRecords(PeopleCollection population, String recordsDirPath, Date startDate) {
        log.info("OBDModel --- Outputting EG_SKYE records");

        try {
            new EGSkyeSourceRecordGenerator(population, recordsDirPath).generateEventRecords(startDate);
        } catch (Exception e) {
            log.info("Record generation failed");
            e.printStackTrace();
            log.info(e.getMessage());
        }
    }

}
