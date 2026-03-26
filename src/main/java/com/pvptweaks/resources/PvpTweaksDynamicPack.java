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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PvpTweaksDynamicPack implements ResourcePack {

    private static final ResourcePackInfo INFO = new ResourcePackInfo(
        "pvptweaks:dynamic", Text.literal("PVP Tweaks Dynamic"),
        ResourcePackSource.BUILTIN, Optional.empty()
    );

    @Override public InputSupplier<InputStream> openRoot(String... segments) { return null; }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if (type != ResourceType.CLIENT_RESOURCES) return null;

        String ns   = id.getNamespace();
        String path = id.getPath();

        if (ns.equals("pvptweaks")) {
            if (path.equals("sounds.json")) return buildSoundsJson();
            if (path.startsWith("sounds/custom/")) {
                String fileName = path.substring("sounds/custom/".length());
                Path file = PvpTweaksConfig.SOUNDS_DIR.resolve(fileName);
                if (Files.exists(file)) return () -> Files.newInputStream(file);
                PvpTweaksMod.LOGGER.warn("[PVP Tweaks] ogg not found on disk: {}", file);
                return null;
            }
            return null;
        }

        if (ns.equals("minecraft")) {
            String preset = PvpTweaksConfig.get().firePreset;
            if (preset == null || preset.equals("vanilla")) return null;
            String res = switch (path) {
                case "models/block/template_fire_floor.json"    -> "/assets/pvptweaks/models/fire/" + preset + "/template_fire_floor.json";
                case "models/block/template_fire_side.json"     -> "/assets/pvptweaks/models/fire/" + preset + "/template_fire_side.json";
                case "models/block/template_fire_side_alt.json" -> "/assets/pvptweaks/models/fire/" + preset + "/template_fire_side_alt.json";
                case "models/block/template_fire_up.json"       -> "/assets/pvptweaks/models/fire/" + preset + "/template_fire_up.json";
                case "models/block/template_fire_up_alt.json"   -> "/assets/pvptweaks/models/fire/" + preset + "/template_fire_up_alt.json";
                default -> null;
            };
            if (res == null) return null;
            final String finalRes = res;
            return () -> {
                InputStream is = PvpTweaksDynamicPack.class.getResourceAsStream(finalRes);
                if (is == null) throw new IOException("Missing: " + finalRes);
                return is;
            };
        }
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        if (type != ResourceType.CLIENT_RESOURCES) return;

        if (namespace.equals("pvptweaks")) {
            // CRITICAL: SoundManager.prepare() calls findResources("pvptweaks", "sounds.json", ...)
            // to discover sound registries. Without this branch pvptweaks sounds are never loaded.
            if ("sounds.json".startsWith(prefix)) {
                Identifier sid = Identifier.of("pvptweaks", "sounds.json");
                InputSupplier<InputStream> sup = buildSoundsJson();
                if (sup != null) {
                    PvpTweaksMod.LOGGER.info("[PVP Tweaks] findResources: exposing pvptweaks:sounds.json");
                    consumer.accept(sid, sup);
                }
            }
            Path dir = PvpTweaksConfig.SOUNDS_DIR;
            if (Files.exists(dir)) {
                try {
                    Files.list(dir)
                        .filter(p -> p.getFileName().toString().endsWith(".ogg"))
                        .forEach(p -> {
                            String soundPath = "sounds/custom/" + p.getFileName().toString();
                            if (soundPath.startsWith(prefix)) {
                                Identifier id = Identifier.of("pvptweaks", soundPath);
                                consumer.accept(id, () -> Files.newInputStream(p));
                            }
                        });
                } catch (Exception e) {
                    PvpTweaksMod.LOGGER.error("[PVP Tweaks] findResources error: {}", e.getMessage());
                }
            }
            return;
        }

        if (!namespace.equals("minecraft")) return;
        String preset = PvpTweaksConfig.get().firePreset;
        if (preset == null || preset.equals("vanilla")) return;
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

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return type == ResourceType.CLIENT_RESOURCES ? Set.of("minecraft", "pvptweaks") : Set.of();
    }

    @Override public <T> T parseMetadata(ResourceMetadataSerializer<T> s) throws IOException { return null; }
    @Override public ResourcePackInfo getInfo() { return INFO; }
    @Override public void close() {}

    private InputSupplier<InputStream> buildSoundsJson() {
        Path dir = PvpTweaksConfig.SOUNDS_DIR;
        StringBuilder sb = new StringBuilder("{");
        try {
            if (Files.exists(dir)) {
                List<Path> files = Files.list(dir)
                    .filter(p -> p.getFileName().toString().endsWith(".ogg"))
                    .sorted().collect(Collectors.toList());
                for (int i = 0; i < files.size(); i++) {
                    String name = files.get(i).getFileName().toString();
                    String key  = name.substring(0, name.length() - 4);
                    sb.append("\"custom/").append(key).append("\":")
                      .append("{\"sounds\":[{\"name\":\"pvptweaks:custom/")
                      .append(key).append("\",\"stream\":false}]}");
                    if (i < files.size() - 1) sb.append(",");
                }
            }
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] buildSoundsJson: {}", e.getMessage());
        }
        sb.append("}");
        String json = sb.toString();
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] sounds.json: {}", json);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }
}
