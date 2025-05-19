package uk.ac.standrews.cs.valipop.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Area;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Cache;

/**
 * Used to convert a custom geography files (.ser) into JSON. May be executed standalone.
 */
public class GeogFileConverter {

    public static void convert(String source, String target) throws IOException, ClassNotFoundException {

        Cache geography = Cache.readFromFile(source);

        new ObjectMapper().writer().writeValue(new File(target),
                                               geography.getAllAreas().stream()
                                                       .filter(GeogFileConverter::isDataComplete)
                                                       .collect(Collectors.toList()));
    }

    private static boolean isDataComplete(Area area) {
        return !(area.getRoad() == null || area.getSuburb() == null && area.getTown() == null && area.getCounty() == null);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        convert("valipop/src/main/resources/valipop/inputs/scotland_test_population_no_mig/annotations/geography/scotland-residential-ways.ser",
                "valipop/src/main/resources/valipop/inputs/scotland_test_population/annotations/geography/scotland-residential-ways.json");
    }

}
