package uk.ac.standrews.cs.valipop.utils.addressLookup;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Cache implements Serializable {

    private static final long serialVersionUID = 748931747946237593L;

    ArrayList<Area> areaDB = new ArrayList<>();

    private Map<Double, Map<Double, Area>> lookupHistory = new HashMap<>();

    Map<String, AreaSet> areaSets = new HashMap<>();

    long nextErrorID = -1;

    Map<Long, Area> areaIndex = new HashMap<>();

    private final int HISTORY_PRECISION = 4;
    private final double PRECISION_ADJUSTMENT = Math.pow(10, HISTORY_PRECISION);

    private transient String filePath;

    public Area checkCache(double lat, double lon) {

        // Checks that given coords don't correspond to a bounding box already retrieved
        Map<Double, Area> index = lookupHistory.get(round(lat));
        if(index != null) {
            return index.get(round(lon));
        }

        for(Area area : areaDB) {
            if(area.containsPoint(lat, lon)) {
                // Here we are just taking the first one we find - may want to change this later
                // (Bounding boxes can overlap due to their definition)
                return area;
            }
        }

        return null;
    }

    public void addHistory(double lat, double lon, Area area) {
        Map<Double, Area> index = lookupHistory.get(round(lat));

        if(index == null) {
            index = new HashMap<>();
            lookupHistory.put(round(lat), index);
        }

        index.put(round(lon), area);
    }

    private Double round(double d) {
        return Math.round(d * PRECISION_ADJUSTMENT) / PRECISION_ADJUSTMENT;
    }



    public void writeToFile() throws IOException {
        writeToFile(filePath);
    }

    public void writeToFile(String fileName) throws IOException {

        FileOutputStream fileOut = new FileOutputStream(fileName);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(this);
        objectOut.flush();
        objectOut.close();

    }

    public static Cache readFromFile(String fileName) throws IOException, ClassNotFoundException {

        FileInputStream fileIn = new FileInputStream(fileName);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);

        Cache cache = (Cache) objectIn.readObject();
        objectIn.close();

        cache.setFilePath(fileName);

        return cache;
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
