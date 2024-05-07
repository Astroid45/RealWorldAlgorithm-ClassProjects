import bridges.base.*;
import bridges.data_src_dependent.ActorMovieIMDB;
import bridges.connect.*;
import java.util.*;

public class ShortestPath {

    private GraphAdjListSimple<String> graph;
    private List<String> path;
    private List<Integer> distances;

    public ShortestPath(List<ActorMovieIMDB> data) { // This constructor method builds the graph
        this.graph = new GraphAdjListSimple<>(); // Calling the private instance variable

        Map<String, Boolean> vertexMap = new HashMap<>();

        for (ActorMovieIMDB item : data) {
            String actor = item.getActor();
            String movie = item.getMovie();

            if (!vertexMap.containsKey(actor)) {
                graph.addVertex(actor, actor);
                vertexMap.put(actor, true);
            }

            if (!vertexMap.containsKey(movie)) {
                graph.addVertex(movie, movie);
                vertexMap.put(movie, true);
            }

            graph.addEdge(actor, movie);
            graph.addEdge(movie, actor);
        }
    }

    public void findAndVisualizePath(String startActor, String targetActor) { // This function was used as check, responsible for printing path and checking if theres path
        bfs(startActor, targetActor);
        if (!path.isEmpty()) {
            System.out.println("Path from " + startActor + " to " + targetActor + ":");
            for (String actor : path) {
                System.out.println("   " + actor);
            }
            visualizeGraph(); 
        } else {
            System.out.println("No path found from " + startActor + " to " + targetActor);
        }
    }

    private void bfs(String startActor, String targetActor) { // Bfs for finding shortest path 
        Queue<String> queue = new LinkedList<>(); // Initiating all hash maps as well as queue
        Map<String, Integer> distanceMap = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();
        this.path = new ArrayList<>(); // Setting a new array list to print for both path and distance
        this.distances = new ArrayList<>();

        queue.add(startActor);
        distanceMap.put(startActor, 0);
        parentMap.put(startActor, null);

        while (!queue.isEmpty()) {
            String currentActor = queue.poll();

            if (currentActor.equals(targetActor)) { // This reconstructs the list for when target actor is found allowing me to print the path 
                reconstructPath(startActor, targetActor, parentMap, distanceMap);
                return;
            }

            for (Edge<String, String> edge : graph.outgoingEdgeSetOf(currentActor)) { // Loop is used for traversing the children nodes
                String neighbor = edge.getTo();
                if (!distanceMap.containsKey(neighbor)) { // This check adds the children to queue/distanceMap/parentMap if not already in distanceMap
                    queue.add(neighbor);
                    distanceMap.put(neighbor, distanceMap.get(currentActor) + 1);
                    parentMap.put(neighbor, currentActor);
                }
            }
        }
    }

    private void reconstructPath(String startActor, String targetActor, Map<String, String> parentMap,
            Map<String, Integer> distanceMap) { // This function is in charge of reconstructing the path, i.e reversing the list to print
        String currentActor = targetActor;

        while (currentActor != null) { // While loop adds the visited nodes to paths and distance, then sets current to next
            path.add(currentActor);
            distances.add(distanceMap.get(currentActor));
            currentActor = parentMap.get(currentActor);
        }

        Collections.reverse(path);
        Collections.reverse(distances);
    }

   
    private void visualizeGraph() { // Function is all in charge of the visualization of the graph
        for (int i = 0; i < path.size() - 1; i++) {
            String actor = path.get(i);
            String neighbor = path.get(i + 1);
            int distance = distances.get(i);
            Element element = graph.getVertex(actor);
            Element element2 = graph.getVertex(neighbor);

            graph.getLinkVisualizer(actor, neighbor).setColor("orange");
            graph.getLinkVisualizer(actor, neighbor).setThickness(5.0);
            graph.getVisualizer(actor).setColor("red");
            graph.getVisualizer(neighbor).setColor("red");
            graph.getVisualizer(actor).setSize(50.0);
            graph.getVisualizer(neighbor).setSize(50.0);

            if (i == 0) { // Sets the initial actor to cyan
                graph.getVisualizer(actor).setColor("cyan"); 
                graph.getVisualizer(neighbor).setSize(50.0);
            }
            element.setLabel(actor + " (" + distance + ")");
            element2.setLabel(neighbor + " (" + distance + ")");
        }
    }

    public static void main(String[] args) throws Exception {
        String username = Credential.readUsername();
        String apiKey = Credential.readApiKey();
        Bridges bridges = new Bridges(14, username, apiKey);
        bridges.setTitle("BFS Bacon Number Graph");
        bridges.setDescription("I used a BFS algorithm to find the shortest path between two actors through movies.");

        DataSource ds = bridges.getDataSource();
        List<ActorMovieIMDB> data = ds.getActorMovieIMDBData(1813);

        ShortestPath pathFinder = new ShortestPath(data);

        String targetActor = "James_Earl_Jones"; 

        pathFinder.findAndVisualizePath("Kevin_Bacon_(I)", targetActor);

        bridges.setDataStructure(pathFinder.graph);
        bridges.visualize();
    }
}
