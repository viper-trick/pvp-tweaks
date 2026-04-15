package net.minecraft.client.realms;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.DontSerialize;
import org.jspecify.annotations.Nullable;

/**
 * Checks so that only intended pojos are passed to the GSON (handles
 * serialization after obfuscation).
 */
@Environment(EnvType.CLIENT)
public class CheckedGson {
	ExclusionStrategy EXCLUSION_STRATEGY = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

		@Override
		public boolean shouldSkipField(FieldAttributes fieldAttributes) {
			return fieldAttributes.getAnnotation(DontSerialize.class) != null;
		}
	};
	private final Gson GSON = new GsonBuilder()
		.addSerializationExclusionStrategy(this.EXCLUSION_STRATEGY)
		.addDeserializationExclusionStrategy(this.EXCLUSION_STRATEGY)
		.create();

	public String toJson(RealmsSerializable serializable) {
		return this.GSON.toJson(serializable);
	}

	public String toJson(JsonElement json) {
		return this.GSON.toJson(json);
	}

	@Nullable
	public <T extends RealmsSerializable> T fromJson(String json, Class<T> type) {
		return this.GSON.fromJson(json, type);
	}
}
