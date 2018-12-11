package uk.ac.standrews.cs.valipop.utils.addressLookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Place {

    private static ObjectMapper mapper = new ObjectMapper();


    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonProperty("osm_type")
    private String osm_type;

    private Coords centroid;

    @JsonProperty("centroid")
    private void unpackNestedCentroid(Map<String, Object> centroid) {
        ArrayList<? extends Number> cen = (ArrayList<? extends Number>) centroid.get("coordinates");
        this.centroid = new Coords(cen.get(1).doubleValue(), cen.get(0).doubleValue());
    }


    @JsonProperty("osm_id")
    private long osmWayID;

    public static Place makePlace(String jsonInput) throws IOException {
        Place place = mapper.readValue(jsonInput, Place.class);
        return place;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public Coords getCentroid() {
        return centroid;
    }

    public long getOsmWayID() {
        return osmWayID;
    }

    public boolean isWay() {
        return osm_type.toLowerCase().equals("w");
    }

}
