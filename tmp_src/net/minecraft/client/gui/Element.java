package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.jspecify.annotations.Nullable;

/**
 * Base GUI interface for handling callbacks related to
 * keyboard or mouse actions.
 * 
 * Mouse coordinate is bounded by the size of the window in
 * pixels.
 */
@Environment(EnvType.CLIENT)
public interface Element extends Navigable {
	/**
	 * Callback for when a mouse move event has been captured.
	 * 
	 * @see net.minecraft.client.Mouse#onCursorPos
	 * 
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 */
	default void mouseMoved(double mouseX, double mouseY) {
	}

	/**
	 * Callback for when a mouse button down event
	 * has been captured.
	 * 
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseButton(long, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 */
	default boolean mouseClicked(Click click, boolean doubled) {
		return false;
	}

	/**
	 * Callback for when a mouse button release event
	 * has been captured.
	 * 
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseButton(long, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 */
	default boolean mouseReleased(Click click) {
		return false;
	}

	/**
	 * Callback for when a mouse button drag event
	 * has been captured.
	 * 
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onCursorPos(long, double, double)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 */
	default boolean mouseDragged(Click click, double offsetX, double offsetY) {
		return false;
	}

	/**
	 * Callback for when a mouse button scroll event
	 * has been captured.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseScroll(long, double, double)
	 * 
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 * @param horizontalAmount the horizontal scroll amount
	 * @param verticalAmount the vertical scroll amount
	 */
	default boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return false;
	}

	/**
	 * Callback for when a key down event has been captured.
	 * 
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onKey(long, int, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 */
	default boolean keyPressed(KeyInput input) {
		return false;
	}

	/**
	 * Callback for when a key down event has been captured.
	 * 
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onKey(long, int, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 */
	default boolean keyReleased(KeyInput input) {
		return false;
	}

	/**
	 * Callback for when a character input has been captured.
	 * 
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onChar(long, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 */
	default boolean charTyped(CharInput input) {
		return false;
	}

	@Nullable
	default GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
		return null;
	}

	/**
	 * Checks if the mouse position is within the bound
	 * of the element.
	 * 
	 * @return {@code true} if the mouse is within the bound of the element, otherwise {@code false}
	 * 
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 */
	default boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}

	void setFocused(boolean focused);

	boolean isFocused();

	default boolean isClickable() {
		return true;
	}

	@Nullable
	default GuiNavigationPath getFocusedPath() {
		return this.isFocused() ? GuiNavigationPath.of(this) : null;
	}

	default ScreenRect getNavigationFocus() {
		return ScreenRect.empty();
	}

	default ScreenRect getBorder(NavigationDirection direction) {
		return this.getNavigationFocus().getBorder(direction);
	}
}
