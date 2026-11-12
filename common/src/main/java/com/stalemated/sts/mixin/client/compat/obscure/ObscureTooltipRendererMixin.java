package com.stalemated.sts.mixin.client.compat.obscure;

import com.stalemated.lib.util.state.SharedTooltipState;
import com.stalemated.sts.compat.obscure.ObscureTooltipsCompat;
import com.stalemated.sts.resize.TooltipDimensionManager;
import com.stalemated.sts.state.TooltipContextManager;
import dev.obscuria.tooltips.client.TooltipHelper;
import dev.obscuria.tooltips.client.TooltipState;
import dev.obscuria.tooltips.client.tooltip.TooltipScroll;
import dev.obscuria.tooltips.client.tooltip.layout.TooltipLayout;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "dev.obscuria.tooltips.client.TooltipRenderer")
public abstract class ObscureTooltipRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)Z", at = @At("HEAD"))
    private static void sts$onRenderHead(DrawContext graphics, TextRenderer font, List<TooltipComponent> components, int mouseX, int mouseY, TooltipPositioner positioner, CallbackInfoReturnable<Boolean> cir) {
        if (ObscureTooltipsCompat.isCompatActive()) {
            TooltipDimensionManager.setState(graphics, font);
        }
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)Z", at = @At(value = "INVOKE", target = "Ldev/obscuria/tooltips/client/TooltipHelper;wrapLines(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Lnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    private static List<TooltipComponent> sts$redirectWrapLines(DrawContext graphics, List<TooltipComponent> components, TextRenderer font) {
        if (ObscureTooltipsCompat.isCompatActive()) {
            return components;
        }
        return TooltipHelper.wrapLines(graphics, components, font);
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)Z", at = @At(value = "INVOKE", target = "Ldev/obscuria/tooltips/client/tooltip/layout/TooltipLayout;rawProcessPostWrap(Ldev/obscuria/tooltips/client/TooltipState;Ljava/util/List;Lnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    private static List<TooltipComponent> sts$redirectPostWrap(TooltipLayout<TooltipState> instance, TooltipState state, List<TooltipComponent> components, TextRenderer font) {
        List<TooltipComponent> postWrapped = instance.rawProcessPostWrap(state, components, font);
        if (ObscureTooltipsCompat.isCompatActive()) {
            return ObscureTooltipsCompat.processComponents(postWrapped, font);
        }
        return postWrapped;
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)Z", at = @At(value = "INVOKE", target = "Ldev/obscuria/tooltips/client/tooltip/TooltipScroll;update(Ldev/obscuria/tooltips/client/TooltipState;II)V"))
    private static void sts$suppressScrollUpdate(TooltipState state, int tooltipHeight, int screenHeight) {
        if (ObscureTooltipsCompat.isCompatActive()) {
            return;
        }
        TooltipScroll.update(state, tooltipHeight, screenHeight);
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)Z", at = @At(value = "INVOKE", target = "Ldev/obscuria/tooltips/client/tooltip/TooltipScroll;getScroll()F")
    )
    private static float sts$redirectGetScroll() {
        if (ObscureTooltipsCompat.isCompatActive()) {
            return 0.0F;
        }
        return TooltipScroll.getScroll();
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)Z", at = @At("RETURN"))
    private static void sts$onRenderReturn(CallbackInfoReturnable<Boolean> cir) {
        if (ObscureTooltipsCompat.isCompatActive() && cir.getReturnValueZ()) {
            TooltipDimensionManager.clearState();
            TooltipContextManager.pop();
            SharedTooltipState.clearCustomDimensionsRequest();
        }
    }
}
