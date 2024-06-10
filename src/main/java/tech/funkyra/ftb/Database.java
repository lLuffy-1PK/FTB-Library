package tech.funkyra.ftb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;

import static com.feed_the_beast.ftblib.FTBLibConfig.general;

public class Database {
	private static final ConnectionString connectionStr =
		new ConnectionString(general.mongoUri);

	private static final MongoClient mongo = MongoClients.create(connectionStr);
	public static final MongoDatabase ftbDb = mongo.getDatabase("ftb");

	public static final ReplaceOptions queryOption = new ReplaceOptions().upsert(true);

	public static void closeConnection() {
		mongo.close();
	}
}
