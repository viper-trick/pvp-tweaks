package com.pvptweaks.gui;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.sound.AudioEditor;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import java.nio.file.Files;
import java.nio.file.Path;

public class SoundEditorScreen extends Screen {

    private final Screen parent;
    private final String originalId;
    private final String absolutePath;

    private RangeSliderWidget rangeSlider;
    private CustomSliderWidget speedSlider;
    private EditBox nameField;
    private String statusMsg = "";
    private double duration = 0.0;

    private double speedVal = 1.0;
    private double startVal = 0.0;
    private double endVal = 1.0;
    
    private static Clip previewClip = null;
    private boolean processing = false;
    private Path origPath;
    private boolean firstInit = true;

    public SoundEditorScreen(Screen parent, String originalId, String absolutePath) {
        super(Component.literal("Sound Editor"));
        this.parent = parent;
        this.originalId = originalId;
        this.absolutePath = absolutePath;
    }

    @Override
    protected void init() {
        origPath = PvpTweaksConfig.SOUNDS_DIR.resolve(originalId.replace(".ogg", ".orig"));
        Path sourcePath = Files.exists(origPath) ? origPath : Path.of(absolutePath);
        
        duration = AudioEditor.getDuration(sourcePath);
        if (duration <= 0) duration = 1.0; 

        Path jsonPath = PvpTweaksConfig.SOUNDS_DIR.resolve(originalId.replace(".ogg", ".json"));
        if (firstInit && Files.exists(jsonPath)) {
            try {
                String json = Files.readString(jsonPath);
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                if (obj.has("start")) startVal = obj.get("start").getAsDouble() / duration;
                if (obj.has("end")) endVal = obj.get("end").getAsDouble() / duration;
                if (obj.has("speed")) speedVal = obj.get("speed").getAsDouble();
            } catch (Exception ignored) { }
        }
        firstInit = false;

        int cx = width / 2;
        int sy = 70;

        rangeSlider = new RangeSliderWidget(cx - 150, sy, 300, 24, duration, startVal, endVal);
        addRenderableWidget(rangeSlider);

        speedSlider = new CustomSliderWidget(cx - 150, sy + 35, 275, 20, "Speed", speedVal, 0.1, 3.0, false, v -> speedVal = v);
        addRenderableWidget(speedSlider);
        addRenderableWidget(new ModernButtonWidget(cx + 130, sy + 35, 20, 20, Component.literal("\u21ba"), () -> {
            speedVal = 1.0;
            this.clearWidgets();
            this.init();
        }));

        nameField = new EditBox(font, cx - 150, sy + 65, 300, 20, Component.literal("Name"));
        nameField.setValue(originalId);
        addRenderableWidget(nameField);

        int by = height - 45;
        addRenderableWidget(new ModernButtonWidget(cx - 150, by - 30, 300, 20, Component.literal("\u25b6 Preview Edits"), () -> previewEdits()));
        addRenderableWidget(new ModernButtonWidget(cx - 150, by, 95, 20, Component.literal("\u2714 Save As"), () -> applyEdits(false)));
        addRenderableWidget(new ModernButtonWidget(cx - 45, by, 95, 20, Component.literal("\ud83d\udbe0 Overwrite"), () -> applyEdits(true)));
        addRenderableWidget(new ModernButtonWidget(cx + 60, by, 90, 20, Component.literal("Cancel"), () -> closeScreen()));
    }
    
    private void closeScreen() {
        if (previewClip != null && previewClip.isOpen()) {
            previewClip.stop();
            previewClip.close();
        }
        minecraft.setScreen(parent);
    }

    private void previewEdits() {
        if (processing) return;
        processing = true;
        statusMsg = "§eProcessing preview...";

        new Thread(() -> {
            try {
                if (previewClip != null && previewClip.isOpen()) {
                    previewClip.stop();
                    previewClip.close();
                }

                double start = rangeSlider.getStartVal() * duration;
                double end = rangeSlider.getEndVal() * duration;
                double speed = speedVal;

                Path sourcePath = Files.exists(origPath) ? origPath : Path.of(absolutePath);
                Path tempOutput = PvpTweaksConfig.SOUNDS_DIR.resolve("temp_preview.wav");

                if (AudioEditor.processAudio(sourcePath, tempOutput, start, end, speed)) {
                    previewClip = AudioSystem.getClip();
                    previewClip.open(AudioSystem.getAudioInputStream(tempOutput.toFile()));
                    previewClip.start();
                    statusMsg = "§aPlaying preview...";
                } else {
                    statusMsg = "§cFailed to process audio.";
                }
            } catch (Exception e) {
                statusMsg = "§cError running preview.";
                PvpTweaksMod.LOGGER.error("Preview failed", e);
            } finally {
                processing = false;
            }
        }).start();
    }

    private void applyEdits(boolean overwrite) {
        if (processing) return;
        processing = true;
        statusMsg = "§eProcessing and saving audio...";

        new Thread(() -> {
            try {
                double start = rangeSlider.getStartVal() * duration;
                double end = rangeSlider.getEndVal() * duration;
                double speed = speedVal;
                
                String newName = nameField.getValue().trim();
                if (newName.isEmpty()) newName = originalId;
                if (!newName.endsWith(".ogg")) newName += ".ogg";

                Path sourcePath = Files.exists(origPath) ? origPath : Path.of(absolutePath);
                Path output = PvpTweaksConfig.SOUNDS_DIR.resolve(newName);
                Path backup = PvpTweaksConfig.SOUNDS_DIR.resolve(newName.replace(".ogg", ".orig"));
                Path jsonPath = PvpTweaksConfig.SOUNDS_DIR.resolve(newName.replace(".ogg", ".json"));

                if (!sourcePath.equals(backup)) {
                    Files.copy(sourcePath, backup, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
                obj.addProperty("start", start);
                obj.addProperty("end", end);
                obj.addProperty("speed", speed);
                Files.writeString(jsonPath, obj.toString());

                boolean success = AudioEditor.processAudio(backup, output, start, end, speed);
                if (success) {
                    net.minecraft.client.Minecraft.getInstance().execute(() -> {
                        if (parent instanceof ModernSoundPickerScreen picker) {
                            if (previewClip != null && previewClip.isOpen()) {
                                previewClip.stop();
                                previewClip.close();
                            }
                            picker.refreshCustomTab();
                            picker.selectAndSaveSound(output.toAbsolutePath().toString());
                        } else {
                            closeScreen();
                        }
                    });
                } else {
                    statusMsg = "§cFailed to process audio. Check inputs.";
                }
            } catch (Exception e) {
                statusMsg = "§cError saving edits.";
                PvpTweaksMod.LOGGER.error("Audio edit failed", e);
            } finally {
                processing = false;
            }
        }).start();
    }

    @Override
    public void render(GuiGraphics ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawOutline(ctx, 20, 20, width - 40, height - 40, 1, UiPalette.BORDER);
        
        ctx.drawCenteredString(font, Component.literal("\u00a7lCUSTOM SOUND EDITOR"), width / 2, 30, UiPalette.ACCENT_BLUE);
        ctx.drawCenteredString(font, Component.literal("Editing: \u00a7e" + originalId + " \u00a77(Duration: \u00a7a" + String.format("%.2f", duration) + "s\u00a77)"), width / 2, 45, UiPalette.TEXT_SECONDARY);

        if (!statusMsg.isEmpty()) {
            ctx.drawCenteredString(font, Component.literal(statusMsg), width / 2, height - 85, 0xFFFFFFFF);
        }
        super.render(ctx, mx, my, delta);
    }

    private static class RangeSliderWidget extends AbstractWidget {
        private double startVal;
        private double endVal;
        private boolean draggingStart = false;
        private boolean draggingEnd = false;
        private final double duration;

        public RangeSliderWidget(int x, int y, int width, int height, double duration, double startVal, double endVal) {
            super(x, y, width, height, Component.empty());
            this.duration = duration;
            this.startVal = startVal;
            this.endVal = endVal;
        }

        public double getStartVal() { return startVal; }
        public double getEndVal() { return endVal; }

        @Override
        public void renderWidget(GuiGraphics ctx, int mx, int my, float delta) {
            RenderUtils.drawRoundedRect(ctx, getX(), getY(), width, height, 4, 0xFF000000);
            
            int sX = getX() + (int)(startVal * width);
            int eX = getX() + (int)(endVal * width);
            RenderUtils.drawRoundedRect(ctx, sX, getY() + 2, eX - sX, height - 4, 2, 0xFF44AA44);

            ctx.fill(sX - 2, getY(), sX + 2, getY() + height, 0xFFFFFFFF);
            ctx.fill(eX - 2, getY(), eX + 2, getY() + height, 0xFFFFFFFF);

            String msg = String.format("Trim: %.2fs -> %.2fs", startVal * duration, endVal * duration);
            ctx.drawCenteredString(Minecraft.getInstance().font, Component.literal(msg), getX() + width / 2, getY() + (height - 8) / 2, 0xFFFFFFFF);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
            if (click.x() >= getX() && click.x() <= getX() + width && click.y() >= getY() && click.y() <= getY() + height) {
                int sX = getX() + (int)(startVal * width);
                int eX = getX() + (int)(endVal * width);
                if (Math.abs(click.x() - sX) <= Math.abs(click.x() - eX)) draggingStart = true;
                else draggingEnd = true;
                updateValuesFromMouse(click.x());
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent click) {
            draggingStart = false;
            draggingEnd = false;
            return super.mouseReleased(click);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
            if (draggingStart || draggingEnd) {
                updateValuesFromMouse(click.x());
                return true;
            }
            return super.mouseDragged(click, deltaX, deltaY);
        }

        private void updateValuesFromMouse(double mx) {
            double val = Mth.clamp((mx - getX()) / (double) width, 0.0, 1.0);
            if (draggingStart) startVal = Math.min(val, endVal - 0.01);
            else if (draggingEnd) endVal = Math.max(val, startVal + 0.01);
        }
        
        @Override
        protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput builder) {}
    }
}
