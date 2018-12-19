package uk.ac.standrews.cs.valipop.utils.addressLookup;

import uk.ac.standrews.cs.valipop.utils.addressLookup.Area;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AreaSet implements Serializable {

    private static final long serialVersionUID = 76428763786238422L;

    private ArrayList<Area> areas = new ArrayList<>();

    private long uptoNumber = 0;

    public AreaSet(Area firstArea) {
        areas.add(firstArea);
        uptoNumber += firstArea.getMaximumNumberOfAbodes();
    }

    public long addArea(Area area) {
        areas.add(area);
        long offset = uptoNumber;
        uptoNumber += area.getMaximumNumberOfAbodes();
        return offset;
    }

}
