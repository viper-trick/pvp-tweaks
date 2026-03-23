package com.pvptweaks.resources;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

public class PvpTweaksDynamicPack implements ResourcePack {

    private static final ResourcePackInfo INFO = new ResourcePackInfo(
        "pvptweaks:fire", Text.literal("PVP Tweaks Fire"),
        ResourcePackSource.BUILTIN, Optional.empty()
    );

    @Override public InputSupplier<InputStream> openRoot(String... segments) { return null; }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        // Log ALL calls to see what MC asks from us
        if (id.getPath().contains("fire") && id.getNamespace().equals("minecraft")) {
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] FirePack.open called: {}", id);
        }
        if (type != ResourceType.CLIENT_RESOURCES) return null;

        if (id.getNamespace().equals("pvptweaks") && id.getPath().startsWith("sounds/custom/")) {
            String fileName = id.getPath().substring("sounds/custom/".length());
            java.nio.file.Path soundFile = com.pvptweaks.config.PvpTweaksConfig.SOUNDS_DIR.resolve(fileName);
            if (java.nio.file.Files.exists(soundFile)) {
                final java.nio.file.Path fp = soundFile;
                return () -> java.nio.file.Files.newInputStream(fp);
            }
            return null;
        }
        if (id.getNamespace().equals("pvptweaks") && id.getPath().equals("sounds.json")) {
            return buildSoundsJson();
        }
        if (!id.getNamespace().equals("minecraft")) return null;

        String preset = PvpTweaksConfig.get().firePreset;
        if (preset == null || preset.equals("vanilla")) return null;

        String path = id.getPath();
        String res = null;

        if (path.equals("models/block/template_fire_floor.json"))
            res = "/assets/pvptweaks/models/fire/" + preset + "/template_fire_floor.json";
        else if (path.equals("models/block/template_fire_side.json"))
            res = "/assets/pvptweaks/models/fire/" + preset + "/template_fire_side.json";
        else if (path.equals("models/block/template_fire_side_alt.json"))
            res = "/assets/pvptweaks/models/fire/" + preset + "/template_fire_side_alt.json";
        else if (path.equals("models/block/template_fire_up.json"))
            res = "/assets/pvptweaks/models/fire/" + preset + "/template_fire_up.json";
        else if (path.equals("models/block/template_fire_up_alt.json"))
            res = "/assets/pvptweaks/models/fire/" + preset + "/template_fire_up_alt.json";

        if (res == null) return null;
        final String finalRes = res;
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] FirePack SERVING: {}", finalRes);
        return () -> {
            InputStream is = PvpTweaksDynamicPack.class.getResourceAsStream(finalRes);
            if (is == null) throw new IOException("Missing: " + finalRes);
            return is;
        };
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        if (type != ResourceType.CLIENT_RESOURCES) return;
        if (!namespace.equals("minecraft")) return;
        String preset = PvpTweaksConfig.get().firePreset;
        if (preset == null || preset.equals("vanilla")) return;

        // Report all fire models so the loader knows we have them
        for (String m : new String[]{
            "models/block/template_fire_floor.json",
            "models/block/template_fire_side.json",
            "models/block/template_fire_side_alt.json",
            "models/block/template_fire_up.json",
            "models/block/template_fire_up_alt.json"
        }) {
            if (m.startsWith(prefix)) {
                Identifier id = Identifier.of("minecraft", m);
                InputSupplier<InputStream> sup = open(type, id);
                if (sup != null) consumer.accept(id, sup);
            }
        }
    }

    @Override public Set<String> getNamespaces(ResourceType type) {
        return type == ResourceType.CLIENT_RESOURCES ? Set.of("minecraft", "pvptweaks") : Set.of();
    }
    @Override public <T> T parseMetadata(ResourceMetadataSerializer<T> s) throws IOException { return null; }
    @Override public ResourcePackInfo getInfo() { return INFO; }
    private InputSupplier<InputStream> buildSoundsJson() {
        java.nio.file.Path dir = com.pvptweaks.config.PvpTweaksConfig.SOUNDS_DIR;
        StringBuilder sb = new StringBuilder("{");
        try {
            if (java.nio.file.Files.exists(dir)) {
                java.util.List<java.nio.file.Path> files = java.nio.file.Files.list(dir)
                    .filter(p -> p.toString().endsWith(".ogg"))
                    .collect(java.util.stream.Collectors.toList());
                for (int i = 0; i < files.size(); i++) {
                    String name = files.get(i).getFileName().toString();
                    String key  = name.substring(0, name.length() - 4);
                    // "custom/key": {"sounds": [{"name":"pvptweaks:custom/key","stream":false}]}
                    sb.append("\"custom/").append(key)
                      .append("\":{\"sounds\":[{\"name\":\"pvptweaks:custom/")
                      .append(key).append("\",\"stream\":false}]}");
                    if (i < files.size() - 1) sb.append(",");
                }
            }
        } catch (Exception e) {
            com.pvptweaks.PvpTweaksMod.LOGGER.error("[PVP Tweaks] buildSoundsJson: {}", e.getMessage());
        }
        sb.append("}");
        byte[] bytes = sb.toString().getBytes();
        com.pvptweaks.PvpTweaksMod.LOGGER.info("[PVP Tweaks] sounds.json: {}", new String(bytes));
        return () -> new java.io.ByteArrayInputStream(bytes);
    }
    @Override public void close() {}
}
