package net.minecraft.item;

import com.google.common.collect.ImmutableBiMap;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.CopperBlockSet;

public record CopperBlockItemSet(
	Item unaffected, Item exposed, Item weathered, Item oxidized, Item waxed, Item waxedExposed, Item waxedWeathered, Item waxedOxidized
) {
	public static CopperBlockItemSet create(CopperBlockSet blockSet, Function<Block, Item> registerFunction) {
		return new CopperBlockItemSet(
			(Item)registerFunction.apply(blockSet.unaffected()),
			(Item)registerFunction.apply(blockSet.exposed()),
			(Item)registerFunction.apply(blockSet.weathered()),
			(Item)registerFunction.apply(blockSet.oxidized()),
			(Item)registerFunction.apply(blockSet.waxed()),
			(Item)registerFunction.apply(blockSet.waxedExposed()),
			(Item)registerFunction.apply(blockSet.waxedWeathered()),
			(Item)registerFunction.apply(blockSet.waxedOxidized())
		);
	}

	public ImmutableBiMap<Item, Item> getWaxingMap() {
		return ImmutableBiMap.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered, this.oxidized, this.waxedOxidized);
	}

	public void forEach(Consumer<Item> consumer) {
		consumer.accept(this.unaffected);
		consumer.accept(this.exposed);
		consumer.accept(this.weathered);
		consumer.accept(this.oxidized);
		consumer.accept(this.waxed);
		consumer.accept(this.waxedExposed);
		consumer.accept(this.waxedWeathered);
		consumer.accept(this.waxedOxidized);
	}
}
