package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import org.jspecify.annotations.Nullable;

public class TridentAnimationFix extends ComponentFix {
	public TridentAnimationFix(Schema schema) {
		super(schema, "TridentAnimationFix", "minecraft:consumable");
	}

	@Nullable
	@Override
	protected <T> Dynamic<T> fixComponent(Dynamic<T> dynamic) {
		return dynamic.update("animation", value -> {
			String string = (String)value.asString().result().orElse("");
			return "spear".equals(string) ? value.createString("trident") : value;
		});
	}
}
