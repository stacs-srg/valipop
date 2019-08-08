package uk.ac.standrews.cs.valipop.utils.sourceEventRecords;

import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat.EGSkyeSourceRecordGenerator;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecordGenerator;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisualiserFormat.RelationshipsTable;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisualiserFormat.SimplifiedSourceRecordGenerator;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.tdFormat.TDSourceRecordGenerator;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class RecordGenerationFactory {

    public static final Logger log = Logger.getLogger(RecordGenerationFactory.class.getName());

    public static void outputRecords(RecordFormat recordFormat, Path recordsOutputDir,  PeopleCollection population, LocalDate startDate) {

        switch(recordFormat) {
            case DS:
                extractBMDRecords(population, recordsOutputDir);
                break;
            case EG_SKYE:
                extractEGSkyeRecords(population, recordsOutputDir, startDate);
                break;
            case TD:
                extractTDRecords(population, recordsOutputDir, startDate);
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

    private static void extractSimplifiedBMDRecords(PeopleCollection population, Path recordsDirPath) {
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

    private static void extractBMDRecords(PeopleCollection population, Path recordsDirPath) {
        log.info("OBDModel --- Outputting BMD records");

        try {
            new SourceRecordGenerator(population, recordsDirPath).generateEventRecords(new String[0]);

        } catch (Exception e) {
            log.info("Record generation failed");
            e.printStackTrace();
            log.info(e.getMessage());
        }
    }

    private static void extractEGSkyeRecords(PeopleCollection population, Path recordsDirPath, LocalDate startDate) {
        log.info("OBDModel --- Outputting EG_SKYE records");

        try {
            new EGSkyeSourceRecordGenerator(population, recordsDirPath).generateEventRecords(startDate);
        } catch (Exception e) {
            log.info("Record generation failed");
            e.printStackTrace();
            log.info(e.getMessage());
        }
    }

    private static void extractTDRecords(PeopleCollection population, Path recordsDirPath, LocalDate startDate) {
        log.info("OBDModel --- Outputting TD records");

        try {
            new TDSourceRecordGenerator(population, recordsDirPath).generateEventRecords(startDate);
        } catch (Exception e) {
            log.info("Record generation failed");
            e.printStackTrace();
            log.info(e.getMessage());
        }
    }
}
