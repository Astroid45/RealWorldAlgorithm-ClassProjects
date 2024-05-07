import java.util.List;
import java.util.Random;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.data_src_dependent.ActorMovieIMDB;

public class IMDBTutorial {
    public static void main(String[] args) throws Exception {
		String username = Credential.readUsername();
		String apiKey = Credential.readApiKey();
		//create the Bridges object
		Bridges bridges = new Bridges(11, username, apiKey);

		DataSource ds = bridges.getDataSource();

		// Get a List of ActorMovieIMDB objects from Bridges
		List<ActorMovieIMDB> mylist = ds.getActorMovieIMDBData(1813);

		// Inspect a random ActorMovieIMDB object
		ActorMovieIMDB pair1 = mylist.get((new Random()).nextInt(mylist.size()));
		System.out.println(pair1.getActor());
		System.out.println(pair1.getMovie());
	}
}
