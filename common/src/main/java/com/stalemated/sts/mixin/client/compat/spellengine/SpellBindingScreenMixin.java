package com.stalemated.sts.mixin.client.compat.spellengine;

import com.stalemated.lib.util.state.SharedTooltipState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.spell_engine.spellbinding.SpellBindingScreen")
public abstract class SpellBindingScreenMixin {

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II)V"))
    private void sts$forceCustomDimensionsForSpells(CallbackInfo ci) {
        SharedTooltipState.requestCustomDimensions();
    }
}
