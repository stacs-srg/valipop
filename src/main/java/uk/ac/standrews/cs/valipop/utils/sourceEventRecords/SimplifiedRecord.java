package uk.ac.standrews.cs.valipop.utils.sourceEventRecords;

import uk.ac.standrews.cs.utilities.MappedIterator;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisualiserFormat.SimplifiedBirthSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisualiserFormat.SimplifiedDeathSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.processingVisualiserFormat.SimplifiedMarriageSourceRecord;

public class SimplifiedRecord extends Record {

    SimplifiedRecord(Iterable<IPerson> people, Iterable<IPartnership> partneships) {
      super(people, partneships);
    }

    @Override
    protected Iterable<SourceRecord> toBirthRecords(Iterable<IPerson> people) {
        return () -> new MappedIterator<>(people.iterator(), SimplifiedBirthSourceRecord::new);
    }

    @Override
    protected Iterable<SourceRecord> toDeathRecords(Iterable<IPerson> people) {
        return () -> new MappedIterator<>(people.iterator(), SimplifiedDeathSourceRecord::new);
    }

    @Override
    protected Iterable<SourceRecord> toMarriageRecords(Iterable<IPartnership> partnerships) {
        return () -> new MappedIterator<>(partnerships.iterator(), SimplifiedMarriageSourceRecord::new);
    }
}

