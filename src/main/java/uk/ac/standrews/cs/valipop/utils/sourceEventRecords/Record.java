package uk.ac.standrews.cs.valipop.utils.sourceEventRecords;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import uk.ac.standrews.cs.utilities.FileManipulation;
import uk.ac.standrews.cs.utilities.TimeManipulation;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecord;
import uk.ac.standrews.cs.utilities.archive.Diagnostic;

abstract public class Record {
  private static final String BIRTH_RECORDS_PATH = "birth_records.csv";
  private static final String DEATH_RECORDS_PATH = "death_records.csv";
  private static final String MARRIAGE_RECORDS_PATH = "marriage_records.csv";

  // TODO allow output file paths to be configured, add -i option to output to console

  private Iterable<IPerson> people;
  private Iterable<IPartnership> partnerships;

  Record(Iterable<IPerson> people, Iterable<IPartnership> partnerships) {
    this.people = people;
    this.partnerships = partnerships;
  }

  abstract protected Iterable<? extends SourceRecord> toBirthRecords(Iterable<IPerson> people);
  abstract protected Iterable<? extends SourceRecord> toDeathRecords(Iterable<IPerson> people);
  abstract protected Iterable<? extends SourceRecord> toMarriageRecords(Iterable<IPartnership> partnerships);

  public void exportRecords(Path recordDir) throws IOException {

    final long start_time = System.currentTimeMillis();

    Diagnostic.traceNoSource("Generating birth records");
    exportRecord(toBirthRecords(people), recordDir.resolve(BIRTH_RECORDS_PATH));
    TimeManipulation.reportElapsedTime(start_time);

    Diagnostic.traceNoSource("Generating death records");
    exportRecord(toDeathRecords(people), recordDir.resolve(DEATH_RECORDS_PATH));
    TimeManipulation.reportElapsedTime(start_time);

    Diagnostic.traceNoSource("Generating marriage records");
    exportRecord(toMarriageRecords(partnerships), recordDir.resolve(MARRIAGE_RECORDS_PATH));
    TimeManipulation.reportElapsedTime(start_time);
  }

  private void exportRecord(final Iterable<? extends SourceRecord> records, Path recordPath) throws IOException {
    // Generate birth records
    FileManipulation.createParentDirectoryIfDoesNotExist(recordPath);

    try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(recordPath, FileManipulation.FILE_CHARSET))) {

        boolean first = true;

        for (final SourceRecord record : records) {

            if (first) {
                writer.println(record.getHeaders());
                first = false;
            }

            writer.println(record);
        }
    }
  }
}
