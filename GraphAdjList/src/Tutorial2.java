import java.util.ArrayList;
import java.lang.String;
import bridges.base.Element;
import bridges.base.SLelement;
import bridges.base.GraphAdjListSimple;
import bridges.base.Edge;
import bridges.connect.Bridges;
public class Tutorial2 {
    public static void main(String[] args) throws Exception {
		String username = Credential.readUsername();
		String apiKey = Credential.readApiKey();
		// initialize Bridges, set credentials
		Bridges bridges = new Bridges(9, username,
			apiKey);

		// set a title
		bridges.setTitle("A Simple Adjacency list based Graph Example.");

		// set  description
		bridges.setDescription("Demonstrate styling graph nodes and links with visual attributes");

		// create an adjacency list based graph
		GraphAdjListSimple<String> graph = new GraphAdjListSimple<String>();

		// create some actor names to be added to the graph
		String  kevin_bacon = "Kevin Bacon",
				denzel_washington = "Denzel Washington",
				morgan_freeman = "Morgan Freeman",
				tom_cruise = "Tom Cruise",
				angelina_jolie = "Angelina Jolie",
				amy_adams = "Amy Adams",
				brad_pitt = "Brad Pitt";

		// add them to the graph
		graph.addVertex(kevin_bacon, kevin_bacon);
		graph.addVertex(denzel_washington, denzel_washington);
		graph.addVertex(morgan_freeman, morgan_freeman);
		graph.addVertex(tom_cruise, tom_cruise);
		graph.addVertex(angelina_jolie, angelina_jolie);
		graph.addVertex(amy_adams, amy_adams);
		graph.addVertex(brad_pitt, brad_pitt);

		// add edges
		graph.addEdge(kevin_bacon, denzel_washington);
		graph.addEdge(kevin_bacon, morgan_freeman);
		graph.addEdge(kevin_bacon, angelina_jolie);
		graph.addEdge(amy_adams, angelina_jolie);
		graph.addEdge(tom_cruise, amy_adams);
		graph.addEdge(angelina_jolie, brad_pitt);
		graph.addEdge(brad_pitt, denzel_washington);

		// style  the nodes

		// distinguish the male and female actors by color
		graph.getVertex(kevin_bacon).setColor("limegreen");
		graph.getVertex(brad_pitt).setColor("limegreen");
		graph.getVertex(morgan_freeman).setColor("limegreen");
		graph.getVertex(denzel_washington).setColor("limegreen");
		graph.getVertex(tom_cruise).setColor("limegreen");
		graph.getVertex(angelina_jolie).setColor("crimson");
		graph.getVertex(amy_adams).setColor("crimson");

		// shape
		graph.getVertex(brad_pitt).setShape("square");
		graph.getVertex(angelina_jolie).setShape("square");

		// opacity
		graph.getVertex(tom_cruise).setOpacity(0.3f);

		// style the links
		graph.getLinkVisualizer(angelina_jolie, brad_pitt).setColor("orange");
        // Below are the added changes apart of Task2 which where to give the labels to the connceting links
        graph.getLinkVisualizer(kevin_bacon, denzel_washington).setLabel("Kevin Bacon -> Denzel Washington");
        graph.getLinkVisualizer(kevin_bacon, morgan_freeman).setLabel("Kevin Bacon -> Morgan Freeman");
        graph.getLinkVisualizer(kevin_bacon, angelina_jolie).setLabel("Kevin Bacon -> Angelina Jolie");
        graph.getLinkVisualizer(amy_adams, angelina_jolie).setLabel("Amy Adams -> Angelina Jolie");
        graph.getLinkVisualizer(tom_cruise, amy_adams).setLabel("Tom Cruise -> Amy Adams");
        graph.getLinkVisualizer(angelina_jolie, brad_pitt).setLabel("Angelina Jolie -> Brad Pitt");
        graph.getLinkVisualizer(brad_pitt, denzel_washington).setLabel("Brad Pitt -> Denzel Washington");
		graph.getLinkVisualizer(angelina_jolie, brad_pitt).setThickness(2.0f);

		// Pass the graph object to BRIDGES
		bridges.setDataStructure(graph);

		// Finaly we call the visualize function
		bridges.visualize();
	}
}
