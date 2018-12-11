package uk.ac.standrews.cs.valipop.utils.addressLookup;

import uk.ac.standrews.cs.valipop.utils.addressLookup.Area;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AreaSet {

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
