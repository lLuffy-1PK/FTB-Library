package tech.funkyra.ftb;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class DBUtil {
	public static String toDocument(@Nonnull NBTTagCompound nbt) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(nbt, byteArrayOutputStream);

			return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static NBTTagCompound fromDocument(@Nonnull Document document) {
		try {
			String playerNBT = document.getString("player");

			if (playerNBT == null) return new NBTTagCompound();

			byte[] data = Base64.getDecoder().decode(playerNBT);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

			return CompressedStreamTools.readCompressed(byteArrayInputStream);
		} catch (Exception e) {
			e.printStackTrace();
			return new NBTTagCompound();
		}
	}
}
