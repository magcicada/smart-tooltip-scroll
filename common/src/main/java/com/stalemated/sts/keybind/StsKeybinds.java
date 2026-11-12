package com.stalemated.sts.keybind;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import com.stalemated.lib.client.ClientPlatformHelper;

public class StsKeybinds {
    public static final KeyBinding SCROLL_BYPASS_KEY = new KeyBinding(
            "key.smart_tooltip_scroll.bypass_scroll",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            "category.smart_tooltip_scroll.keys"
    );

    public static void register() {
        ClientPlatformHelper.registerKeyBinding(SCROLL_BYPASS_KEY);
    }
}
