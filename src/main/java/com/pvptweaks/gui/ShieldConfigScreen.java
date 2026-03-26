package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Live shield adjuster.
 * RIGHT half = dark control panel.
 * LEFT  half = transparent, live game shows through (shouldPause=false).
 * Colors: 0xFFrrggbb (ARGB required in MC 1.21.6+).
 */
public class ShieldConfigScreen extends Screen {

    private final Screen parent;

    private interface FloatGetter { float get(PvpTweaksConfig c); }
    private interface FloatSetter { void set(PvpTweaksConfig c, float v); }

    private record Row(
        String label,
        FloatGetter get, FloatSetter set,
        float step, float defaultVal
    ) {}

    private static final Row[] ROWS = {
        new Row("Offset X",
            c -> c.shieldOffsetX, (c,v) -> c.shieldOffsetX = (int)v, 1f, 0f),
        new Row("Offset Y",
            c -> c.shieldOffsetY, (c,v) -> c.shieldOffsetY = (int)v, 1f, 0f),
        new Row("Offset Z",
            c -> c.shieldOffsetZ, (c,v) -> c.shieldOffsetZ = (int)v, 1f, 0f),
        new Row("Rotation X",
            c -> c.shieldRotX,    (c,v) -> c.shieldRotX    = (int)v, 1f, 0f),
        new Row("Rotation Y",
            c -> c.shieldRotY,    (c,v) -> c.shieldRotY    = (int)v, 1f, 0f),
        new Row("Rotation Z",
            c -> c.shieldRotZ,    (c,v) -> c.shieldRotZ    = (int)v, 1f, 0f),
        new Row("Scale %",
            c -> c.shieldScalePct,(c,v) -> c.shieldScalePct= (int)v, 5f, 100f),
    };

    private static final int PANEL_W = 260;
    private static final int ROW_H   = 28;
    private static final int START_Y = 40;

    private static final int COL_WHITE  = 0xFFFFFFFF;
    private static final int COL_GREY   = 0xFFAAAAAA;
    private static final int COL_DGREY  = 0xFF888888;
    private static final int COL_YELLOW = 0xFFFFFF55;
    private static final int COL_CYAN   = 0xFF55FFFF;
    private static final int COL_GOLD   = 0xFFFFAA00;

    public ShieldConfigScreen(Screen parent) {
        super(Text.literal("Shield Adjuster"));
        this.parent = parent;
    }

    private int px() { return this.width - PANEL_W; }

    @Override
    protected void init() {
        int px = px();
        for (int i = 0; i < ROWS.length; i++) {
            final int   idx = i;
            final float st  = ROWS[i].step;
            int btnY = START_Y + idx * ROW_H + 14;
            int right = this.width - 3;

            addDrawableChild(ButtonWidget.builder(Text.literal("Reset"),
                b -> reset(idx)).dimensions(right - 40, btnY, 40, 12).build());
            addDrawableChild(ButtonWidget.builder(Text.literal(">>"),
                b -> adjust(idx, +st * 10)).dimensions(right - 68, btnY, 24, 12).build());
            addDrawableChild(ButtonWidget.builder(Text.literal(">"),
                b -> adjust(idx, +st)).dimensions(right - 90, btnY, 20, 12).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("<"),
                b -> adjust(idx, -st)).dimensions(right - 114, btnY, 20, 12).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("<<"),
                b -> adjust(idx, -st * 10)).dimensions(right - 138, btnY, 24, 12).build());
        }
        int botY = this.height - 24;
        int px2  = px();
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset All"),
            b -> resetAll()).dimensions(px2 + 4, botY, 72, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"),
            b -> { PvpTweaksConfig.save(); client.setScreen(parent); })
            .dimensions(px2 + 82, botY, 72, 18).build());
    }

    private void adjust(int idx, float delta) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float next = Math.round((ROWS[idx].get.get(cfg) + delta) * 1000f) / 1000f;
        ROWS[idx].set.set(cfg, next);
        PvpTweaksConfig.save();
    }

    private void reset(int idx) {
        ROWS[idx].set.set(PvpTweaksConfig.get(), ROWS[idx].defaultVal);
        PvpTweaksConfig.save();
    }

    private void resetAll() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        for (Row row : ROWS) row.set.set(cfg, row.defaultVal);
        PvpTweaksConfig.save();
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        int px = px();
        ctx.fill(px - 2, 0, this.width, this.height, 0xCC000000);
        ctx.fill(px - 3, 0, px - 2, this.height, 0x55FFFFFF);
        // LEFT — no fill, live game shows through
        int lx = 6, ly = 14, lw = px - 16, lh = this.height - 28;
        int bc = 0x44AAAAAA;
        ctx.fill(lx,    ly,    lx+lw,   ly+1,    bc);
        ctx.fill(lx,    ly+lh, lx+lw,   ly+lh+1, bc);
        ctx.fill(lx,    ly,    lx+1,    ly+lh,   bc);
        ctx.fill(lx+lw, ly,    lx+lw+1, ly+lh,   bc);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawDeferredElements();

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int px  = px();
        int mid = px + PANEL_W / 2;

        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00a76\u2756 Shield Adjuster"), mid, 6, COL_GOLD);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("H = close   << / >> = x10"), mid, 17, COL_GREY);

        for (int i = 0; i < ROWS.length; i++) {
            float  val   = ROWS[i].get.get(cfg);
            int    y     = START_Y + i * ROW_H + 3;
            String label = ROWS[i].label;
            String valStr = label.startsWith("Scale")
                ? String.format("%d%%", (int) val)
                : String.format("%+.2f", val / 100f);
            int valColor = (val == ROWS[i].defaultVal) ? COL_GREY : COL_YELLOW;

            ctx.drawTextWithShadow(textRenderer,
                Text.literal("\u00a7b" + label), px + 6, y, COL_CYAN);
            ctx.drawTextWithShadow(textRenderer,
                Text.literal(valStr), px + 90, y, valColor);
        }

        int lmid = (px - 6) / 2;
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00bb Live Preview \u00ab"), lmid, 20, COL_GREY);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Hold shield in off-hand"), lmid, 32, COL_DGREY);
        ctx.drawItem(new ItemStack(Items.SHIELD), lmid - 8, this.height / 2 - 36);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Changes apply instantly"), lmid, this.height / 2 - 14, COL_DGREY);
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
