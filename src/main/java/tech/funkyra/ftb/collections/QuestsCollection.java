package tech.funkyra.ftb.collections;

import com.mongodb.client.MongoCollection;
import net.minecraft.nbt.NBTTagCompound;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static tech.funkyra.ftb.DBUtil.fromDocument;
import static tech.funkyra.ftb.DBUtil.toDocument;
import static tech.funkyra.ftb.Database.ftbDb;
import static tech.funkyra.ftb.Database.queryOption;

public class QuestsCollection {
	private static final MongoCollection<Document> teamRewardsCollection = ftbDb.getCollection("quests");

	public static NBTTagCompound getData(String teamID) {
		try {
			Document data = teamRewardsCollection.find(eq("teamID", teamID)).first();
			assert data != null;

			return fromDocument(data);
		} catch (Exception ignored) {
			return new NBTTagCompound();
		}
	}

	public static boolean setData(String teamID, NBTTagCompound data) {
		try {
			Document doc = new Document("teamID", teamID).append("nbt", toDocument(data));

			return teamRewardsCollection.replaceOne(eq("teamID", teamID), doc, queryOption).wasAcknowledged();
		} catch (Exception err) {
			err.printStackTrace();
			return false;
		}
	}
}
