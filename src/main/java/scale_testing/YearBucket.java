package scale_testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class YearBucket {

    public YearBucket() {

        for(int i = 0; i < 5; i++) {
            malesbyNumberChildren.put(i, new ChildrenBucket());
        }

    }

    HashMap<Integer, ChildrenBucket> malesbyNumberChildren = new HashMap<Integer, ChildrenBucket>();
    HashMap<Integer, ChildrenBucket> femalesbyNumberChildren = new HashMap<Integer, ChildrenBucket>();


}
