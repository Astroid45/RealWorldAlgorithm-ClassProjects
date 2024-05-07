import bridges.base.GraphAdjList;
import bridges.base.LinkVisualizer;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.data_src_dependent.City;
import java.io.IOException;
import java.util.*;

public class TSP {
    public static void main(String[] args) {
        String username = Credential.readUsername();
        String apiKey = Credential.readApiKey();
        Bridges bridges = new Bridges(20, username, apiKey);
        bridges.setTitle("Traveling Salesman Problem - US Cities");
        bridges.setDescription(
                "For this code I made three graphs which increase the population for each iteration, while still maintaining the TSP. I found the shortest path between all the vertices within the population using an MST as well as a nearest neighbor approach. Additionally I then retraced the same path it took from the ending vertex back to the starting vertex. My starting population is 600,000 and my ending population is 800,000.");
        try {
            DataSource ds = bridges.getDataSource();
            HashMap<String, String> map = new HashMap<>();
            map.put("country", "US");
            int minPopulation = 600000;

            int populationIncrement = 100000;
            int totalPopulation = 0;

            for (int i = 1; i <= 3; i++) {
                totalPopulation += populationIncrement;
                map.put("min_pop", String.valueOf(minPopulation));
                List<City> cities = ds.getUSCitiesData(map);
                List<String> shortestTour = findShortestTour(cities);
                GraphAdjList<String, String, Double> graph = buildGraph(cities, shortestTour);
                visualizeShortestTour(bridges, graph, shortestTour, i, minPopulation, cities); // Pass minPopulation
                bridges.setCoordSystemType("albersusa");
                bridges.setMapOverlay(true);
                bridges.setMap("us", "all");
                bridges.visualize();
                Thread.sleep(2000);

                minPopulation += populationIncrement; 
            }
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }
    }

    static GraphAdjList<String, String, Double> buildGraph(List<City> cities, List<String> shortestTour) {
        GraphAdjList<String, String, Double> graph = new GraphAdjList<>();

        for (City city : cities) {
            String cityName = city.getCity();
            graph.addVertex(cityName, cityName);
            graph.getVisualizer(cityName).setLocation(city.getLongitude(), city.getLatitude());
        }

        for (int i = 0; i < shortestTour.size() - 1; i++) {
            String city1Name = shortestTour.get(i);
            String city2Name = shortestTour.get(i + 1);
            City city1 = findCityByName(cities, city1Name);
            City city2 = findCityByName(cities, city2Name);
            double distance = getDistance(city1, city2);
            graph.addEdge(city1Name, city2Name, distance);
            graph.getLinkVisualizer(city1Name, city2Name).setLabel(String.valueOf(distance));
        }

        return graph;
    }

    static City findCityByName(List<City> cities, String cityName) {
        for (City city : cities) {
            if (city.getCity().equals(cityName)) {
                return city;
            }
        }
        return null;
    }

    static double getDistance(City city1, City city2) {
        double lat1 = city1.getLatitude();
        double lon1 = city1.getLongitude();
        double lat2 = city2.getLatitude();
        double lon2 = city2.getLongitude();
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return Math.sqrt(Math.pow(distance, 2));
    }

    static List<String> findShortestTour(List<City> cities) {
        List<String> tour = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        String startCity = cities.get(0).getCity();
        tour.add(startCity);
        visited.add(startCity);

        while (visited.size() < cities.size()) {
            String currentCity = tour.get(tour.size() - 1);
            double minDistance = Double.MAX_VALUE;
            String nextCity = null;
            for (City city : cities) {
                if (!visited.contains(city.getCity())) {
                    double distance = getDistance(findCityByName(cities, currentCity), city);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextCity = city.getCity();
                    }
                }
            }
            if (nextCity != null) {
                tour.add(nextCity);
                visited.add(nextCity);
            } else {
                break;
            }
        }

        tour.add(startCity);

        return tour;
    }

    static void visualizeShortestTour(Bridges bridges, GraphAdjList<String, String, Double> graph, List<String> tour,
            int tourIndex, int minPopulation, List<City> cities) {
        System.out.println("Minimum Population for Case " + tourIndex + ": " + minPopulation);

        for (int i = 0; i < tour.size() - 1; i++) {
            String currentCity = tour.get(i);
            String nextCity = tour.get(i + 1);

            graph.addEdge(currentCity, nextCity,
                    getDistance(findCityByName(cities, currentCity), findCityByName(cities, nextCity)));
            LinkVisualizer forwardVisualizer = graph.getLinkVisualizer(currentCity, nextCity);
            if (forwardVisualizer != null) {
                if (tourIndex == 1) {
                    forwardVisualizer.setColor("orange");
                    forwardVisualizer.setOpacity(1.0f);
                } else if (tourIndex == 2) {
                    forwardVisualizer.setColor("purple");
                    forwardVisualizer.setOpacity(1.0f);
                } else if (tourIndex == 3) {
                    forwardVisualizer.setColor("cyan");
                    forwardVisualizer.setOpacity(1.0f);
                }
                forwardVisualizer.setThickness(2.0f);
                graph.getVisualizer(currentCity).setSize(3.0f);
                graph.getVisualizer(nextCity).setSize(3.0f);
            }

            // Reverse edge
            graph.addEdge(nextCity, currentCity,
                    getDistance(findCityByName(cities, nextCity), findCityByName(cities, currentCity)));
            LinkVisualizer reverseVisualizer = graph.getLinkVisualizer(nextCity, currentCity);
            if (reverseVisualizer != null) {
                reverseVisualizer.setColor("pink");
                reverseVisualizer.setOpacity(0.8f);
                reverseVisualizer.setThickness(1.0f);
            }
        }

        graph.getVisualizer(tour.get(0)).setSize(10.0f);
        graph.getVisualizer(tour.get(tour.size() - 1)).setSize(10.0f);
        graph.getVisualizer(tour.get(0)).setColor("blue");
        graph.getVisualizer(tour.get(tour.size() - 1)).setColor("blue");
        graph.getVisualizer(tour.get(0)).setShape("star");
        graph.getVisualizer(tour.get(tour.size() - 1)).setShape("star");

        bridges.setDataStructure(graph);
    }

}
