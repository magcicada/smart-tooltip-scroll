package com.stalemated.sts.compat.tooltipoverhaul;

import com.stalemated.lib.util.state.SharedTooltipState;
import com.stalemated.sts.config.ConfigManager;
import com.stalemated.sts.resize.TooltipDimensionManager;
import com.stalemated.sts.state.TooltipContextManager;
import dev.xylonity.tooltipoverhaul.client.render.TooltipContext;
import dev.xylonity.tooltipoverhaul.client.util.TooltipScrollState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class TooltipOverhaulStateManager {
    private static boolean stsActiveForCurrentTooltip = false;
    private static TooltipContext currentTOContext = null;

    public static boolean isHandlingTOTooltip() {
        return currentTOContext != null || stsActiveForCurrentTooltip;
    }

    public static TooltipContext getCurrentTOContext() {
        return currentTOContext;
    }

    public static void setContext(TooltipContext context) {
        currentTOContext = context;
    }

    public static void setStsActive(boolean active) {
        stsActiveForCurrentTooltip = active;
    }

    public static boolean isStsActive() {
        return stsActiveForCurrentTooltip;
    }

    public static void ensureContext(Object contextObj) {
        if (!ConfigManager.getConfig().tooltip_overhaul_compat) return;

        if (contextObj instanceof TooltipContext context) {
            TooltipDimensionManager.setState(context.getGraphics(), context.getFont());
            if (stsActiveForCurrentTooltip) {
                currentTOContext = context;
            }
        }
    }

    public static void clearContext() {
        if (!ConfigManager.getConfig().tooltip_overhaul_compat) return;

        TooltipDimensionManager.clearState();
        TooltipContextManager.pop();
        SharedTooltipState.clearCustomDimensionsRequest();
        stsActiveForCurrentTooltip = false;
        currentTOContext = null;
    }

    public static void disableToScroll(CallbackInfo ci) {
        if (stsActiveForCurrentTooltip && ConfigManager.getConfig().tooltip_overhaul_compat) {
            try {
                TooltipScrollState.reset();
            } catch (Exception ignored) {}
            ci.cancel();
        }
    }
}
