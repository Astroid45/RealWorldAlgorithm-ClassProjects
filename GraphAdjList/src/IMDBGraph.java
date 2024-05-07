import bridges.base.Color;
import bridges.base.ColorGrid;
import bridges.base.Edge;
import bridges.base.ElementVisualizer;
import bridges.base.Polyline;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.data_src_dependent.ActorMovieIMDB;
import bridges.base.GraphAdjListSimple;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class IMDBGraph {
    public static void main(String[] args) throws Exception {
        String username = Credential.readUsername();
        String apiKey = Credential.readApiKey();
        Bridges bridges = new Bridges(12, username,
                apiKey);
        
        bridges.setTitle("IMDB Database");
        bridges.setDescription("Showcasing connecting edges and verticies of IMDB actors and their movies.");

        DataSource ds_2 = bridges.getDataSource();
        List<ActorMovieIMDB> data = ds_2.getActorMovieIMDBData(1813);
        GraphAdjListSimple<String> graph_2 = new GraphAdjListSimple<>();

        Map<String, Boolean> vertexMap = new HashMap<>();

        for (ActorMovieIMDB item: data){
            String actor = item.getActor();
            String movie = item.getMovie();

            if(!vertexMap.containsKey(actor)){
                graph_2.addVertex(actor, actor);
                vertexMap.put(actor, true);
            }

            if(!vertexMap.containsValue(movie)){
                graph_2.addVertex(movie, movie);
                vertexMap.put(movie, true);
            }

            graph_2.addEdge(actor, movie);
        }

        bridges.setDataStructure(graph_2);

        String actor1 = "Denzel_Washington";
        String actor2 = "Brigitte_Bardot";

        if (graph_2.getVertices().containsKey(actor1) && graph_2.getVertices().containsKey(actor2)) {
            colorImmediateNeighbor(graph_2, actor1, new Color("orange"), new Color("red"));
            colorImmediateNeighbor(graph_2, actor2, new Color("orange"), new Color("blue"));

            bridges.visualize();
        } else {
            System.out.println("One or both of the actors do not exist in the graph."); // <-- Check making sure both actors exist
        }
    }

    private static void colorImmediateNeighbor(GraphAdjListSimple<String> graph, String actor, Color color, Color color_2){
        ElementVisualizer ev = graph.getVisualizer(actor);

        if (ev != null) {
            for (Edge<String, String> edge : graph.outgoingEdgeSetOf(actor)) {
                String neighbor = edge.getTo();

                graph.getLinkVisualizer(actor, neighbor).setColor(color);
                graph.getVertex(actor).setColor(color_2);
                graph.getVisualizer(neighbor).setColor(color_2);
            }
        } else {
            System.out.println("Vertex " + actor + " does not exist! First add the vertices to the graph."); // <-- Just a check I added to make sure actor was in the graph
        }
    }
}