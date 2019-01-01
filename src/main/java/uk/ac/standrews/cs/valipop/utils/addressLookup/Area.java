package uk.ac.standrews.cs.valipop.utils.addressLookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Area implements Serializable {

    private static final long serialVersionUID = 8749827387328328989L;

    private static ObjectMapper mapper = new ObjectMapper();

    @JsonProperty("error")
    private String error = "none";

    @JsonProperty("place_id")
    private long placeId;

    private String road;
    private String suburb;
    private String town;
    private String county;
    private String state;
    private String postcode;

    @JsonProperty("address")
    private void unpackNestedAddress(Map<String, Object> address) {
        road = (String) address.get("road");
        suburb = (String) address.get("suburb");
        town = (String) address.get("town");
        county = (String) address.get("county");
        state = (String) address.get("state");
        postcode = (String) address.get("postcode");
    }

    @JsonProperty("boundingbox")
    private String[] boundingBoxString;

    private BoundingBox boundingBox;

    private Place details;

    // a street can be made up of many areas, the offset prevents each subpart having the same house numbers
    private long numberingOffset = 0;
    private long maximumNumberOfAbodes = 0;

    private transient ArrayList<Address> addresses = new ArrayList<>();

    public static Area makeArea(String jsonInput, Cache cache) throws IOException, InvalidCoordSet, InterruptedException, APIOverloadedException {
        Area area = mapper.readValue(jsonInput, Area.class);

        if(area.error.equals("none")) {

            // do we already have this area?
            Area a = cache.getAreaByID(area.placeId);
            if(a != null) {
                return a;
            }

            area.boundingBox = new BoundingBox(area.boundingBoxString);
            area.details = OpenStreetMapAPI.getPlaceFromAPI(area.placeId, cache);

            if (area.isResidential()) {
                area.maximumNumberOfAbodes = Math.round(ReverseGeocodeLookup.ABODES_PER_KM * GPSDistanceConverter.distance(area.boundingBox.getBottomLeft(), area.boundingBox.getTopRight(), 'K'));

                try {
                    String areaString = area.getAreaSetString();
                    AreaSet set = cache.getAreaSet(areaString);

                    if (set == null) {
                        cache.addAreaSet(areaString, new AreaSet(area));
                    } else {
                        area.numberingOffset = set.addArea(area);
                    }

                } catch (IncompleteAreaInformationException e) {
                    System.out.println("--- Area incomplete ---");
                    System.out.println(area.toString());
                }

            }

            cache.addToAreaIndex(area.placeId, area);

        } else {
            area.placeId = cache.decrementErrorID();
        }

        return area;
    }

    public String getAreaSetString() throws IncompleteAreaInformationException {
        String s = "";

        if(road == null || suburb == null && town == null && county == null) {
            throw new IncompleteAreaInformationException();
        }

        s += road;

        if(suburb != null)
            s += suburb;

        if(town != null)
            s += town;

        return s;

    }

    public Address getFreeAddress() {

        for(Address address : addresses) {
            if(!address.isInhabited()) {
                return address;
            }
        }

        if(addresses.size() < maximumNumberOfAbodes) {
            // +1 so that house numbers don't start at zero!
            Address newAddress = new Address(numberingOffset + addresses.size() + 1, this);
            addresses.add(newAddress);
            return newAddress;
        }

        return null;
    }

    public boolean containsPoint(double lat, double lon) {
        return boundingBox.containsPoint(lat, lon);
    }

    public boolean isResidential() {
        return details.getType().toLowerCase().equals("residential");
    }

    public long getMaximumNumberOfAbodes() {
        return maximumNumberOfAbodes;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("--- Area: " + placeId + " ---\n");

        if(!isErroneous()) {
            s.append(road + ", " + suburb + ", " + town + ", " + county + "\n");
            s.append(state + ", " + postcode + "\n");
            s.append("bl: " + boundingBox.getBottomLeft().toString() + "\n");
            s.append("tr: " + boundingBox.getTopRight().toString() + "\n");
            s.append("centroid: " + details.getCentroid().toString() + "\n");
            s.append("catergory: " + details.getCategory() + "\n");
            s.append("type: " + details.getType() + "\n");
            s.append("way id: " + details.getOsmWayID() + "\n");
            s.append("max abodes: " + maximumNumberOfAbodes + "\n");
        } else {
            s.append("ERRONEOUS AREA - " + error + "\n");
        }

        s.append("---  EOA: " + placeId + " ---\n");

        return s.toString();
    }

    public boolean isErroneous() {
        return !error.equals("none");
    }

    public boolean isWay() {
        return (details == null ? false : details.isWay());
    }

    public String getState() {
        return (state == null ? "" : state);
    }
}
