package net.minecraft.resource;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.JsonHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class AbstractFileResourcePack implements ResourcePack {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ResourcePackInfo info;

	protected AbstractFileResourcePack(ResourcePackInfo info) {
		this.info = info;
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer) throws IOException {
		InputSupplier<InputStream> inputSupplier = this.openRoot(new String[]{"pack.mcmeta"});
		if (inputSupplier == null) {
			return null;
		} else {
			InputStream inputStream = inputSupplier.get();

			Object var4;
			try {
				var4 = parseMetadata(metadataSerializer, inputStream, this.info);
			} catch (Throwable var7) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return (T)var4;
		}
	}

	@Nullable
	public static <T> T parseMetadata(ResourceMetadataSerializer<T> resourceMetadataSerializer, InputStream inputStream, ResourcePackInfo resourcePackInfo) {
		JsonObject jsonObject;
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			try {
				jsonObject = JsonHelper.deserialize(bufferedReader);
			} catch (Throwable var8) {
				try {
					bufferedReader.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}

				throw var8;
			}

			bufferedReader.close();
		} catch (Exception var9) {
			LOGGER.error("Couldn't load {} {} metadata: {}", resourcePackInfo.id(), resourceMetadataSerializer.name(), var9.getMessage());
			return null;
		}

		return (T)(!jsonObject.has(resourceMetadataSerializer.name())
			? null
			: resourceMetadataSerializer.codec()
				.parse(JsonOps.INSTANCE, jsonObject.get(resourceMetadataSerializer.name()))
				.ifError(error -> LOGGER.error("Couldn't load {} {} metadata: {}", resourcePackInfo.id(), resourceMetadataSerializer.name(), error.message()))
				.result()
				.orElse(null));
	}

	@Override
	public ResourcePackInfo getInfo() {
		return this.info;
	}
}
