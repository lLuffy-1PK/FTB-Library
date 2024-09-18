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
			assert teamID != null;
			assert uuid != null;
			assert nick != null;
			assert nbt != null;

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
			Document data = playersCollection.find(eq("uuid", uuid)).first();
			assert data != null;

			return fromDocument(data);
		} catch (Exception e) {
			return new NBTTagCompound();
		}
	}
}

/*
NBTTagCompound nbt = fromDocument(players.next());
				String uuidString = nbt.getString("UUID");
				UUID uuid = StringUtils.fromString(uuidString);
				assert uuid != null;

				playerNBT.put(uuid, nbt);
				ForgePlayer player = new ForgePlayer(this, uuid, nbt.getString("Name"));

				this.players.put(uuid, player);
 */