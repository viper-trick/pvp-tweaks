package com.pvptweaks.resources;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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

public class PvpTweaksDynamicPack implements PackResources {
    public static PvpTweaksDynamicPack INSTANCE;

    public PvpTweaksDynamicPack() {
        INSTANCE = this;
        System.out.println("[PVP Tweaks] DynamicPack instance created!");
    }

    private static final PackLocationInfo INFO = new PackLocationInfo(
        "pvptweaks:dynamic", Component.literal("PVP Tweaks Dynamic"),
        PackSource.BUILT_IN, Optional.empty()
    );

    @Override
    public IoSupplier<InputStream> getRootResource(String... segments) {
        if (segments.length == 1 && segments[0].equals("pack.mcmeta")) {
            String mcmeta = "{\"pack\":{\"pack_format\":34,\"description\":\"PVP Tweaks Dynamic Sounds\"}}";
            return () -> new ByteArrayInputStream(mcmeta.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, Identifier id) {
        if (type != PackType.CLIENT_RESOURCES) return null;

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
                case "textures/block/fire_0.png" -> "/assets/pvptweaks/textures/block/fire_" + preset + "_0.png";
                case "textures/block/fire_1.png" -> "/assets/pvptweaks/textures/block/fire_" + preset + "_1.png";
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
                if (is == null) {
                    PvpTweaksMod.LOGGER.warn("[PVP Tweaks] Missing resource: {}", finalRes);
                    // Fallback: return 1x1 transparent pixel PNG
                    byte[] transparentPng = new byte[]{
                        (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
                        0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk
                        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                        0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte)0xC4,
                        0x00, 0x00, 0x00, 0x0D, 0x49, 0x44, 0x41, 0x54, // IDAT chunk
                        0x78, (byte)0x9C, 0x62, 0x60, 0x00, 0x00, 0x00, 0x02,
                        0x00, 0x01, (byte)0xE5, 0x27, (byte)0xDE, (byte)0xFC,
                        0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, // IEND chunk
                        (byte)0xAE, 0x42, 0x60, (byte)0x82
                    };
                    return new ByteArrayInputStream(transparentPng);
                }
                return is;
            };
        }
        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String prefix, PackResources.ResourceOutput consumer) {
        if (type != PackType.CLIENT_RESOURCES) return;

        if (namespace.equals("pvptweaks")) {
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] findResources called: namespace={}, prefix={}", namespace, prefix);
            
            // Serve sounds.json if it matches prefix
            if ("sounds.json".startsWith(prefix)) {
                PvpTweaksMod.LOGGER.info("[PVP Tweaks] Providing sounds.json via findResources");
                consumer.accept(Identifier.fromNamespaceAndPath("pvptweaks", "sounds.json"), buildSoundsJson());
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
                                Identifier id = Identifier.fromNamespaceAndPath("pvptweaks", relPath);
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
            "textures/block/fire_0.png",
            "textures/block/fire_1.png",
            "models/block/template_fire_floor.json",
            "models/block/template_fire_side.json",
            "models/block/template_fire_side_alt.json",
            "models/block/template_fire_up.json",
            "models/block/template_fire_up_alt.json"
        }) {
            if (m.startsWith(prefix)) {
                Identifier id = Identifier.fromNamespaceAndPath("minecraft", m);
                IoSupplier<InputStream> sup = getResource(type, id);
                if (sup != null) consumer.accept(id, sup);
            }
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? Set.of("minecraft", "pvptweaks") : Set.of();
    }

    @Override public <T> T getMetadataSection(MetadataSectionType<T> s) throws IOException { return null; }
    @Override public PackLocationInfo location() { return INFO; }
    @Override public void close() {}

    private IoSupplier<InputStream> buildSoundsJson() {
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
