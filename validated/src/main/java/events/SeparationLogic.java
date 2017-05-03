package events;

import simulationEntities.person.IPerson;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SeparationLogic {
    public static void handle(Map<Integer, ArrayList<IPerson>> continuingPartnedFemalesByChildren) {

        for(Integer i : continuingPartnedFemalesByChildren.keySet()) {
            for(IPerson p : continuingPartnedFemalesByChildren.get(i)) {
                p.willSeparate(false);
            }
        }
    }
}
