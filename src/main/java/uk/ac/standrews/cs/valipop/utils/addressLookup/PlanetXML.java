package uk.ac.standrews.cs.valipop.utils.addressLookup;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PlanetXML {

    static Map<String, Coords> nodeMap = new HashMap<>();

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, InvalidCoordSet, InterruptedException {

        Document document = readInDocument("src/main/resources/valipop/geography-cache/scotland-latest.osm");

        populateNodeMap(document);

        String file = "src/main/resources/valipop/geography-cache/scotland-residential-ways.ser";

        Cache cache = new Cache();
        try {
            cache = Cache.readFromFile(file);
        } catch (ClassNotFoundException e) {
            System.err.println("Cache class not found in serial input file");
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println("No cache found at file path - proceeding to construct new cache");
        } catch (NotSerializableException e) {
            System.err.println("Cache malformed? If from previous error then please delete: " + file);
            System.exit(1);
        }

        ReverseGeocodeLookup rgl = new ReverseGeocodeLookup(cache);

        addResidentialWaysToCache(document, rgl);

        cache.writeToFile();


    }

    private static void addResidentialWaysToCache(Document document, ReverseGeocodeLookup rgl) throws InterruptedException, InvalidCoordSet, IOException {

        NodeList wayList = document.getElementsByTagName("way");

        for(int i = 0; i < wayList.getLength(); i++) {

            if(i != 0 && i % 1000 == 0) {
                rgl.cache.writeToFile();
            }

            Element way = getElement(wayList, i);

            if(way != null) {

                NodeList ownedNodes = way.getChildNodes();
                boolean residentialWay = false;
                ArrayList<Coords> nodes = new ArrayList<>();

                for(int j = 0 ; j < ownedNodes.getLength(); j++) {

                    Element node = getElement(ownedNodes, j);

                    if(node != null) {

                        String nodeID = node.getAttribute("ref");


                        if(nodeID.equals("")) {
                            String key = node.getAttribute("k");
                            if(key.equals("highway")) {
                                String value = node.getAttribute("v");
                                residentialWay = residentialWay || value.equals("residential");
                            }


                        } else {
                            Coords c = nodeMap.get(nodeID);

                            if (c != null) {
                                nodes.add(c);
                            } else {
                                System.out.println(nodeID);
                            }
                        }

                    }
                }

                if(residentialWay) {
                    Coords middle = getMiddleElement(nodes);

                    if(middle != null) {
                        rgl.getArea(middle);
                    }
                }

            }

        }
    }

    private static void populateNodeMap(Document document) {
        NodeList nodeList = document.getElementsByTagName("node");

        for(int n = 0; n < nodeList.getLength(); n++) {

            Element element = getElement(nodeList, n);

            if(element != null) {

                String id = element.getAttribute("id");
                String lat = element.getAttribute("lat");
                String lon = element.getAttribute("lon");

                Coords coords = new Coords(lat, lon);

                nodeMap.put(id, coords);

            }

        }
    }

    private static Document readInDocument(String pathToDocument) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new File( pathToDocument ));
    }

    private static Coords getMiddleElement(ArrayList<Coords> nodes) {

        return nodes.size() != 0 ? nodes.get(nodes.size() / 2) : null;

    }

    public static Element getElement(NodeList nodeList, int index) {

        Node node = nodeList.item(index);

        if(node.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) node;
        }

        return null;

    }

}
