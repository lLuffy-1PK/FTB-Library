package tech.funkyra.ftb;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bson.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static com.mongodb.client.model.Filters.eq;

public class DBUtil {
	public static String toDocument(NBTTagCompound nbt) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(nbt, byteArrayOutputStream);

			return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static NBTTagCompound fromDocument(Document document) {
		try {
			byte[] data = Base64.getDecoder().decode(document.getString("nbt"));
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

			return CompressedStreamTools.readCompressed(byteArrayInputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return new NBTTagCompound();
		}
	}
}
