package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class RealmsScreen extends Screen {
	protected static final int field_33055 = 17;
	protected static final int field_33057 = 7;
	protected static final long MAX_FILE_SIZE = 5368709120L;
	/**
	 * Hex color {@code 0xFF4C4C4C}.
	 */
	protected static final int DARK_GRAY = -11776948;
	/**
	 * Hex color {@code 0xFF6C6C6C}.
	 */
	protected static final int GRAY = -9671572;
	/**
	 * Hex color {@code 0xFF7FFF7F}.
	 */
	protected static final int GREEN = -8388737;
	/**
	 * Hex color {@code 0xFF3366BB}
	 */
	protected static final int BLUE = -13408581;
	/**
	 * Hex color {@code 0xFF6C71C4}.
	 */
	protected static final int PURPLE = -9670204;
	protected static final int field_39676 = 32;
	protected static final int field_54866 = 8;
	protected static final Identifier REALMS_LOGO_TEXTURE = Identifier.ofVanilla("textures/gui/title/realms.png");
	protected static final int REALMS_LOGO_WIDGET_WIDTH = 128;
	protected static final int REALMS_LOGO_WIDGET_HEIGHT = 34;
	protected static final int REALMS_LOGO_TEXTURE_WIDTH = 128;
	protected static final int REALMS_LOGO_TEXTURE_HEIGHT = 64;
	private final List<RealmsLabel> labels = Lists.<RealmsLabel>newArrayList();

	public RealmsScreen(Text text) {
		super(text);
	}

	/**
	 * Moved from RealmsConstants in 20w10a
	 */
	protected static int row(int index) {
		return 40 + index * 13;
	}

	protected RealmsLabel addLabel(RealmsLabel label) {
		this.labels.add(label);
		return this.addDrawable(label);
	}

	public Text narrateLabels() {
		return ScreenTexts.joinLines((Collection<? extends Text>)this.labels.stream().map(RealmsLabel::getText).collect(Collectors.toList()));
	}

	protected static IconWidget createRealmsLogoIconWidget() {
		return IconWidget.create(128, 34, REALMS_LOGO_TEXTURE, 128, 64);
	}
}
