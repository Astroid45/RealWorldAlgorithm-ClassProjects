import bridges.base.SymbolCollection;
import bridges.base.Rectangle;
import bridges.base.Circle;
import bridges.base.Polyline;
import bridges.base.Color;
import bridges.connect.*;
import java.util.*;
import bridges.data_src_dependent.City;

public class quad_tree {
    static SymbolCollection sc;

    public static void main(String[] args) {
        String username = Credential.readUsername();
        String apiKey = Credential.readApiKey();
        Bridges bridges = new Bridges(16, username, apiKey);
        bridges.setTitle("Quad Tree");

        try {
            DataSource ds = bridges.getDataSource();
            HashMap<String, String> params = new HashMap<>();
            params.put("country", "US");
            params.put("min_pop", "10000");
            Vector<City> cities = ds.getUSCitiesData(params);

            // Scale the lat/long values
            double scaleFactor = 1.0;
            // Collections.sort(cities, Comparator.comparing(City::getCity));
            for (City city : cities) {
                city.setLatitude((float) (city.getLatitude() * scaleFactor));
                city.setLongitude((float) (city.getLongitude() * scaleFactor));
                // System.out.println(city.getCity() + ": (" + city.getLatitude() + ", " + city.getLongitude() + ")");
            }

            // Compute bounding box
            // Compute bounding box
            float xmin = Float.POSITIVE_INFINITY;
            float xmax = Float.NEGATIVE_INFINITY;
            float ymin = Float.POSITIVE_INFINITY;
            float ymax = Float.NEGATIVE_INFINITY;
            for (City city : cities) {
                float longitude = city.getLongitude();
                float latitude = city.getLatitude();

                // Exclude outlier longitude values
                if (longitude >= -180 && longitude <= 180) {
                    xmin = Math.min(xmin, longitude);
                    xmax = Math.max(xmax, longitude);
                }

                // Exclude outlier latitude values
                if (latitude >= -90 && latitude <= 90) {
                    ymin = Math.min(ymin, latitude);
                    ymax = Math.max(ymax, latitude);
                }
            }

            // Set viewport for symbol collection
            System.out.println("Bounding box: xMin=" + xmin + ", xMax=" + xmax + ", yMin=" + ymin + ", yMax=" + ymax);
            sc = new SymbolCollection();
            sc.setViewport(xmin, xmax, ymin, ymax);

            // Create a separate SymbolCollection for lines
            SymbolCollection lineCollection = new SymbolCollection();
            lineCollection.setViewport(xmin, xmax, ymin, ymax);

            // Construct quadtree
            QuadTreeNode quadtree = constructQuadTree(cities.toArray(new City[cities.size()]), xmin, xmax, ymin, ymax,
                    1, lineCollection);

            // Add the line collection to the Bridges visualization
            bridges.setDataStructure(lineCollection);

            // Search for cities
            String[] citiesToSearch = { "Charlotte", "Centralia",  "Los Angeles" };
            ArrayList<Color> colorPalette = generateColorPalette();
            for (String city : citiesToSearch) {
                City cityPoint = getCityByName(cities, city);
                if (cityPoint != null) {
                    ArrayList<Polyline> searchedPolylines = new ArrayList<>();
                    boolean found = searchQuadTree(quadtree, cityPoint.getLatitude(), cityPoint.getLongitude(),
                            searchedPolylines, colorPalette);
                    if (found) {
                        System.out.println("Found " + city + " at (" + cityPoint.getLatitude() + ", "
                                + cityPoint.getLongitude() + ")");
                    } else {
                        System.out.println(city + " not found");
                    }
                } else {
                    System.out.println(city + " not found");
                }
            }

            // Set visualizer type
            bridges.setDataStructure(sc);
            bridges.setMap("us", "all");
            bridges.setMapOverlay(true);
            // Visualize
            bridges.visualize();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }

    public static QuadTreeNode constructQuadTree(City[] cities, float xMin, float xMax, float yMin, float yMax,
            int threshold, SymbolCollection lineCollection) {

            // System.out.println("Constructing quadtree for range: xMin=" + xMin + ", xMax=" + xMax + ", yMin=" + yMin
            //     + ", yMax=" + yMax);

            // Check if we have fewer cities than the threshold
            if (cities.length <= threshold) {
                QuadTreeNode node = new QuadTreeNode(xMin, xMax, yMin, yMax);
                // Add all cities to the node and return
                for (City city : cities) {
                    Circle circle = new Circle(city.getLongitude(), city.getLatitude(), 0.1f);
                    circle.setFillColor("red");
                    sc.addSymbol(circle);
                    node.points.add(city);
                }
                // System.out.println("Reached threshold, returning leaf node with " + node.points.size() + " cities");
                return node;
            }

            // Calculate partition coordinates
            float partitionX = (xMin + xMax) / 2;
            float partitionY = (yMin + yMax) / 2;
            // System.out.println("PartitionX: " + partitionX + ", PartitionY: " + partitionY);

            // Initialize lists for each quadrant
            ArrayList<City> nwPoints = new ArrayList<>();
            ArrayList<City> nePoints = new ArrayList<>();
            ArrayList<City> swPoints = new ArrayList<>();
            ArrayList<City> sePoints = new ArrayList<>();

            // Divide cities into quadrants
            for (City city : cities) {
                if (city.getLatitude() <= partitionY && city.getLongitude() <= partitionX) {
                    nwPoints.add(city);
                } else if (city.getLatitude() <= partitionY && city.getLongitude() > partitionX) {
                    nePoints.add(city);
                } else if (city.getLatitude() > partitionY && city.getLongitude() <= partitionX) {
                    swPoints.add(city);
                } else {
                    sePoints.add(city);
                }
                // System.out.println("nwPoints: " + nwPoints.size() + ", nePoints: " + nePoints.size() + ", swPoints: "
                //         + swPoints.size() + ", sePoints: " + sePoints.size());
            }
            

            // Add symbols for partition lines
            Polyline verticalLine = new Polyline();
            verticalLine.addPoint(partitionX, yMin);
            verticalLine.addPoint(partitionX, yMax);
            lineCollection.addSymbol(verticalLine);

            Polyline horizontalLine = new Polyline();
            horizontalLine.addPoint(xMin, partitionY);
            horizontalLine.addPoint(xMax, partitionY);
            lineCollection.addSymbol(horizontalLine);

            // System.out.println("Vertical line: " + verticalLine + ", Horizontal line: " + horizontalLine);

            // Recursively construct child nodes
            QuadTreeNode node = new QuadTreeNode(xMin, xMax, yMin, yMax);
            node.children[0] = constructQuadTree(nwPoints.toArray(new City[0]), xMin, partitionX, yMin, partitionY,
                    threshold, lineCollection);
            node.children[1] = constructQuadTree(nePoints.toArray(new City[0]), partitionX, xMax, yMin, partitionY,
                    threshold, lineCollection);
            node.children[2] = constructQuadTree(swPoints.toArray(new City[0]), xMin, partitionX, partitionY, yMax,
                    threshold, lineCollection);
            node.children[3] = constructQuadTree(sePoints.toArray(new City[0]), partitionX, xMax, partitionY, yMax,
                    threshold, lineCollection);
            
            // System.out.println("Children nodes: " + Arrays.toString(node.children));
            return node;
    }

    public static boolean searchQuadTree(QuadTreeNode node, float x, float y, ArrayList<Polyline> searchedPolylines,
            ArrayList<Color> colorPalette) {
        System.out.println("Searching for point at (" + x + ", " + y + ") in node: " + node);
        System.out.println("Node boundaries: xMin=" + node.xMin + ", xMax=" + node.xMax + ", yMin=" + node.yMin
                + ", yMax=" + node.yMax);

        if (node == null) {
            System.out.println("Node is null.");
            return false;
        }

        if (node.xMin <= x && x <= node.xMax && node.yMin <= y && y <= node.yMax) {
            System.out.println("Point (" + x + ", " + y + ") is within node boundaries.");

            Circle circle = new Circle(x, y, 10.0f);
            circle.setFillColor("red");
            sc.addSymbol(circle);

            if (!node.points.isEmpty()) {
                System.out.println("Point found in node: " + node);
                return true;
            }

            System.out.println("Adding plot lines to node: " + node);
            
        }
        

        System.out.println("Point (" + x + ", " + y + ") is not within node boundaries.");
        return false;
    }

    public static Color getNextColor(ArrayList<Color> colorPalette, int index) {
        return colorPalette.get(index % colorPalette.size());
    }

    public static ArrayList<Color> generateColorPalette() {
        ArrayList<Color> colorPalette = new ArrayList<>();
        colorPalette.add(new Color("red"));
        colorPalette.add(new Color("green"));
        colorPalette.add(new Color("blue"));
        colorPalette.add(new Color("yellow"));
        colorPalette.add(new Color("cyan"));
        colorPalette.add(new Color("magenta"));
        colorPalette.add(new Color("orange"));
        colorPalette.add(new Color("purple"));
        return colorPalette;
    }

    public static City getCityByName(Vector<City> cities, String cityName) {
        System.out.println("Searching for city: " + cityName);
        for (City city : cities) {
            System.out.println("Comparing with city: " + city.getCity());
            if (city.getCity().equals(cityName)) {
                System.out.println("City found: " + cityName);
                return city;
            }
        }
        System.out.println("City not found: " + cityName);
        return null;
    }

    static class QuadTreeNode {
        float xMin, xMax, yMin, yMax;
        float partitionX, partitionY;
        ArrayList<City> points;
        QuadTreeNode[] children;

        public QuadTreeNode(float xMin, float xMax, float yMin, float yMax) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            this.partitionX = (xMin + xMax) / 2;
            this.partitionY = (yMin + yMax) / 2;
            points = new ArrayList<>();
            children = new QuadTreeNode[4];
        }

    }
}
