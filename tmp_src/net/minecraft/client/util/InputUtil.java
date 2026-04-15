package net.minecraft.client.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

@Environment(EnvType.CLIENT)
public class InputUtil {
	@Nullable
	private static final MethodHandle GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE;
	private static final int GLFW_RAW_MOUSE_MOTION;
	public static final int GLFW_KEY_0 = 48;
	public static final int GLFW_KEY_1 = 49;
	public static final int GLFW_KEY_2 = 50;
	public static final int GLFW_KEY_3 = 51;
	public static final int GLFW_KEY_4 = 52;
	public static final int GLFW_KEY_5 = 53;
	public static final int GLFW_KEY_6 = 54;
	public static final int GLFW_KEY_7 = 55;
	public static final int GLFW_KEY_8 = 56;
	public static final int GLFW_KEY_9 = 57;
	public static final int GLFW_KEY_A = 65;
	public static final int GLFW_KEY_B = 66;
	public static final int GLFW_KEY_C = 67;
	public static final int GLFW_KEY_D = 68;
	public static final int GLFW_KEY_E = 69;
	public static final int GLFW_KEY_F = 70;
	public static final int GLFW_KEY_G = 71;
	public static final int GLFW_KEY_H = 72;
	public static final int GLFW_KEY_I = 73;
	public static final int GLFW_KEY_J = 74;
	public static final int GLFW_KEY_K = 75;
	public static final int GLFW_KEY_L = 76;
	public static final int GLFW_KEY_M = 77;
	public static final int GLFW_KEY_N = 78;
	public static final int GLFW_KEY_O = 79;
	public static final int GLFW_KEY_P = 80;
	public static final int GLFW_KEY_Q = 81;
	public static final int GLFW_KEY_R = 82;
	public static final int GLFW_KEY_S = 83;
	public static final int GLFW_KEY_T = 84;
	public static final int GLFW_KEY_U = 85;
	public static final int GLFW_KEY_V = 86;
	public static final int GLFW_KEY_W = 87;
	public static final int GLFW_KEY_X = 88;
	public static final int GLFW_KEY_Y = 89;
	public static final int GLFW_KEY_Z = 90;
	public static final int GLFW_KEY_F1 = 290;
	public static final int GLFW_KEY_F2 = 291;
	public static final int GLFW_KEY_F3 = 292;
	public static final int GLFW_KEY_F4 = 293;
	public static final int GLFW_KEY_F5 = 294;
	public static final int GLFW_KEY_F6 = 295;
	public static final int GLFW_KEY_F7 = 296;
	public static final int GLFW_KEY_F8 = 297;
	public static final int GLFW_KEY_F9 = 298;
	public static final int GLFW_KEY_F10 = 299;
	public static final int GLFW_KEY_F11 = 300;
	public static final int GLFW_KEY_F12 = 301;
	public static final int GLFW_KEY_F13 = 302;
	public static final int GLFW_KEY_F14 = 303;
	public static final int GLFW_KEY_F15 = 304;
	public static final int GLFW_KEY_F16 = 305;
	public static final int GLFW_KEY_F17 = 306;
	public static final int GLFW_KEY_F18 = 307;
	public static final int GLFW_KEY_F19 = 308;
	public static final int GLFW_KEY_F20 = 309;
	public static final int GLFW_KEY_F21 = 310;
	public static final int GLFW_KEY_F22 = 311;
	public static final int GLFW_KEY_F23 = 312;
	public static final int GLFW_KEY_F24 = 313;
	public static final int GLFW_KEY_F25 = 314;
	public static final int GLFW_KEY_NUM_LOCK = 282;
	public static final int GLFW_KEY_KP_0 = 320;
	public static final int GLFW_KEY_KP_1 = 321;
	public static final int GLFW_KEY_KP_2 = 322;
	public static final int GLFW_KEY_KP_3 = 323;
	public static final int GLFW_KEY_KP_4 = 324;
	public static final int GLFW_KEY_KP_5 = 325;
	public static final int GLFW_KEY_KP_6 = 326;
	public static final int GLFW_KEY_KP_7 = 327;
	public static final int GLFW_KEY_KP_8 = 328;
	public static final int GLFW_KEY_KP_9 = 329;
	public static final int GLFW_KEY_KP_DECIMAL = 330;
	public static final int GLFW_KEY_KP_ENTER = 335;
	public static final int GLFW_KEY_KP_EQUAL = 336;
	public static final int GLFW_KEY_DOWN = 264;
	public static final int GLFW_KEY_LEFT = 263;
	public static final int GLFW_KEY_RIGHT = 262;
	public static final int GLFW_KEY_UP = 265;
	public static final int GLFW_KEY_KP_ADD = 334;
	public static final int GLFW_KEY_APOSTROPHE = 39;
	public static final int GLFW_KEY_BACKSLASH = 92;
	public static final int GLFW_KEY_COMMA = 44;
	public static final int GLFW_KEY_EQUAL = 61;
	public static final int GLFW_KEY_GRAVE_ACCENT = 96;
	public static final int GLFW_KEY_LEFT_BRACKET = 91;
	public static final int GLFW_KEY_MINUS = 45;
	public static final int GLFW_KEY_KP_MULTIPLY = 332;
	public static final int GLFW_KEY_PERIOD = 46;
	public static final int GLFW_KEY_RIGHT_BRACKET = 93;
	public static final int GLFW_KEY_SEMICOLON = 59;
	public static final int GLFW_KEY_SLASH = 47;
	public static final int GLFW_KEY_SPACE = 32;
	public static final int GLFW_KEY_TAB = 258;
	public static final int GLFW_KEY_LEFT_ALT = 342;
	public static final int GLFW_KEY_LEFT_CONTROL = 341;
	public static final int GLFW_KEY_LEFT_SHIFT = 340;
	public static final int GLFW_KEY_LEFT_SUPER = 343;
	public static final int GLFW_KEY_RIGHT_ALT = 346;
	public static final int GLFW_KEY_RIGHT_CONTROL = 345;
	public static final int GLFW_KEY_RIGHT_SHIFT = 344;
	public static final int GLFW_KEY_RIGHT_SUPER = 347;
	public static final int GLFW_KEY_ENTER = 257;
	public static final int GLFW_KEY_ESCAPE = 256;
	public static final int GLFW_KEY_BACKSPACE = 259;
	public static final int GLFW_KEY_DELETE = 261;
	public static final int GLFW_KEY_END = 269;
	public static final int GLFW_KEY_HOME = 268;
	public static final int GLFW_KEY_INSERT = 260;
	public static final int GLFW_KEY_PAGE_DOWN = 267;
	public static final int GLFW_KEY_PAGE_UP = 266;
	public static final int GLFW_KEY_CAPS_LOCK = 280;
	public static final int GLFW_KEY_PAUSE = 284;
	public static final int GLFW_KEY_SCROLL_LOCK = 281;
	public static final int GLFW_KEY_PRINT_SCREEN = 283;
	public static final int GLFW_PRESS = 1;
	public static final int GLFW_RELEASE = 0;
	public static final int GLFW_REPEAT = 2;
	public static final int GLFW_MOUSE_BUTTON_LEFT = 0;
	public static final int GLFW_MOUSE_BUTTON_RIGHT = 1;
	public static final int GLFW_MOUSE_BUTTON_MIDDLE = 2;
	public static final int field_63448 = 3;
	public static final int field_63449 = 4;
	public static final int field_63450 = 5;
	public static final int field_63451 = 6;
	public static final int field_63452 = 0;
	public static final int GLFW_MOD_SHIFT = 1;
	public static final int GLFW_MOD_CONTROL = 2;
	public static final int GLFW_MOD_ALT = 4;
	public static final int GLFW_MOD_SUPER = 8;
	public static final int GLFW_MOD_CAPS_LOCK = 16;
	public static final int GLFW_MOD_NUM_LOCK = 32;
	public static final int GLFW_CURSOR = 208897;
	public static final int GLFW_CURSOR_DISABLED = 212995;
	public static final int GLFW_CURSOR_NORMAL = 212993;
	public static final InputUtil.Key UNKNOWN_KEY;

	public static InputUtil.Key fromKeyCode(KeyInput key) {
		return key.key() == GLFW.GLFW_KEY_UNKNOWN ? InputUtil.Type.SCANCODE.createFromCode(key.scancode()) : InputUtil.Type.KEYSYM.createFromCode(key.key());
	}

	public static InputUtil.Key fromTranslationKey(String translationKey) {
		if (InputUtil.Key.KEYS.containsKey(translationKey)) {
			return (InputUtil.Key)InputUtil.Key.KEYS.get(translationKey);
		} else {
			for (InputUtil.Type type : InputUtil.Type.values()) {
				if (translationKey.startsWith(type.name)) {
					String string = translationKey.substring(type.name.length() + 1);
					int i = Integer.parseInt(string);
					if (type == InputUtil.Type.MOUSE) {
						i--;
					}

					return type.createFromCode(i);
				}
			}

			throw new IllegalArgumentException("Unknown key name: " + translationKey);
		}
	}

	public static boolean isKeyPressed(Window window, int code) {
		return GLFW.glfwGetKey(window.getHandle(), code) == GLFW_PRESS;
	}

	public static void setKeyboardCallbacks(Window window, GLFWKeyCallbackI keyCallback, GLFWCharModsCallbackI charModsCallback) {
		GLFW.glfwSetKeyCallback(window.getHandle(), keyCallback);
		GLFW.glfwSetCharModsCallback(window.getHandle(), charModsCallback);
	}

	public static void setMouseCallbacks(
		Window window,
		GLFWCursorPosCallbackI cursorPosCallback,
		GLFWMouseButtonCallbackI mouseButtonCallback,
		GLFWScrollCallbackI scrollCallback,
		GLFWDropCallbackI dropCallback
	) {
		GLFW.glfwSetCursorPosCallback(window.getHandle(), cursorPosCallback);
		GLFW.glfwSetMouseButtonCallback(window.getHandle(), mouseButtonCallback);
		GLFW.glfwSetScrollCallback(window.getHandle(), scrollCallback);
		GLFW.glfwSetDropCallback(window.getHandle(), dropCallback);
	}

	public static void setCursorParameters(Window window, int inputModeValue, double x, double y) {
		GLFW.glfwSetCursorPos(window.getHandle(), x, y);
		GLFW.glfwSetInputMode(window.getHandle(), GLFW_CURSOR, inputModeValue);
	}

	public static boolean isRawMouseMotionSupported() {
		try {
			return GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE != null && (boolean)GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE.invokeExact();
		} catch (Throwable var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void setRawMouseMotionMode(Window window, boolean value) {
		if (isRawMouseMotionSupported()) {
			GLFW.glfwSetInputMode(window.getHandle(), GLFW_RAW_MOUSE_MOTION, value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		}
	}

	static {
		Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(boolean.class);
		MethodHandle methodHandle = null;
		int i = 0;

		try {
			methodHandle = lookup.findStatic(GLFW.class, "glfwRawMouseMotionSupported", methodType);
			MethodHandle methodHandle2 = lookup.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", int.class);
			i = (int)methodHandle2.invokeExact();
		} catch (NoSuchFieldException | NoSuchMethodException var5) {
		} catch (Throwable var6) {
			throw new RuntimeException(var6);
		}

		GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE = methodHandle;
		GLFW_RAW_MOUSE_MOTION = i;
		UNKNOWN_KEY = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_UNKNOWN);
	}

	@Environment(EnvType.CLIENT)
	public static final class Key {
		private final String translationKey;
		private final InputUtil.Type type;
		private final int code;
		private final Supplier<Text> localizedText;
		static final Map<String, InputUtil.Key> KEYS = Maps.<String, InputUtil.Key>newHashMap();

		Key(String translationKey, InputUtil.Type type, int code) {
			this.translationKey = translationKey;
			this.type = type;
			this.code = code;
			this.localizedText = Suppliers.memoize(() -> (Text)type.textTranslator.apply(code, translationKey));
			KEYS.put(translationKey, this);
		}

		public InputUtil.Type getCategory() {
			return this.type;
		}

		public int getCode() {
			return this.code;
		}

		public String getTranslationKey() {
			return this.translationKey;
		}

		public Text getLocalizedText() {
			return (Text)this.localizedText.get();
		}

		public OptionalInt toInt() {
			if (this.code >= InputUtil.GLFW_KEY_0 && this.code <= InputUtil.GLFW_KEY_9) {
				return OptionalInt.of(this.code - InputUtil.GLFW_KEY_0);
			} else {
				return this.code >= InputUtil.GLFW_KEY_KP_0 && this.code <= InputUtil.GLFW_KEY_KP_9
					? OptionalInt.of(this.code - InputUtil.GLFW_KEY_KP_0)
					: OptionalInt.empty();
			}
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				InputUtil.Key key = (InputUtil.Key)o;
				return this.code == key.code && this.type == key.type;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.type, this.code});
		}

		public String toString() {
			return this.translationKey;
		}
	}

	@Retention(RetentionPolicy.CLASS)
	@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
	@Environment(EnvType.CLIENT)
	public @interface Keycode {
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		KEYSYM("key.keyboard", (keyCode, translationKey) -> {
			if ("key.keyboard.unknown".equals(translationKey)) {
				return Text.translatable(translationKey);
			} else {
				String string = GLFW.glfwGetKeyName(keyCode, -1);
				return string != null ? Text.literal(string.toUpperCase(Locale.ROOT)) : Text.translatable(translationKey);
			}
		}),
		SCANCODE("scancode", (scanCode, translationKey) -> {
			String string = GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, scanCode);
			return string != null ? Text.literal(string) : Text.translatable(translationKey);
		}),
		MOUSE(
			"key.mouse",
			(buttonCode, translationKey) -> Language.getInstance().hasTranslation(translationKey)
				? Text.translatable(translationKey)
				: Text.translatable("key.mouse", buttonCode + 1)
		);

		private static final String UNKNOWN_TRANSLATION_KEY = "key.keyboard.unknown";
		private final Int2ObjectMap<InputUtil.Key> map = new Int2ObjectOpenHashMap<>();
		final String name;
		final BiFunction<Integer, String, Text> textTranslator;

		private static void mapKey(InputUtil.Type type, String translationKey, int keyCode) {
			InputUtil.Key key = new InputUtil.Key(translationKey, type, keyCode);
			type.map.put(keyCode, key);
		}

		private Type(final String name, final BiFunction<Integer, String, Text> textTranslator) {
			this.name = name;
			this.textTranslator = textTranslator;
		}

		public InputUtil.Key createFromCode(int code) {
			return this.map.computeIfAbsent(code, (Int2ObjectFunction<? extends InputUtil.Key>)(codex -> {
				int i = codex;
				if (this == MOUSE) {
					i = codex + 1;
				}

				String string = this.name + "." + i;
				return new InputUtil.Key(string, this, codex);
			}));
		}

		static {
			mapKey(KEYSYM, "key.keyboard.unknown", GLFW.GLFW_KEY_UNKNOWN);
			mapKey(MOUSE, "key.mouse.left", InputUtil.GLFW_MOUSE_BUTTON_LEFT);
			mapKey(MOUSE, "key.mouse.right", InputUtil.GLFW_MOUSE_BUTTON_RIGHT);
			mapKey(MOUSE, "key.mouse.middle", InputUtil.GLFW_MOUSE_BUTTON_MIDDLE);
			mapKey(MOUSE, "key.mouse.4", GLFW.GLFW_MOUSE_BUTTON_4);
			mapKey(MOUSE, "key.mouse.5", GLFW.GLFW_MOUSE_BUTTON_5);
			mapKey(MOUSE, "key.mouse.6", GLFW.GLFW_MOUSE_BUTTON_6);
			mapKey(MOUSE, "key.mouse.7", GLFW.GLFW_MOUSE_BUTTON_7);
			mapKey(MOUSE, "key.mouse.8", GLFW.GLFW_MOUSE_BUTTON_8);
			mapKey(KEYSYM, "key.keyboard.0", InputUtil.GLFW_KEY_0);
			mapKey(KEYSYM, "key.keyboard.1", InputUtil.GLFW_KEY_1);
			mapKey(KEYSYM, "key.keyboard.2", InputUtil.GLFW_KEY_2);
			mapKey(KEYSYM, "key.keyboard.3", InputUtil.GLFW_KEY_3);
			mapKey(KEYSYM, "key.keyboard.4", InputUtil.GLFW_KEY_4);
			mapKey(KEYSYM, "key.keyboard.5", InputUtil.GLFW_KEY_5);
			mapKey(KEYSYM, "key.keyboard.6", InputUtil.GLFW_KEY_6);
			mapKey(KEYSYM, "key.keyboard.7", InputUtil.GLFW_KEY_7);
			mapKey(KEYSYM, "key.keyboard.8", InputUtil.GLFW_KEY_8);
			mapKey(KEYSYM, "key.keyboard.9", InputUtil.GLFW_KEY_9);
			mapKey(KEYSYM, "key.keyboard.a", InputUtil.GLFW_KEY_A);
			mapKey(KEYSYM, "key.keyboard.b", InputUtil.GLFW_KEY_B);
			mapKey(KEYSYM, "key.keyboard.c", InputUtil.GLFW_KEY_C);
			mapKey(KEYSYM, "key.keyboard.d", InputUtil.GLFW_KEY_D);
			mapKey(KEYSYM, "key.keyboard.e", InputUtil.GLFW_KEY_E);
			mapKey(KEYSYM, "key.keyboard.f", InputUtil.GLFW_KEY_F);
			mapKey(KEYSYM, "key.keyboard.g", InputUtil.GLFW_KEY_G);
			mapKey(KEYSYM, "key.keyboard.h", InputUtil.GLFW_KEY_H);
			mapKey(KEYSYM, "key.keyboard.i", InputUtil.GLFW_KEY_I);
			mapKey(KEYSYM, "key.keyboard.j", InputUtil.GLFW_KEY_J);
			mapKey(KEYSYM, "key.keyboard.k", InputUtil.GLFW_KEY_K);
			mapKey(KEYSYM, "key.keyboard.l", InputUtil.GLFW_KEY_L);
			mapKey(KEYSYM, "key.keyboard.m", InputUtil.GLFW_KEY_M);
			mapKey(KEYSYM, "key.keyboard.n", InputUtil.GLFW_KEY_N);
			mapKey(KEYSYM, "key.keyboard.o", InputUtil.GLFW_KEY_O);
			mapKey(KEYSYM, "key.keyboard.p", InputUtil.GLFW_KEY_P);
			mapKey(KEYSYM, "key.keyboard.q", InputUtil.GLFW_KEY_Q);
			mapKey(KEYSYM, "key.keyboard.r", InputUtil.GLFW_KEY_R);
			mapKey(KEYSYM, "key.keyboard.s", InputUtil.GLFW_KEY_S);
			mapKey(KEYSYM, "key.keyboard.t", InputUtil.GLFW_KEY_T);
			mapKey(KEYSYM, "key.keyboard.u", InputUtil.GLFW_KEY_U);
			mapKey(KEYSYM, "key.keyboard.v", InputUtil.GLFW_KEY_V);
			mapKey(KEYSYM, "key.keyboard.w", InputUtil.GLFW_KEY_W);
			mapKey(KEYSYM, "key.keyboard.x", InputUtil.GLFW_KEY_X);
			mapKey(KEYSYM, "key.keyboard.y", InputUtil.GLFW_KEY_Y);
			mapKey(KEYSYM, "key.keyboard.z", InputUtil.GLFW_KEY_Z);
			mapKey(KEYSYM, "key.keyboard.f1", InputUtil.GLFW_KEY_F1);
			mapKey(KEYSYM, "key.keyboard.f2", InputUtil.GLFW_KEY_F2);
			mapKey(KEYSYM, "key.keyboard.f3", InputUtil.GLFW_KEY_F3);
			mapKey(KEYSYM, "key.keyboard.f4", InputUtil.GLFW_KEY_F4);
			mapKey(KEYSYM, "key.keyboard.f5", InputUtil.GLFW_KEY_F5);
			mapKey(KEYSYM, "key.keyboard.f6", InputUtil.GLFW_KEY_F6);
			mapKey(KEYSYM, "key.keyboard.f7", InputUtil.GLFW_KEY_F7);
			mapKey(KEYSYM, "key.keyboard.f8", InputUtil.GLFW_KEY_F8);
			mapKey(KEYSYM, "key.keyboard.f9", InputUtil.GLFW_KEY_F9);
			mapKey(KEYSYM, "key.keyboard.f10", InputUtil.GLFW_KEY_F10);
			mapKey(KEYSYM, "key.keyboard.f11", InputUtil.GLFW_KEY_F11);
			mapKey(KEYSYM, "key.keyboard.f12", InputUtil.GLFW_KEY_F12);
			mapKey(KEYSYM, "key.keyboard.f13", InputUtil.GLFW_KEY_F13);
			mapKey(KEYSYM, "key.keyboard.f14", InputUtil.GLFW_KEY_F14);
			mapKey(KEYSYM, "key.keyboard.f15", InputUtil.GLFW_KEY_F15);
			mapKey(KEYSYM, "key.keyboard.f16", InputUtil.GLFW_KEY_F16);
			mapKey(KEYSYM, "key.keyboard.f17", InputUtil.GLFW_KEY_F17);
			mapKey(KEYSYM, "key.keyboard.f18", InputUtil.GLFW_KEY_F18);
			mapKey(KEYSYM, "key.keyboard.f19", InputUtil.GLFW_KEY_F19);
			mapKey(KEYSYM, "key.keyboard.f20", InputUtil.GLFW_KEY_F20);
			mapKey(KEYSYM, "key.keyboard.f21", InputUtil.GLFW_KEY_F21);
			mapKey(KEYSYM, "key.keyboard.f22", InputUtil.GLFW_KEY_F22);
			mapKey(KEYSYM, "key.keyboard.f23", InputUtil.GLFW_KEY_F23);
			mapKey(KEYSYM, "key.keyboard.f24", InputUtil.GLFW_KEY_F24);
			mapKey(KEYSYM, "key.keyboard.f25", InputUtil.GLFW_KEY_F25);
			mapKey(KEYSYM, "key.keyboard.num.lock", InputUtil.GLFW_KEY_NUM_LOCK);
			mapKey(KEYSYM, "key.keyboard.keypad.0", InputUtil.GLFW_KEY_KP_0);
			mapKey(KEYSYM, "key.keyboard.keypad.1", InputUtil.GLFW_KEY_KP_1);
			mapKey(KEYSYM, "key.keyboard.keypad.2", InputUtil.GLFW_KEY_KP_2);
			mapKey(KEYSYM, "key.keyboard.keypad.3", InputUtil.GLFW_KEY_KP_3);
			mapKey(KEYSYM, "key.keyboard.keypad.4", InputUtil.GLFW_KEY_KP_4);
			mapKey(KEYSYM, "key.keyboard.keypad.5", InputUtil.GLFW_KEY_KP_5);
			mapKey(KEYSYM, "key.keyboard.keypad.6", InputUtil.GLFW_KEY_KP_6);
			mapKey(KEYSYM, "key.keyboard.keypad.7", InputUtil.GLFW_KEY_KP_7);
			mapKey(KEYSYM, "key.keyboard.keypad.8", InputUtil.GLFW_KEY_KP_8);
			mapKey(KEYSYM, "key.keyboard.keypad.9", InputUtil.GLFW_KEY_KP_9);
			mapKey(KEYSYM, "key.keyboard.keypad.add", InputUtil.GLFW_KEY_KP_ADD);
			mapKey(KEYSYM, "key.keyboard.keypad.decimal", InputUtil.GLFW_KEY_KP_DECIMAL);
			mapKey(KEYSYM, "key.keyboard.keypad.enter", InputUtil.GLFW_KEY_KP_ENTER);
			mapKey(KEYSYM, "key.keyboard.keypad.equal", InputUtil.GLFW_KEY_KP_EQUAL);
			mapKey(KEYSYM, "key.keyboard.keypad.multiply", InputUtil.GLFW_KEY_KP_MULTIPLY);
			mapKey(KEYSYM, "key.keyboard.keypad.divide", GLFW.GLFW_KEY_KP_DIVIDE);
			mapKey(KEYSYM, "key.keyboard.keypad.subtract", GLFW.GLFW_KEY_KP_SUBTRACT);
			mapKey(KEYSYM, "key.keyboard.down", InputUtil.GLFW_KEY_DOWN);
			mapKey(KEYSYM, "key.keyboard.left", InputUtil.GLFW_KEY_LEFT);
			mapKey(KEYSYM, "key.keyboard.right", InputUtil.GLFW_KEY_RIGHT);
			mapKey(KEYSYM, "key.keyboard.up", InputUtil.GLFW_KEY_UP);
			mapKey(KEYSYM, "key.keyboard.apostrophe", InputUtil.GLFW_KEY_APOSTROPHE);
			mapKey(KEYSYM, "key.keyboard.backslash", InputUtil.GLFW_KEY_BACKSLASH);
			mapKey(KEYSYM, "key.keyboard.comma", InputUtil.GLFW_KEY_COMMA);
			mapKey(KEYSYM, "key.keyboard.equal", InputUtil.GLFW_KEY_EQUAL);
			mapKey(KEYSYM, "key.keyboard.grave.accent", InputUtil.GLFW_KEY_GRAVE_ACCENT);
			mapKey(KEYSYM, "key.keyboard.left.bracket", InputUtil.GLFW_KEY_LEFT_BRACKET);
			mapKey(KEYSYM, "key.keyboard.minus", InputUtil.GLFW_KEY_MINUS);
			mapKey(KEYSYM, "key.keyboard.period", InputUtil.GLFW_KEY_PERIOD);
			mapKey(KEYSYM, "key.keyboard.right.bracket", InputUtil.GLFW_KEY_RIGHT_BRACKET);
			mapKey(KEYSYM, "key.keyboard.semicolon", InputUtil.GLFW_KEY_SEMICOLON);
			mapKey(KEYSYM, "key.keyboard.slash", InputUtil.GLFW_KEY_SLASH);
			mapKey(KEYSYM, "key.keyboard.space", InputUtil.GLFW_KEY_SPACE);
			mapKey(KEYSYM, "key.keyboard.tab", InputUtil.GLFW_KEY_TAB);
			mapKey(KEYSYM, "key.keyboard.left.alt", InputUtil.GLFW_KEY_LEFT_ALT);
			mapKey(KEYSYM, "key.keyboard.left.control", InputUtil.GLFW_KEY_LEFT_CONTROL);
			mapKey(KEYSYM, "key.keyboard.left.shift", InputUtil.GLFW_KEY_LEFT_SHIFT);
			mapKey(KEYSYM, "key.keyboard.left.win", InputUtil.GLFW_KEY_LEFT_SUPER);
			mapKey(KEYSYM, "key.keyboard.right.alt", InputUtil.GLFW_KEY_RIGHT_ALT);
			mapKey(KEYSYM, "key.keyboard.right.control", InputUtil.GLFW_KEY_RIGHT_CONTROL);
			mapKey(KEYSYM, "key.keyboard.right.shift", InputUtil.GLFW_KEY_RIGHT_SHIFT);
			mapKey(KEYSYM, "key.keyboard.right.win", InputUtil.GLFW_KEY_RIGHT_SUPER);
			mapKey(KEYSYM, "key.keyboard.enter", InputUtil.GLFW_KEY_ENTER);
			mapKey(KEYSYM, "key.keyboard.escape", InputUtil.GLFW_KEY_ESCAPE);
			mapKey(KEYSYM, "key.keyboard.backspace", InputUtil.GLFW_KEY_BACKSPACE);
			mapKey(KEYSYM, "key.keyboard.delete", InputUtil.GLFW_KEY_DELETE);
			mapKey(KEYSYM, "key.keyboard.end", InputUtil.GLFW_KEY_END);
			mapKey(KEYSYM, "key.keyboard.home", InputUtil.GLFW_KEY_HOME);
			mapKey(KEYSYM, "key.keyboard.insert", InputUtil.GLFW_KEY_INSERT);
			mapKey(KEYSYM, "key.keyboard.page.down", InputUtil.GLFW_KEY_PAGE_DOWN);
			mapKey(KEYSYM, "key.keyboard.page.up", InputUtil.GLFW_KEY_PAGE_UP);
			mapKey(KEYSYM, "key.keyboard.caps.lock", InputUtil.GLFW_KEY_CAPS_LOCK);
			mapKey(KEYSYM, "key.keyboard.pause", InputUtil.GLFW_KEY_PAUSE);
			mapKey(KEYSYM, "key.keyboard.scroll.lock", InputUtil.GLFW_KEY_SCROLL_LOCK);
			mapKey(KEYSYM, "key.keyboard.menu", GLFW.GLFW_KEY_MENU);
			mapKey(KEYSYM, "key.keyboard.print.screen", InputUtil.GLFW_KEY_PRINT_SCREEN);
			mapKey(KEYSYM, "key.keyboard.world.1", GLFW.GLFW_KEY_WORLD_1);
			mapKey(KEYSYM, "key.keyboard.world.2", GLFW.GLFW_KEY_WORLD_2);
		}
	}
}
