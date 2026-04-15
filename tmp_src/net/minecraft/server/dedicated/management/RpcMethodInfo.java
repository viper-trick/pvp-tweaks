package net.minecraft.server.dedicated.management;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

public record RpcMethodInfo<Params, Result>(String description, Optional<RpcRequestParameter<Params>> params, Optional<RpcResponseResult<Result>> result) {
	public RpcMethodInfo(String description, @Nullable RpcRequestParameter<Params> param, @Nullable RpcResponseResult<Result> result) {
		this(description, Optional.ofNullable(param), Optional.ofNullable(result));
	}

	private static <Params> Optional<RpcRequestParameter<Params>> getParameter(List<RpcRequestParameter<Params>> params) {
		return params.isEmpty() ? Optional.empty() : Optional.of((RpcRequestParameter)params.getFirst());
	}

	private static <Params> List<RpcRequestParameter<Params>> toParameterList(Optional<RpcRequestParameter<Params>> param) {
		return param.isPresent() ? List.of((RpcRequestParameter)param.get()) : List.of();
	}

	private static <Params> Codec<Optional<RpcRequestParameter<Params>>> createParamsCodec() {
		return RpcRequestParameter.createCodec().codec().listOf().xmap(RpcMethodInfo::getParameter, RpcMethodInfo::toParameterList);
	}

	static <Params, Result> MapCodec<RpcMethodInfo<Params, Result>> createCodec() {
		return RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codec.STRING.fieldOf("description").forGetter(RpcMethodInfo::description),
					createParamsCodec().fieldOf("params").forGetter(RpcMethodInfo::params),
					RpcResponseResult.getCodec().optionalFieldOf("result").forGetter(RpcMethodInfo::result)
				)
				.apply(instance, RpcMethodInfo::new)
		);
	}

	public RpcMethodInfo.Entry<Params, Result> toEntry(Identifier name) {
		return new RpcMethodInfo.Entry<>(name, this);
	}

	public record Entry<Params, Result>(Identifier name, RpcMethodInfo<Params, Result> contents) {
		public static final Codec<RpcMethodInfo.Entry<?, ?>> CODEC = createCodec();

		public static <Params, Result> Codec<RpcMethodInfo.Entry<Params, Result>> createCodec() {
			return RecordCodecBuilder.create(
				instance -> instance.group(
						Identifier.CODEC.fieldOf("name").forGetter(RpcMethodInfo.Entry::name), RpcMethodInfo.createCodec().forGetter(RpcMethodInfo.Entry::contents)
					)
					.apply(instance, RpcMethodInfo.Entry::new)
			);
		}
	}
}
