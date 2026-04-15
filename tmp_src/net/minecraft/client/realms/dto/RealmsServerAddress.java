package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.ServiceQuality;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public record RealmsServerAddress(
	@Nullable @SerializedName("address") String address,
	@Nullable @SerializedName("resourcePackUrl") String resourcePackUrl,
	@Nullable @SerializedName("resourcePackHash") String resourcePackHash,
	@Nullable @SerializedName("sessionRegionData") RealmsServerAddress.RegionData regionData
) implements RealmsSerializable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final RealmsServerAddress NULL = new RealmsServerAddress(null, null, null, null);

	public static RealmsServerAddress parse(CheckedGson gson, String json) {
		try {
			RealmsServerAddress realmsServerAddress = gson.fromJson(json, RealmsServerAddress.class);
			if (realmsServerAddress == null) {
				LOGGER.error("Could not parse RealmsServerAddress: {}", json);
				return NULL;
			} else {
				return realmsServerAddress;
			}
		} catch (Exception var3) {
			LOGGER.error("Could not parse RealmsServerAddress", (Throwable)var3);
			return NULL;
		}
	}

	@Environment(EnvType.CLIENT)
	public record RegionData(
		@Nullable @SerializedName("regionName") RealmsRegion region, @Nullable @SerializedName("serviceQuality") ServiceQuality serviceQuality
	) implements RealmsSerializable {
	}
}
