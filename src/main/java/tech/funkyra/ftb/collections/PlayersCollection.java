package tech.funkyra.ftb.collections;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.minecraft.nbt.NBTTagCompound;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static tech.funkyra.ftb.DBUtil.fromDocument;
import static tech.funkyra.ftb.DBUtil.toDocument;
import static tech.funkyra.ftb.Database.ftbDb;
import static tech.funkyra.ftb.Database.queryOption;

public class PlayersCollection {
	private static final MongoCollection<Document> playersCollection = ftbDb.getCollection("ftbplayers");

	public static boolean updatePlayer(short teamUID, String teamID, String uuid, String nick, NBTTagCompound nbt) {
		try {
			Document data = new Document("teamUID", teamUID)
				.append("teamID", teamID)
				.append("uuid", uuid)
				.append("nick", nick)
				.append("nbt", toDocument(nbt));

			return playersCollection.replaceOne(eq("teamID", teamID), data, queryOption).wasAcknowledged();
		} catch (Exception err) {
			err.printStackTrace();
			return false;
		}
	}

	public static MongoCursor<Document> getAllPlayers() {
		return playersCollection.find().iterator();
	}

	public static NBTTagCompound getPlayerIfExists(String uuid) {
		try {
			return fromDocument(playersCollection.find(eq("uuid", uuid)).first());
		} catch (Exception e) {
			return new NBTTagCompound();
		}
	}
}
