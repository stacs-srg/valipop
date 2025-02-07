package uk.ac.standrews.cs.valipop.utils.sourceEventRecords;

import uk.ac.standrews.cs.utilities.MappedIterator;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.tdFormat.TDBirthSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.tdFormat.TDDeathSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.tdFormat.TDMarriageSourceRecord;

public class TDRecord extends Record {

    TDRecord(Iterable<IPerson> people, Iterable<IPartnership> partneships) {
      super(people, partneships);
    }

    @Override
    protected Iterable<SourceRecord> toBirthRecords(Iterable<IPerson> people) {
        return () -> new MappedIterator<>(people.iterator(), TDBirthSourceRecord::new);
    }

    @Override
    protected Iterable<SourceRecord> toDeathRecords(Iterable<IPerson> people) {
        return () -> new MappedIterator<>(people.iterator(), TDDeathSourceRecord::new);
    }

    @Override
    protected Iterable<SourceRecord> toMarriageRecords(Iterable<IPartnership> partnerships) {
        return () -> new MappedIterator<>(partnerships.iterator(), TDMarriageSourceRecord::new);
    }
}
