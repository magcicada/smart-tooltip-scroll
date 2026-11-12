package com.stalemated.sts.compat.tooltipoverhaul;

import com.stalemated.lib.util.state.SharedTooltipState;
import com.stalemated.sts.config.ConfigManager;
import com.stalemated.sts.resize.TooltipDimensionManager;
import com.stalemated.sts.resize.components.WrappedTitleTooltipComponent;
import com.stalemated.sts.scroll.resetter.ExternalScrollStateResetter;
import com.stalemated.sts.state.TooltipContextManager;
import com.stalemated.sts.mixin.client.compat.tooltipoverhaul.TooltipContextAccessor;
import com.stalemated.sts.state.TooltipContext;
import dev.xylonity.tooltipoverhaul.client.util.TooltipScrollState;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;

import java.util.List;

public class TooltipOverhaulCompat implements ExternalScrollStateResetter {

    @Override
    public void resetState() {
        if (!ConfigManager.getConfig().tooltip_overhaul_compat) return;
        try {
            TooltipScrollState.reset();
        } catch (Exception ignored) {}
    }

    public static void applyDimensions(Object contextObj) {
        TooltipOverhaulStateManager.setStsActive(false);

        if (!ConfigManager.getConfig().custom_tooltip_dimensions || !ConfigManager.getConfig().tooltip_overhaul_compat) return;
        if (!(contextObj instanceof dev.xylonity.tooltipoverhaul.client.render.TooltipContext context)) return;

        List<TooltipComponent> componentsListRaw = context.getComponents();
        if (componentsListRaw == null || componentsListRaw.isEmpty()) return;

        ItemStack stack = context.getStack();
        TooltipContext stsCtx = TooltipContextManager.peek();

        boolean isItem = stsCtx != null || !stack.isEmpty();
        boolean isForced = SharedTooltipState.isCustomDimensionsRequested();

        if (isItem || isForced) {
            TooltipOverhaulStateManager.setContext(context);
            TooltipDimensionManager.setState(context.getGraphics(), context.getFont());

            if (stsCtx == null) {
                stsCtx = new TooltipContext(stack);
            }

            TooltipDimensionManager.pinnedHeightPredictor = pinned -> {
                if (pinned.isEmpty()) return 0;

                if (pinned.get(0) instanceof WrappedTitleTooltipComponent wrapped) {
                    TooltipOverhaulLayoutMetrics metrics = TooltipOverhaulLayoutMetrics.calculate(context, wrapped);
                    return metrics.getTargetDividerY();
                }
                return TooltipDimensionManager.calculateComponentListHeight(pinned);
            };

            List<TooltipComponent> processed = TooltipDimensionManager.enforceHeightLimit(componentsListRaw, stsCtx);
            TooltipDimensionManager.pinnedHeightPredictor = null;
            
            ((TooltipContextAccessor) context).sts$setComponents(processed);

            TooltipOverhaulStateManager.setStsActive(true);
        }
    }
}
