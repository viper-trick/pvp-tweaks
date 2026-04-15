package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SentInvite implements RealmsSerializable {
	@SerializedName("name")
	@Nullable
	public String profileName;
	@SerializedName("uuid")
	@JsonAdapter(UUIDTypeAdapter.class)
	@Nullable
	public UUID uuid;
}
