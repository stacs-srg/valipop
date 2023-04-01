package uk.ac.standrews.cs.valipop.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Area;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Cache;

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
        convert("/Users/tdalton/tom/phd/repos/population-model/src/main/resources/valipop/inputs/scotland_test_population_no_mig/annotations/geography/scotland-residential-ways.ser",
                "/Users/tdalton/tom/phd/repos/population-model/src/main/resources/valipop/inputs/scotland_test_population/annotations/geography/scotland-residential-ways.json");
    }

}
