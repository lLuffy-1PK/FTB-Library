package tech.funkyra.ftb.collections;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.minecraft.nbt.NBTTagCompound;
import org.bson.Document;

import javax.annotation.Nonnull;

import static com.mongodb.client.model.Filters.eq;
import static tech.funkyra.ftb.DBUtil.fromDocument;
import static tech.funkyra.ftb.DBUtil.toDocument;
import static tech.funkyra.ftb.Database.ftbDb;
import static tech.funkyra.ftb.Database.queryOption;

public class TeamsCollection {
	private static final MongoCollection<Document> teamsCollection = ftbDb.getCollection("teams");

	public static boolean updateTeam(short uid, @Nonnull String id, NBTTagCompound nbt) {
		Document data = new Document("uid", uid).append("id", id).append("nbt", toDocument(nbt));

		return teamsCollection.replaceOne(eq("uid", uid), data, queryOption).wasAcknowledged();
	}

	public static boolean deleteTeam(short uid) {
		return teamsCollection.deleteOne(eq("uid", uid)).wasAcknowledged();
	}

	public static MongoCursor<Document> getAllTeams() {
		return teamsCollection.find().iterator();
	}

	public static NBTTagCompound getTeamById(@Nonnull String id) {
		return fromDocument(teamsCollection.find(eq("id", id)).first());
	}
}
