import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import bridges.base.*;
import bridges.connect.Bridges;

public class Floyd {
    public static void main(String[] args) {
        String username = Credential.readUsername();
        String apiKey = Credential.readApiKey();
        Bridges bridges = new Bridges(18, username, apiKey);
        bridges.setTitle("Floyd's Shortest Path US");

        String file = "graph_us_cities.txt";
        Path path = Paths.get(file);
        Path absolutePath = path.toAbsolutePath();
        try {
            InputStream inputStream = Files.newInputStream(absolutePath);
            if (inputStream == null) {
                throw new FileNotFoundException("File not found: graph_us_cities.txt");
            }

            GraphAdjList<String, String, Double> graph = createGraphFromInputStream(inputStream);

            Map<String, Map<String, List<String>>> shortestPaths = floydWarshall(graph);

            visualizeShortestPaths(bridges, graph, shortestPaths, "Charlotte_NC", "Seattle_WA", "Los_Angeles_CA", "Portland_ME");
        } catch (FileNotFoundException e) { // Properly handle FileNotFoundException
            System.err.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }
    }

    static GraphAdjList<String, String, Double> createGraphFromInputStream(InputStream inputStream) throws IOException {
        GraphAdjList<String, String, Double> graph = new GraphAdjList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        boolean edgesSection = false; // Flag to indicate if we're reading edges
        // Read vertices and add them to the graph
        while ((line = reader.readLine()) != null) {
            if (line.equals("Edges: 220")) {
                edgesSection = true;
                continue; 
            }
            if (!edgesSection) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    String city = parts[1].trim();
                    graph.addVertex(city, city);
                    try {
                        double latitude = Double.parseDouble(parts[2].trim());
                        double longitude = Double.parseDouble(parts[3].trim());
                        graph.getVisualizer(city).setLocation(longitude, latitude);
                        graph.getVisualizer(city).setSize(1.0f);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                } else {
                    System.err.println("Starting line: " + line);
                }

            } else { // Parsing edges
                String[] parts = line.split("\\s+"); // Split by whitespace
                if (parts.length >= 3) { // Ensure the edge line contains at least 3 values
                    String src = parts[1].trim();
                    String dest = parts[2].trim();
                    double distance = Double.parseDouble(parts[3].trim());
                    graph.addEdge(src, dest, distance);
                    String distance2 = distance + "";
                    graph.getLinkVisualizer(src, dest).setLabel(distance2);
                    graph.getLinkVisualizer(src, dest).setThickness(1.0f);
                } else {
                    System.err.println("Starting line: " + line);
                }
            }
        }
        reader.close();
        return graph;
    }

    static Map<String, Map<String, List<String>>> floydWarshall(GraphAdjList<String, String, Double> graph) {
        Map<String, Map<String, List<String>>> shortestPaths = initializeShortestPaths(graph, "Charlotte_NC");
        Set<String> vertices = graph.getVertices().keySet();

        for (String k : vertices) {
            for (String i : vertices) {
                for (String j : vertices) {
                    List<String> pathIK = shortestPaths.get(i).get(k);
                    List<String> pathKJ = shortestPaths.get(k).get(j);
                    if (pathIK != null && pathKJ != null) {
                        // Check if there's an edge from vertex i to vertex j via vertex k
                        boolean edgeExists = false;
                        for (Edge<String, Double> edge : graph.outgoingEdgeSetOf(k)) {
                            if (edge.getFrom().equals(i) && edge.getTo().equals(j)) {
                                edgeExists = true;
                                break;
                            }
                        }
                        if (edgeExists) {
                            // If the new path via k is shorter, update the shortest path
                            if (pathIK.size() + pathKJ.size() - 1 < shortestPaths.get(i).get(j).size()) {
                                List<String> newPath = new ArrayList<>(pathIK);
                                newPath.addAll(pathKJ.subList(1, pathKJ.size()));
                                shortestPaths.get(i).put(j, newPath);
                            }
                        }
                    }
                }
            }
        }
        return shortestPaths;
    }

    static double getDistance(GraphAdjList<String, String, Double> graph, List<String> path) {
        double distance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            Double edgeWeight = graph.getEdgeData(current, next);
            if (edgeWeight != null) {
                distance += edgeWeight;
            }
        }
        return distance;
    }

    static Map<String, Map<String, List<String>>> initializeShortestPaths(GraphAdjList<String, String, Double> graph,
            String startingCity) {
        Map<String, Map<String, List<String>>> shortestPaths = new HashMap<>();
        Set<String> vertices = graph.getVertices().keySet();

        for (String vertex1 : vertices) {
            shortestPaths.put(vertex1, new HashMap<>());
            for (String vertex2 : vertices) {
                if (vertex1.equals(vertex2)) {
                    shortestPaths.get(vertex1).put(vertex2, new ArrayList<>(Arrays.asList(vertex1)));
                } else {
                    shortestPaths.get(vertex1).put(vertex2, null);
                }
            }
        }
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(startingCity);
        visited.add(startingCity);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (Edge<String, Double> edge : graph.outgoingEdgeSetOf(current)) {
                String neighbor = edge.getTo();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    List<String> path = new ArrayList<>(shortestPaths.get(startingCity).get(current));
                    path.add(neighbor);
                    shortestPaths.get(startingCity).put(neighbor, path);
                }
            }
        }

        return shortestPaths;
    }

    static void visualizeShortestPaths(Bridges bridges, GraphAdjList<String, String, Double> graph,
            Map<String, Map<String, List<String>>> shortestPaths, String startingCity,
            String... targetCities) {
        bridges.setCoordSystemType("albersusa");
        bridges.setDataStructure(graph);
        bridges.setMap("us", "all");

        for (String target : targetCities) {
            List<String> path = shortestPaths.get(startingCity).get(target);
            if (path != null) {
                System.out.println("Shortest path from " + startingCity + " to " + target + ": " + path);
                for (int i = 0; i < path.size() - 1; i++) {
                    String current = path.get(i);
                    String next = path.get(i + 1);
                    LinkVisualizer visualizer = graph.getLinkVisualizer(current, next);
                    if (visualizer != null) {
                        visualizer.setColor("red");
                        visualizer.setThickness(2.0f);
                        graph.getVisualizer(current).setSize(3.0f);
                        graph.getVisualizer(next).setSize(3.0f);
                        graph.getVisualizer(current).setColor("red");
                        graph.getVisualizer(next).setColor("red");
                    }
                }
                graph.getVisualizer(startingCity).setSize(10.0f);
                graph.getVisualizer(startingCity).setColor("blue");
                double totalDistance = calculateDistance(graph, path);
                graph.getVertex(target).setLabel(startingCity + " to " + target + ": " + Double.toString(totalDistance));
                graph.getVisualizer(target).setSize(10.0f);
                graph.getVisualizer(target).setColor("green");
            } else {
                System.out.println("No path found from " + startingCity + " to " + target);
            }
        }

        try {
            bridges.setMapOverlay(true);
            bridges.visualize();
        } catch (Exception e) {
            System.err.println("Exception during visualization: " + e.getMessage());
        }
    }

    static double getDist(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        final double phi1 = Math.toRadians(lat1);
        final double phi2 = Math.toRadians(lat2);
        final double delPhi = Math.toRadians((lat2 - lat1));
        final double delLambda = Math.toRadians((lon2 - lon1));
        final double a = Math.sin(delPhi / 2) * Math.sin(delPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                        * Math.sin(delLambda / 2) * Math.sin(delLambda / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    static double calculateDistance(GraphAdjList<String, String, Double> graph, List<String> path) {
        double distance = 0.0;
        List<Double> edgeWeights = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            Double edgeWeight = graph.getEdgeData(current, next);
            if (edgeWeight != null) {
                edgeWeights.add(edgeWeight);
                distance += edgeWeight;
            } else {
            }
        }
        System.out.println("Edge Weights: " + edgeWeights);
        return distance;
    }

}
