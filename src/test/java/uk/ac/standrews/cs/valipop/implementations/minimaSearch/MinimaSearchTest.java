package uk.ac.standrews.cs.valipop.implementations.minimaSearch;

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.valipop.Config;
import uk.ac.standrews.cs.valipop.implementations.OBDModel;
import uk.ac.standrews.cs.valipop.implementations.SpaceExploredException;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.RecordFormat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MinimaSearchTest {

    OBDModel model;

    @Before
    public void setup() throws IOException {

        Config config = new Config(LocalDate.of(1,1,1), LocalDate.of(100,1,1),
                LocalDate.of(200,1,1), 0, 0, 0, Period.ofYears(1),
                "src/test/resources/valipop/test-pop", "", "",
                0, 0, true, 0, 0, 0,
                0, Period.ofYears(1), RecordFormat.NONE, null, 0, true);

        OBDModel.setUpFileStructureAndLogs("testing", "test-time", "results");

        model = new OBDModel("", config);
    }

    @Test
    public void nanTesting() throws SpaceExploredException {

        double startingFactor = 0.0;

        MinimaSearch.startFactor = startingFactor;
        MinimaSearch.step = 0.5;
        MinimaSearch.initStep = 0.5;

        Control control = Control.BF;

        MinimaSearch.setControllingFactor(control, MinimaSearch.startFactor);
        double bf = MinimaSearch.getControllingFactor(control);

        assertEquals(bf, startingFactor, 1E-6);

        MinimaSearch.setControllingFactor(control, MinimaSearch.getNextFactorValue());
        bf = MinimaSearch.getControllingFactor(control);
        assertEquals(startingFactor, bf, 1E-6);

        MinimaSearch.logFactortoV(bf, 0.2078297837489273);

        MinimaSearch.setControllingFactor(control, MinimaSearch.getNextFactorValue());
        bf = MinimaSearch.getControllingFactor(control);
        assertEquals(startingFactor + 0.5, bf, 1E-6);
    }
}
