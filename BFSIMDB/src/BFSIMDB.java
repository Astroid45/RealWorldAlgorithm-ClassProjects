import bridges.base.Color;
import bridges.base.Element;
import bridges.base.Edge;
import bridges.base.GraphAdjListSimple;
import bridges.connect.Bridges;
import bridges.data_src_dependent.ActorMovieIMDB;
import bridges.connect.DataSource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class BFSIMDB {
    public static void main(String[] args) throws Exception {
        String username = Credential.readUsername();
        String apiKey = Credential.readApiKey();
        Bridges bridges = new Bridges(13, username, apiKey);

        bridges.setTitle("IMDB Database BFS");
        bridges.setDescription("BFS traversal of IMDB actors and their movies with level labels and specified colors.");

        DataSource ds = bridges.getDataSource();
        List<ActorMovieIMDB> data = ds.getActorMovieIMDBData(1813);
        GraphAdjListSimple<String> graph = new GraphAdjListSimple<>();

        Map<String, Boolean> vertexMap = new HashMap<>(); // Creation of hashmap which houses actor and boolean to check if added in graph

        for (ActorMovieIMDB item : data) {
            String actor = item.getActor();
            String movie = item.getMovie();

            if (!vertexMap.containsKey(actor)) { // if loop adding vertex to graph and then putting it in hashmap
                graph.addVertex(actor, actor);
                vertexMap.put(actor, true);
            }

            if (!vertexMap.containsKey(movie)) { // this loop does the same for the movies
                graph.addVertex(movie, movie);
                vertexMap.put(movie, true);
            }

            graph.addEdge(actor, movie); // creates an edge between an actor and all of his/hers movies
        }

        bridges.setDataStructure(graph);

        int maxLevels = 8;
        // this for loop makes sure to create a random starting point iteration through all the actors
        String startActor = "Cate_Blanchett";
        bfs(graph, startActor, maxLevels); // calls bfs method

        for (ActorMovieIMDB item : data) {
            String actor = item.getActor();
            if (!actor.equals(startActor)) {
                bfs(graph, actor, maxLevels);
            }
        }

        bridges.visualize();
    }

    private static void bfs(GraphAdjListSimple<String> graph, String startActor, int maxLevels) {
        Queue<String> queue = new LinkedList<>(); // Queue creation 
        Map<String, Integer> levels = new HashMap<>(); // This hashmap is important for keeping track of the levels
        Map<String, List<String>> actorMovieMap = new HashMap<>(); // This hashmap is important because it houses the actor as the key and all there movies in a list

        queue.add(startActor); // adding first actor to the queue below will set the initial level to 0
        levels.put(startActor, 0);

        while (!queue.isEmpty()) { // This while loop is responsible for setting the colors of the links through the queue method
            String currentActor = queue.poll(); 
            int currentLevel = levels.get(currentActor);

            setVisualizationAttributes(graph, currentActor, currentLevel);

            for (Edge<String, String> edge : graph.outgoingEdgeSetOf(currentActor)) { // This for loop is what adds neighboring movies of actor to the queue
                String neighbor = edge.getTo();
                if (!levels.containsKey(neighbor) && currentLevel < maxLevels) {
                    queue.add(neighbor);

                    levels.put(neighbor, currentLevel + 1); // Sets the level for the new neighbor

                    graph.getLinkVisualizer(currentActor, neighbor).setColor(getColor(currentLevel + 1, currentActor)); // this sets the color for links
                    
                    graph.getVertex(neighbor).setColor(getColor(currentLevel + 2, currentActor)); // This and below sets the color for node which is always one above
                    graph.getVertex(currentActor).setColor(getColor(currentLevel + 2, currentActor));
                        
                    
                    actorMovieMap.computeIfAbsent(currentActor, k -> new LinkedList<>()).add(neighbor);
                }
            }
        }
    }

    private static void setVisualizationAttributes(GraphAdjListSimple<String> graph, String actor, int level) {
        Element element = graph.getVertex(actor);

        if (element != null) {
            element.setLabel(actor + " (" + level + ")"); // This is responsible for setting all the labels for the linking nodes
        }
    }

    private static Color getColor(int level, String actor) {
        // This hashcode helps to adjust the color based on the actor
        int baseLevel = (level + actor.hashCode()) % 9;

        int adjustedLevel = Math.min(baseLevel, 8); // The adjusted level is what decides the color

        if(adjustedLevel < 0){ // This stops the method from returning the default so if the adjusted is negative make it positive so it selects a color
            adjustedLevel = adjustedLevel * -1;
        }

        switch (adjustedLevel) {
            case 0:
                return new Color("red");
            case 1:
                return new Color("green");
            case 2:
                return new Color("blue");
            case 3:
                return new Color("cyan");
            case 4:
                return new Color("magenta");
            case 5:
                return new Color("yellow");
            case 6:
                return new Color("mistyrose");
            case 7:
                return new Color("orange");
            case 8:
                return new Color("purple");
            default:
                return new Color("beige");
        }
    }
}
