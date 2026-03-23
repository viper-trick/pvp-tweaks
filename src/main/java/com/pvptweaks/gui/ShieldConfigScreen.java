package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ShieldConfigScreen extends Screen {

    private final Screen parent;

    private interface FloatGetter { float get(PvpTweaksConfig c); }
    private interface FloatSetter { void set(PvpTweaksConfig c, float v); }
    private record Row(String label, String hint, FloatGetter get, FloatSetter set, float step) {}

    private static final Row[] ROWS = {
        new Row("Offset X",  "< LEFT   > RIGHT",     c -> c.shieldOffsetX, (c,v) -> c.shieldOffsetX = (int)v, 0.01f),
        new Row("Offset Y",  "< DOWN   > UP",         c -> c.shieldOffsetY, (c,v) -> c.shieldOffsetY = (int)v, 0.01f),
        new Row("Offset Z",  "< CLOSER  > FURTHER",   c -> c.shieldOffsetZ, (c,v) -> c.shieldOffsetZ = (int)v, 0.01f),
        new Row("Rotation X","< BACK   > FORWARD",    c -> c.shieldRotX,    (c,v) -> c.shieldRotX = (int)v, 1.0f),
        new Row("Rotation Y","< TURN LEFT > TURN RIGHT", c -> c.shieldRotY, (c,v) -> c.shieldRotY = (int)v, 1.0f),
        new Row("Rotation Z","< ROLL LEFT > ROLL RIGHT", c -> c.shieldRotZ, (c,v) -> c.shieldRotZ = (int)v, 1.0f),
        new Row("Scale %",   "< SMALLER  > BIGGER",   c -> c.shieldScalePct,(c,v) -> c.shieldScalePct = (int)v, 5f),
    };

    private static final int ROW_H   = 36;
    private static final int START_Y = 44;

    public ShieldConfigScreen(Screen parent) {
        super(Text.literal("Shield Adjuster"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        for (int i = 0; i < ROWS.length; i++) {
            final int   idx = i;
            final float st  = ROWS[i].step;
            int btnY = START_Y + idx * ROW_H + 18;

            addDrawableChild(ButtonWidget.builder(Text.literal("<<"),
                btn -> adjust(idx, -st * 10)).dimensions(8, btnY, 28, 16).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("<"),
                btn -> adjust(idx, -st)).dimensions(40, btnY, 22, 16).build());
            addDrawableChild(ButtonWidget.builder(Text.literal(">"),
                btn -> adjust(idx,  st)).dimensions(this.width - 108, btnY, 22, 16).build());
            addDrawableChild(ButtonWidget.builder(Text.literal(">>"),
                btn -> adjust(idx,  st * 10)).dimensions(this.width - 82, btnY, 28, 16).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Reset"),
                btn -> reset(idx)).dimensions(this.width - 50, btnY, 46, 16).build());
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"),
            btn -> { PvpTweaksConfig.save(); client.setScreen(parent); })
            .dimensions(this.width / 2 - 50, this.height - 26, 100, 20).build());
    }

    private void adjust(int idx, float delta) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float next = Math.round((ROWS[idx].get.get(cfg) + delta) * 1000f) / 1000f;
        ROWS[idx].set.set(cfg, next);
        PvpTweaksConfig.save();
    }

    private void reset(int idx) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        ROWS[idx].set.set(cfg, ROWS[idx].label.startsWith("Scale") ? 100f : 0f);
        PvpTweaksConfig.save();
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        // skip vanilla blur — just dim
        ctx.fill(0, 0, this.width, this.height, 0xAA000000);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawDeferredElements();

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int midX = this.width / 2;

        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00a76\u2756 Shield Adjuster  \u00a77(H=close, << / >> = x10)"),
            midX, 8, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00a77Changes apply instantly while screen is open"),
            midX, 20, 0xFFFFFF);

        for (int i = 0; i < ROWS.length; i++) {
            float  val    = ROWS[i].get.get(cfg);
            int    y      = START_Y + i * ROW_H;
            String label  = ROWS[i].label;
            String valStr = label.startsWith("Scale")
                ? String.format("%d%%", (int) val)
                : String.format("%.3f", val);

            ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("\u00a7a" + label + "  \u00a7e" + valStr),
                midX, y + 2, 0xFFFFFF);
            ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("\u00a7f" + ROWS[i].hint),
                midX, y + 13, 0xFFFFFF);
        }
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (input.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_H) {
            PvpTweaksConfig.save();
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(input);
    }
}
