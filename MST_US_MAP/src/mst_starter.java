import java.util.*;
import bridges.base.*;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.data_src_dependent.City;

public class mst_starter {

	public static void main(String[] args) {
		String username = Credential.readUsername();
		String apiKey = Credential.readApiKey();
		Bridges bridges = new Bridges(15, username, apiKey);
		bridges.setTitle("MST on US Cities");
		
		DataSource ds = bridges.getDataSource();
		HashMap<String, String> params = new HashMap<>();
		params.put("country", "US"); 
		params.put("min_pop", "640000"); 
		try {
			Vector<City> cities = ds.getUSCitiesData(params);

			
			GraphAdjList<String, String, Double> graph = createGraphFromCities(cities);

		
			Set<Edge<String, Double>> mstEdges = primMST(graph, cities.get(0).getCity());

			double totalCost = calculateTotalCost(mstEdges);
			int numVertices = graph.getVertices().size();
			int numEdges = mstEdges.size();

			bridges.setDescription("Pop Threshold: " + params.get("min_pop") + 
                               ", Vertices: " + numVertices + 
                               ", Edges: " + numEdges + 
                               ", Cost: " + totalCost);
			System.out.println("Minimum Total Cost of the Graph: " + totalCost);

			visualizeMST(bridges, graph, mstEdges);
		} catch (Exception e) {
			System.err.println("Exception occurred: " + e.getMessage());
		}
	}

	static GraphAdjList<String, String, Double> createGraphFromCities(Vector<City> cities) {
		GraphAdjList<String, String, Double> graph = new GraphAdjList<>();
		// Add vertices
		for (City city : cities) {
			graph.addVertex(city.getCity(), city.getCity());
		}
		
		double tolerance = 0.001;
		for (int i = 0; i < cities.size(); i++) {
			for (int j = i + 1; j < cities.size(); j++) {
				City city1 = cities.get(i);
				City city2 = cities.get(j);
				
				if (Math.abs(city1.getLatitude()) > tolerance && Math.abs(city1.getLongitude()) > tolerance &&
						Math.abs(city2.getLatitude()) > tolerance && Math.abs(city2.getLongitude()) > tolerance) {
					double distance = getDist(city1.getLatitude(), city1.getLongitude(),
							city2.getLatitude(), city2.getLongitude());
					if (distance != 0) {
						graph.addEdge(city1.getCity(), city2.getCity(), distance);
					}
				}
			}
		}
		return graph;
	}

	static Set<Edge<String, Double>> primMST(GraphAdjList<String, String, Double> graph, String startVertex) {
		Set<String> visitedVertices = new HashSet<>();
		PriorityQueue<Edge<String, Double>> minHeap = new PriorityQueue<>(
				Comparator.comparingDouble(Edge::getEdgeData));
		Set<Edge<String, Double>> mstEdges = new HashSet<>();

		visitedVertices.add(startVertex);

		for (Edge<String, Double> edge : graph.outgoingEdgeSetOf(startVertex)) {
			minHeap.add(edge);
		}

		while (!minHeap.isEmpty()) {
			Edge<String, Double> edge = minHeap.poll();
			String fromVertex = edge.getFrom();
			String toVertex = edge.getTo();

			if (!visitedVertices.contains(toVertex)) {
				visitedVertices.add(toVertex);
				mstEdges.add(edge);

				for (Edge<String, Double> nextEdge : graph.outgoingEdgeSetOf(toVertex)) {
					if (!visitedVertices.contains(nextEdge.getTo())) {
						minHeap.add(nextEdge);
					}
				}
			}
		}

		return mstEdges;
	}

	static double calculateTotalCost(Set<Edge<String, Double>> mstEdges) {
		double totalCost = 0;
		for (Edge<String, Double> edge : mstEdges) {
			totalCost += edge.getEdgeData();
		}
		return totalCost;
	}

	static void visualizeMST(Bridges bridges, GraphAdjList<String, String, Double> graph,
			Set<Edge<String, Double>> mstEdges) {
		
		GraphAdjList<String, String, Double> mstGraph = new GraphAdjList<>();

		
		for (String vertex : graph.getVertices().keySet()) {
			mstGraph.addVertex(vertex, vertex);
		}

		
		for (Edge<String, Double> edge : mstEdges) {
			String src = edge.getFrom();
			String dest = edge.getTo();
			double weight = edge.getEdgeData();
			mstGraph.addEdge(src, dest, weight);
			mstGraph.getLinkVisualizer(src, dest).setColor("red");
			mstGraph.getLinkVisualizer(src, dest).setThickness(1.0);
			mstGraph.getLinkVisualizer(src, dest).setLabel(String.valueOf(weight));
		}

		bridges.setCoordSystemType("albersusa");
		bridges.setDataStructure(mstGraph);
		bridges.setMap("us", "all");
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
}
