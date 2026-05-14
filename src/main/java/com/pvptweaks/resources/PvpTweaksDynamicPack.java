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
    public static PvpTweaksDynamicPack INSTANCE;

    public PvpTweaksDynamicPack() {
        INSTANCE = this;
        System.out.println("[PVP Tweaks] DynamicPack instance created!");
    }

    private static final ResourcePackInfo INFO = new ResourcePackInfo(
        "pvptweaks:dynamic", Text.literal("PVP Tweaks Dynamic"),
        ResourcePackSource.BUILTIN, Optional.empty()
    );

    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        if (segments.length == 1 && segments[0].equals("pack.mcmeta")) {
            String mcmeta = "{\"pack\":{\"pack_format\":34,\"description\":\"PVP Tweaks Dynamic Sounds\"}}";
            return () -> new ByteArrayInputStream(mcmeta.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if (type != ResourceType.CLIENT_RESOURCES) return null;

        String ns   = id.getNamespace();
        String path = id.getPath();

        if (ns.equals("pvptweaks")) {
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Resource requested: {}", id);
            if (path.equals("sounds.json")) {
                PvpTweaksMod.LOGGER.info("[PVP Tweaks] Serving dynamic sounds.json");
                return buildSoundsJson();
            }
            if (path.startsWith("sounds/custom/") && path.endsWith(".ogg")) {
                String requestedFn = path.substring("sounds/custom/".length()); // e.g. "my_sound.ogg"
                PvpTweaksMod.LOGGER.info("[PVP Tweaks] Sound resource requested: {}, file part: {}", id, requestedFn);
                Path dir = PvpTweaksConfig.SOUNDS_DIR;
                if (Files.exists(dir)) {
                    try {
                        List<Path> allOggs = Files.list(dir).filter(p -> p.getFileName().toString().toLowerCase().endsWith(".ogg")).toList();
                        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Found {} .ogg files in sounds dir", allOggs.size());
                        
                        Optional<Path> found = allOggs.stream()
                            .filter(p -> {
                                String fn = p.getFileName().toString().toLowerCase();
                                String base = fn.substring(0, fn.length() - 4);
                                String safe = base.replaceAll("[^a-z0-9_]", "_") + ".ogg";
                                return safe.equals(requestedFn);
                            })
                            .findFirst();
                        if (found.isPresent()) {
                            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Serving custom sound file: {} -> {}", path, found.get().getFileName());
                            return () -> Files.newInputStream(found.get());
                        } else {
                            PvpTweaksMod.LOGGER.warn("[PVP Tweaks] No match found for safe name: {}", requestedFn);
                        }
                    } catch (IOException e) {
                        PvpTweaksMod.LOGGER.error("[PVP Tweaks] Error reading sounds dir: {}", e.getMessage());
                    }
                }
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
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] findResources called: namespace={}, prefix={}", namespace, prefix);
            
            // Serve sounds.json if it matches prefix
            if ("sounds.json".startsWith(prefix)) {
                PvpTweaksMod.LOGGER.info("[PVP Tweaks] Providing sounds.json via findResources");
                consumer.accept(Identifier.of("pvptweaks", "sounds.json"), buildSoundsJson());
            }
            
            // Discover custom sounds
            Path dir = PvpTweaksConfig.SOUNDS_DIR;
            if (Files.exists(dir)) {
                try {
                    Files.list(dir)
                        .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".ogg"))
                        .forEach(p -> {
                            String fn = p.getFileName().toString().toLowerCase();
                            String base = fn.substring(0, fn.length() - 4);
                            String safe = base.replaceAll("[^a-z0-9_]", "_") + ".ogg";
                            String relPath = "sounds/custom/" + safe;
                            if (relPath.startsWith(prefix)) {
                                Identifier id = Identifier.of("pvptweaks", relPath);
                                PvpTweaksMod.LOGGER.info("[PVP Tweaks] Providing sound resource via findResources: {}", id);
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
        com.google.gson.JsonObject root = new com.google.gson.JsonObject();
        try {
            if (Files.exists(dir)) {
                List<Path> files = Files.list(dir)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".ogg"))
                    .sorted().collect(Collectors.toList());
                for (Path p : files) {
                    String name = p.getFileName().toString().toLowerCase();
                    String base = name.substring(0, name.length() - 4);
                    String safeId = base.replaceAll("[^a-z0-9_]", "_");
                    
                    com.google.gson.JsonObject entry = new com.google.gson.JsonObject();
                    com.google.gson.JsonArray sounds = new com.google.gson.JsonArray();
                    com.google.gson.JsonObject soundObj = new com.google.gson.JsonObject();
                    // name is relative to assets/pvptweaks/sounds/
                    soundObj.addProperty("name", "pvptweaks:custom/" + safeId);
                    soundObj.addProperty("stream", false);
                    sounds.add(soundObj);
                    entry.add("sounds", sounds);
                    
                    root.add("custom/" + safeId, entry);
                }
            }
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] buildSoundsJson error: {}", e.getMessage());
        }
        String json = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(root);
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Generated dynamic sounds.json for pvptweaks:\n{}", json);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }
}
