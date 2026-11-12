package com.stalemated.sts.mixin.client.compat.emi;

import com.stalemated.lib.util.state.SharedTooltipState;
import com.stalemated.sts.config.ConfigManager;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(targets = "dev.emi.emi.EmiRenderHelper")
public class EmiRenderHelperMixin {

    @ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/gui/screen/Screen;Ldev/emi/emi/runtime/EmiDrawContext;Ljava/util/List;IIILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
            at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private static int sts$adjustMaxWidth(int maxWidth, Screen screen, EmiDrawContext context, List<TooltipComponent> components) {
        if (ConfigManager.getConfig().custom_tooltip_dimensions && components != null && !components.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        return maxWidth;
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/gui/screen/Screen;Ldev/emi/emi/runtime/EmiDrawContext;Ljava/util/List;IIILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
            at = @At(value = "INVOKE", target = "Ldev/emi/emi/mixin/accessor/DrawContextAccessor;invokeDrawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V"))
    private static void sts$beforeDrawTooltip(Screen screen, EmiDrawContext context, List<TooltipComponent> components, int x, int y, int maxWidth, TooltipPositioner positioner, CallbackInfo ci) {
        if (ConfigManager.getConfig().custom_tooltip_dimensions) {
            SharedTooltipState.requestCustomDimensions();
        }
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/gui/screen/Screen;Ldev/emi/emi/runtime/EmiDrawContext;Ljava/util/List;IIILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
            at = @At(value = "INVOKE", target = "Ldev/emi/emi/mixin/accessor/DrawContextAccessor;invokeDrawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", shift = At.Shift.AFTER))
    private static void sts$afterDrawTooltip(Screen screen, EmiDrawContext context, List<TooltipComponent> components, int x, int y, int maxWidth, TooltipPositioner positioner, CallbackInfo ci) {
        SharedTooltipState.clearCustomDimensionsRequest();
    }
}

