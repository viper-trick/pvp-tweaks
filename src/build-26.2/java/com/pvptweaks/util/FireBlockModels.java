package com.pvptweaks.util;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class FireBlockModels {

    public static BlockStateModel emptyModel(Material.Baked particleMat) {
        return new EmptyModel(particleMat);
    }

    public static BlockStateModel scaledModel(BlockStateModel delegate, float scale) {
        return new ScaledModel(delegate, scale);
    }

    private static class EmptyModel implements BlockStateModel {
        private final Material.Baked particleMat;
        EmptyModel(Material.Baked particleMat) { this.particleMat = particleMat; }

        @Override
        public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {}

        @Override
        public Material.Baked particleMaterial() { return particleMat; }

        @Override
        public int materialFlags() { return 0; }
    }

    private static class ScaledModel implements BlockStateModel {
        private final BlockStateModel delegate;
        private final float scale;
        ScaledModel(BlockStateModel delegate, float scale) { this.delegate = delegate; this.scale = scale; }

        @Override
        public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
            int before = parts.size();
            delegate.collectParts(random, parts);
            for (int i = before; i < parts.size(); i++) {
                parts.set(i, new ScaledPart(parts.get(i), scale));
            }
        }

        @Override
        public Material.Baked particleMaterial() { return delegate.particleMaterial(); }

        @Override
        public int materialFlags() { return delegate.materialFlags(); }
    }

    private static class ScaledPart implements BlockStateModelPart {
        private final BlockStateModelPart delegate;
        private final float scale;
        ScaledPart(BlockStateModelPart delegate, float scale) { this.delegate = delegate; this.scale = scale; }

        @Override
        public List<BakedQuad> getQuads(Direction direction) {
            List<BakedQuad> original = delegate.getQuads(direction);
            List<BakedQuad> scaled = new ArrayList<>(original.size());
            for (BakedQuad quad : original) {
                scaled.add(new BakedQuad(
                        new Vector3f(quad.position0().x(), quad.position0().y() * scale, quad.position0().z()),
                        new Vector3f(quad.position1().x(), quad.position1().y() * scale, quad.position1().z()),
                        new Vector3f(quad.position2().x(), quad.position2().y() * scale, quad.position2().z()),
                        new Vector3f(quad.position3().x(), quad.position3().y() * scale, quad.position3().z()),
                        quad.packedUV0(),
                        quad.packedUV1(),
                        quad.packedUV2(),
                        quad.packedUV3(),
                        quad.direction(),
                        quad.materialInfo()
                ));
            }
            return scaled;
        }

        @Override
        public boolean useAmbientOcclusion() { return delegate.useAmbientOcclusion(); }

        @Override
        public Material.Baked particleMaterial() { return delegate.particleMaterial(); }

        @Override
        public int materialFlags() { return delegate.materialFlags(); }
    }
}
