package uk.ac.standrews.cs.valipop.utils.sourceEventRecords;

import uk.ac.standrews.cs.utilities.MappedIterator;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat.EGSkyeBirthSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat.EGSkyeDeathSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat.EGSkyeMarriageSourceRecord;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.SourceRecord;

public class EgSkyeRecord extends Record {

    EgSkyeRecord(Iterable<IPerson> people, Iterable<IPartnership> partneships) {
      super(people, partneships);
    }

    @Override
    protected Iterable<SourceRecord> toBirthRecords(Iterable<IPerson> people) {
        return () -> new MappedIterator<>(people.iterator(), EGSkyeBirthSourceRecord::new);
    }

    @Override
    protected Iterable<SourceRecord> toDeathRecords(Iterable<IPerson> people) {
        return () -> new MappedIterator<>(people.iterator(), EGSkyeDeathSourceRecord::new);
    }

    @Override
    protected Iterable<SourceRecord> toMarriageRecords(Iterable<IPartnership> partnerships) {
        return () -> new MappedIterator<>(partnerships.iterator(), EGSkyeMarriageSourceRecord::new);
    }
}
