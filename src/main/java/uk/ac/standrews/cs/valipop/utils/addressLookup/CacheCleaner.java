package uk.ac.standrews.cs.valipop.utils.addressLookup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CacheCleaner {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String file = "src/main/resources/valipop/geography-cache/scotland-residential-ways.ser";

        Cache cache = Cache.readFromFile(file);

        for(Area area : cache.getAllAreas()) {
            System.out.println(area.toString());
        }

//        removeNullAddressAreas(cache);

//        cache.writeToFile();


    }

    private static void removeNonResidentialAreas(Cache cache) {

        System.out.println(cache.size());

        Collection<Area> toRemove = new ArrayList<>();

        for(Area area : cache.getAllAreas()) {
            if(!area.isResidential()) {
                toRemove.add(area);
                System.out.println(area.toString());

            }
        }

        System.out.println("-------");

        for(Area area : toRemove) {
            cache.removeArea(area);
            System.out.println(cache.size());
        }

    }

    private static void removeNonScotlandAreas(Cache cache) {

        System.out.println(cache.size());

        Collection<Area> toRemove = new ArrayList<>();

        for(Area area : cache.getAllAreas()) {
            if(!area.getState().equals("Scotland")) {
                toRemove.add(area);
                System.out.println(area.toString());
            }
        }

        System.out.println("-------");

        for(Area area : toRemove) {
            cache.removeArea(area);
            System.out.println(cache.size());
        }

    }

    private static void removeNullAddressAreas(Cache cache) {

        System.out.println(cache.size());

        Collection<Area> toRemove = new ArrayList<>();

        for(Area area : cache.getAllAreas()) {

            int count = 0;

            if(area.getRoad() == null)
                count++;

            if(area.getSuburb() == null)
                count++;

            if(count == 2)
                toRemove.add(area);

        }

        System.out.println(toRemove.size());



        for(Area area : toRemove) {
            cache.removeArea(area);
//            System.out.println(area.toString());
        }

        System.out.println(cache.size());

    }

}
