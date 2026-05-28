package com.pvptweaks.gui;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AddSoundScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget pathField;
    private String statusMsg = "";
    private long statusTime = 0;
    private volatile boolean importing = false;

    public AddSoundScreen(Screen parent) {
        super(Text.literal("Import Sound"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int panelY = height / 2 - 90;

        // Path / URL input
        pathField = new TextFieldWidget(textRenderer, cx - 155, panelY + 30, 310, 20,
                Text.literal("File path or URL"));
        pathField.setMaxLength(1024);
        addSelectableChild(pathField);

        int row1 = panelY + 58;
        // Choose from Files (large, prominent)
        addDrawableChild(new ModernButtonWidget(cx - 155, row1, 310, 24,
                Text.literal("\uD83D\uDCC2 Choose from Files"), this::openFilePicker));

        int row2 = row1 + 32;
        addDrawableChild(new ModernButtonWidget(cx - 155, row2, 150, 20,
                Text.literal("\uD83D\uDCCB Paste Clipboard"), () -> {
            String clip = client.keyboard.getClipboard();
            if (clip != null && !clip.isEmpty()) {
                pathField.setText(clip.trim());
                setStatus("\u00a7eReady. Click Import & Save.");
            }
        }));

        addDrawableChild(new ModernButtonWidget(cx + 5, row2, 150, 20,
                Text.literal("\uD83D\uDCC1 Open Sounds Folder"), () -> {
            try { Files.createDirectories(PvpTweaksConfig.SOUNDS_DIR); } catch (Exception ignored) {}
            net.minecraft.util.Util.getOperatingSystem().open(PvpTweaksConfig.SOUNDS_DIR.toFile());
        }));

        int row3 = row2 + 28;
        // Import & Save — the main action
        addDrawableChild(new ModernButtonWidget(cx - 155, row3, 310, 24,
                Text.literal("\u00a7a\u2714 Import & Save"), () -> {
            String input = pathField.getText().trim();
            if (input.isEmpty()) { setStatus("\u00a7cNo path entered!"); return; }
            importAndSave(input);
        }));

        int row4 = row3 + 32;
        addDrawableChild(new ModernButtonWidget(cx - 75, row4, 150, 20,
                Text.literal("\u2716 Cancel"), () -> client.setScreen(parent)));
    }

    /** Vanilla drag-and-drop (may be blocked in Flatpak — Browse button is the fallback). */
    @Override
    public void onFilesDropped(List<Path> paths) {
        if (!paths.isEmpty()) {
            pathField.setText(paths.get(0).toAbsolutePath().toString());
            setStatus("\u00a7eFile dropped. Click Import & Save.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Cross-platform file picker
    // ─────────────────────────────────────────────────────────────────────────

    private void openFilePicker() {
        if (importing) return;
        setStatus("\u00a7eOpening file picker…");
        new Thread(() -> {
            String picked = null;

            // 1. Swing JFileChooser — universal Java, works on Windows/Mac/Linux
            picked = swingPicker();

            // 2. Flatpak portal zenity
            if (picked == null) picked = proc("flatpak-spawn", "--host", "zenity",
                    "--file-selection", "--title=Select Audio File",
                    "--file-filter=Audio files (*.ogg *.wav *.mp3) | *.ogg *.wav *.mp3");

            // 3. Plain zenity (non-Flatpak Linux)
            if (picked == null) picked = proc("zenity", "--file-selection",
                    "--title=Select Audio File",
                    "--file-filter=Audio files (*.ogg *.wav *.mp3) | *.ogg *.wav *.mp3");

            // 4. kdialog (KDE)
            if (picked == null) picked = proc("kdialog", "--getopenfilename", ".",
                    "*.ogg *.wav *.mp3|Audio files");

            // 5. macOS
            if (picked == null) {
                String raw = proc("osascript", "-e",
                        "POSIX path of (choose file with prompt \"Select Audio File\")");
                if (raw != null) picked = raw.trim();
            }

            // 6. Windows PowerShell
            if (picked == null) {
                String raw = proc("powershell", "-Command",
                        "[Reflection.Assembly]::LoadWithPartialName('System.Windows.Forms')|Out-Null;" +
                        "$f=New-Object System.Windows.Forms.OpenFileDialog;" +
                        "$f.Filter='Audio files (*.ogg;*.wav;*.mp3)|*.ogg;*.wav;*.mp3';" +
                        "if($f.ShowDialog() -eq 'OK'){$f.FileName}");
                if (raw != null) picked = raw.trim();
            }

            final String result = picked;
            MinecraftClient.getInstance().execute(() -> {
                if (result != null && !result.isEmpty()) {
                    pathField.setText(result);
                    setStatus("\u00a7eSelected. Click Import & Save.");
                } else {
                    setStatus("\u00a77No file selected. You can paste a path manually.");
                }
            });
        }, "PvpTweaks-FilePicker").start();
    }

    /** Swing JFileChooser — runs in its own daemon thread to avoid EDT/LWJGL conflicts. */
    private String swingPicker() {
        final String[] result = {null};
        try {
            Thread t = new Thread(() -> {
                try {
                    System.setProperty("java.awt.headless", "false");
                    javax.swing.UIManager.setLookAndFeel(
                            javax.swing.UIManager.getSystemLookAndFeelClassName());
                    javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
                    fc.setDialogTitle("Select Audio File");
                    fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                            "Audio files (*.ogg, *.wav, *.mp3)", "ogg", "wav", "mp3"));
                    fc.setAcceptAllFileFilterUsed(false);
                    if (fc.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION
                            && fc.getSelectedFile() != null) {
                        result[0] = fc.getSelectedFile().getAbsolutePath();
                    }
                } catch (Throwable ignored) {}
            }, "PvpTweaks-Swing");
            t.setDaemon(true);
            t.start();
            t.join(30_000);
        } catch (Throwable ignored) {}
        return result[0];
    }

    /** Run a process, return the first stdout line on exit-0, else null. */
    private String proc(String... cmd) {
        try {
            Process p = new ProcessBuilder(cmd).start();
            String line;
            try (java.io.BufferedReader r = new java.io.BufferedReader(
                    new java.io.InputStreamReader(p.getInputStream()))) {
                line = r.readLine();
            }
            if (p.waitFor() == 0 && line != null && !line.isBlank()) return line.trim();
        } catch (Throwable ignored) {}
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Import & Save
    // ─────────────────────────────────────────────────────────────────────────

    private void importAndSave(String input) {
        if (importing) return;
        importing = true;
        setStatus("\u00a7eImporting…");

        new Thread(() -> {
            try {
                Path dest;
                if (input.startsWith("http://") || input.startsWith("https://")) {
                    URI uri = URI.create(input);
                    String name = input.substring(input.lastIndexOf('/') + 1);
                    if (!name.matches("(?i).*\\.(ogg|wav|mp3)$")) name += ".ogg";
                    dest = PvpTweaksConfig.SOUNDS_DIR.resolve(name);
                    Files.createDirectories(dest.getParent());
                    try (InputStream in = uri.toURL().openStream()) {
                        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                } else {
                    String p = input;
                    if (p.startsWith("file://")) p = new URI(p).getPath();
                    if (p.startsWith("\"") && p.endsWith("\"")) p = p.substring(1, p.length() - 1);
                    Path src = Path.of(p);
                    if (!Files.exists(src)) {
                        setStatus("\u00a7cFile not found: " + p);
                        importing = false;
                        return;
                    }
                    dest = PvpTweaksConfig.SOUNDS_DIR.resolve(src.getFileName().toString());
                    Files.createDirectories(dest.getParent());
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                }

                PvpTweaksMod.LOGGER.info("[PVP Tweaks] Imported sound to {}", dest);
                final String absPath = dest.toAbsolutePath().toString();

                MinecraftClient.getInstance().execute(() -> {
                    // Ask the parent picker to select + register + save this file
                    if (parent instanceof ModernSoundPickerScreen m) {
                        m.importAndApply(absPath);
                    } else if (parent instanceof SoundPickerScreen s) {
                        s.importAndApply(absPath);
                    } else {
                        setStatus("\u00a7aImported! Go to Custom tab and select the file.");
                        client.setScreen(parent);
                    }
                });
            } catch (Exception e) {
                PvpTweaksMod.LOGGER.error("[PVP Tweaks] Import failed", e);
                setStatus("\u00a7cError: " + e.getMessage());
                importing = false;
            }
        }, "PvpTweaks-Importer").start();
    }

    private void setStatus(String msg) {
        MinecraftClient.getInstance().execute(() -> {
            this.statusMsg = msg;
            this.statusTime = System.currentTimeMillis();
        });
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        int cx = width / 2;
        int panelX = cx - 175, panelY = height / 2 - 95, panelW = 350, panelH = 240;

        ctx.fill(0, 0, width, height, 0xBB000000);
        RenderUtils.drawRoundedRect(ctx, panelX, panelY, panelW, panelH, 6, 0xFF1A1A2E);
        RenderUtils.drawOutline(ctx, panelX, panelY, panelW, panelH, 1, UiPalette.BORDER);

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("\u00a7b\u00a7lImport Custom Sound"), cx, panelY + 10, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("\u00a78Drag & drop a file here, or use the buttons below"),
                cx, panelY + 22, 0xAAAAAA);
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("\u00a77Path / URL:"), cx - 155, panelY + 15, 0xAAAAAA);

        pathField.render(ctx, mx, my, delta);
        super.render(ctx, mx, my, delta);

        if (!statusMsg.isEmpty() && System.currentTimeMillis() - statusTime < 5000) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                    Text.literal(statusMsg), cx, height / 2 + 105, 0xFFFFFF);
        }
    }
}
