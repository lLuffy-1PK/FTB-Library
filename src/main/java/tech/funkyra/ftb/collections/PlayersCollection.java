package tech.funkyra.ftb.collections;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.minecraft.nbt.NBTTagCompound;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static tech.funkyra.ftb.DBUtil.toDocument;
import static tech.funkyra.ftb.Database.ftbDb;
import static tech.funkyra.ftb.Database.queryOption;

public class PlayersCollection {
	private static final MongoCollection<Document> playersCollection = ftbDb.getCollection("players");

	public static boolean updatePlayer(short teamUID, String teamID, String uuid, String nick, NBTTagCompound nbt) {
		Document data = new Document("teamUID", teamUID)
			.append("teamID", teamID)
			.append("uuid", uuid)
			.append("nick", nick)
			.append("nbt", toDocument(nbt));

		return playersCollection.replaceOne(eq("teamUID", teamUID), data, queryOption).wasAcknowledged();
	}

	public static MongoCursor<Document> getAllPlayers() {
		return playersCollection.find().iterator();
	}
}
