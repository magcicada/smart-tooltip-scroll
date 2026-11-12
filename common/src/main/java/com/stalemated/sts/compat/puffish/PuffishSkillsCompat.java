package com.stalemated.sts.compat.puffish;

import com.stalemated.lib.util.state.SharedTooltipState;
import com.stalemated.sts.config.ConfigManager;
import com.stalemated.sts.resize.TooltipDimensionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class PuffishSkillsCompat {

    public static boolean isSkillTooltipContext = false;

    public static List<OrderedText> wrapLines(MinecraftClient client, Text text) {
        if (isSkillTooltipContext && ConfigManager.getConfig().custom_tooltip_dimensions && client.textRenderer != null) {
            SharedTooltipState.requestCustomDimensions();
            int targetWidth = TooltipDimensionManager.getScaledTooltipWidth();
            return client.textRenderer.wrapLines(text, targetWidth);
        }
        return Tooltip.wrapLines(client, text);
    }
}
