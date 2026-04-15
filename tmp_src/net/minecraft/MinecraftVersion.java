package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.resource.PackVersion;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

public class MinecraftVersion {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final GameVersion DEVELOPMENT = create(UUID.randomUUID().toString().replaceAll("-", ""), "Development Version");

	public static GameVersion create(String id, String name) {
		return create(id, name, true);
	}

	public static GameVersion create(String id, String name, boolean stable) {
		return new GameVersion.Impl(
			id, name, new SaveVersion(4671, "main"), SharedConstants.getProtocolVersion(), PackVersion.of(75, 0), PackVersion.of(94, 1), new Date(), stable
		);
	}

	private static GameVersion fromJson(JsonObject json) {
		JsonObject jsonObject = JsonHelper.getObject(json, "pack_version");
		return new GameVersion.Impl(
			JsonHelper.getString(json, "id"),
			JsonHelper.getString(json, "name"),
			new SaveVersion(JsonHelper.getInt(json, "world_version"), JsonHelper.getString(json, "series_id", "main")),
			JsonHelper.getInt(json, "protocol_version"),
			PackVersion.of(JsonHelper.getInt(jsonObject, "resource_major"), JsonHelper.getInt(jsonObject, "resource_minor")),
			PackVersion.of(JsonHelper.getInt(jsonObject, "data_major"), JsonHelper.getInt(jsonObject, "data_minor")),
			Date.from(ZonedDateTime.parse(JsonHelper.getString(json, "build_time")).toInstant()),
			JsonHelper.getBoolean(json, "stable")
		);
	}

	public static GameVersion create() {
		try {
			InputStream inputStream = MinecraftVersion.class.getResourceAsStream("/version.json");

			GameVersion var9;
			label63: {
				GameVersion var2;
				try {
					if (inputStream == null) {
						LOGGER.warn("Missing version information!");
						var9 = DEVELOPMENT;
						break label63;
					}

					InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

					try {
						var2 = fromJson(JsonHelper.deserialize(inputStreamReader));
					} catch (Throwable var6) {
						try {
							inputStreamReader.close();
						} catch (Throwable var5) {
							var6.addSuppressed(var5);
						}

						throw var6;
					}

					inputStreamReader.close();
				} catch (Throwable var7) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var4) {
							var7.addSuppressed(var4);
						}
					}

					throw var7;
				}

				if (inputStream != null) {
					inputStream.close();
				}

				return var2;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var9;
		} catch (JsonParseException | IOException var8) {
			throw new IllegalStateException("Game version information is corrupt", var8);
		}
	}
}
