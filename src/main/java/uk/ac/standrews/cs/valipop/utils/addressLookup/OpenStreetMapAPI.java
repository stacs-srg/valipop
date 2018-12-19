package uk.ac.standrews.cs.valipop.utils.addressLookup;

import uk.ac.standrews.cs.valipop.utils.addressLookup.Area;
import uk.ac.standrews.cs.valipop.utils.addressLookup.InvalidCoordSet;
import uk.ac.standrews.cs.valipop.utils.addressLookup.Place;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OpenStreetMapAPI {

    public static long lastAPIRequestTime = System.currentTimeMillis();
    public static long requestGapMillis = 1000;

    public static void rateLimiter() throws InterruptedException {

        long wait = requestGapMillis - (System.currentTimeMillis() - lastAPIRequestTime);

        if(wait > 0) {
            Thread.sleep(wait);
        }

        lastAPIRequestTime = System.currentTimeMillis();

    }

    public static Area getAreaFromAPI(double lat, double lon, Cache cache) throws IOException, InvalidCoordSet, InterruptedException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("format", "json");
        parameters.put("lat", String.valueOf(lat));
        parameters.put("lon", String.valueOf(lon));
        parameters.put("zoom", "16");

        URL url = new URL("https://nominatim.openstreetmap.org/reverse.php?" + getParamsString(parameters));

        StringBuffer content = callAPI(url);

        return Area.makeArea(content.toString(), cache);

    }

    public static Place getPlaceFromAPI(long placeId) throws IOException, InterruptedException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("format", "json");
        parameters.put("place_id", String.valueOf(placeId));

        URL url = new URL("https://nominatim.openstreetmap.org/details.php?" + getParamsString(parameters));

        StringBuffer content = callAPI(url);

        return Place.makePlace(content.toString());

    }

    private static StringBuffer callAPI(URL url) throws IOException, InterruptedException {

        rateLimiter();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(30000);
        con.setReadTimeout(30000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");

        System.out.println(con.toString());

        int status = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        con.disconnect();
        return content;
    }


    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

}
