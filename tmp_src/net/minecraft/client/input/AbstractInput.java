package net.minecraft.client.input;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public interface AbstractInput {
	int NOT_A_NUMBER = -1;

	@InputUtil.Keycode
	int getKeycode();

	@AbstractInput.Modifier
	int modifiers();

	default boolean isEnterOrSpace() {
		return this.getKeycode() == InputUtil.GLFW_KEY_ENTER || this.getKeycode() == InputUtil.GLFW_KEY_SPACE || this.getKeycode() == InputUtil.GLFW_KEY_KP_ENTER;
	}

	default boolean isEnter() {
		return this.getKeycode() == InputUtil.GLFW_KEY_ENTER || this.getKeycode() == InputUtil.GLFW_KEY_KP_ENTER;
	}

	default boolean isEscape() {
		return this.getKeycode() == InputUtil.GLFW_KEY_ESCAPE;
	}

	default boolean isLeft() {
		return this.getKeycode() == InputUtil.GLFW_KEY_LEFT;
	}

	default boolean isRight() {
		return this.getKeycode() == InputUtil.GLFW_KEY_RIGHT;
	}

	default boolean isUp() {
		return this.getKeycode() == InputUtil.GLFW_KEY_UP;
	}

	default boolean isDown() {
		return this.getKeycode() == InputUtil.GLFW_KEY_DOWN;
	}

	default boolean isTab() {
		return this.getKeycode() == InputUtil.GLFW_KEY_TAB;
	}

	default int asNumber() {
		int i = this.getKeycode() - InputUtil.GLFW_KEY_0;
		return i >= 0 && i <= 9 ? i : -1;
	}

	default boolean hasAlt() {
		return (this.modifiers() & 4) != 0;
	}

	default boolean hasShift() {
		return (this.modifiers() & 1) != 0;
	}

	default boolean hasCtrl() {
		return (this.modifiers() & 2) != 0;
	}

	default boolean hasCtrlOrCmd() {
		return (this.modifiers() & SystemKeycodes.CTRL_MOD) != 0;
	}

	default boolean isSelectAll() {
		return this.getKeycode() == InputUtil.GLFW_KEY_A && this.hasCtrlOrCmd() && !this.hasShift() && !this.hasAlt();
	}

	default boolean isCopy() {
		return this.getKeycode() == InputUtil.GLFW_KEY_C && this.hasCtrlOrCmd() && !this.hasShift() && !this.hasAlt();
	}

	default boolean isPaste() {
		return this.getKeycode() == InputUtil.GLFW_KEY_V && this.hasCtrlOrCmd() && !this.hasShift() && !this.hasAlt();
	}

	default boolean isCut() {
		return this.getKeycode() == InputUtil.GLFW_KEY_X && this.hasCtrlOrCmd() && !this.hasShift() && !this.hasAlt();
	}

	@Retention(RetentionPolicy.CLASS)
	@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
	@Environment(EnvType.CLIENT)
	public @interface Modifier {
	}
}
