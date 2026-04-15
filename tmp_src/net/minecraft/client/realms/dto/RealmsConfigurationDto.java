package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record RealmsConfigurationDto(
	@SerializedName("options") RealmsOptionsDto options,
	@SerializedName("settings") List<RealmsSettingDto> settings,
	@Nullable @SerializedName("regionSelectionPreference") RealmsRegionSelectionPreference regionSelectionPreference,
	@Nullable @SerializedName("description") RealmsDescriptionDto description
) implements RealmsSerializable {
}
