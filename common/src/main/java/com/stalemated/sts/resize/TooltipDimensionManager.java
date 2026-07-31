package com.stalemated.sts.resize;

import com.stalemated.lib.util.math.MathUtils;
import com.stalemated.lib.helper.PlatformHelper;
import com.stalemated.sts.compat.legendarytooltips.LegendaryTooltipsCompat;
import com.stalemated.sts.config.ConfigManager;
import com.stalemated.sts.resize.overflow.TitleOverflowStrategyFactory;
import com.stalemated.sts.scroll.TooltipScrollManager;
import com.stalemated.sts.scroll.components.ScrollableTooltipComponent;
import com.stalemated.sts.state.StateManager;
import com.stalemated.sts.state.TooltipContext;
import com.stalemated.sts.state.TooltipContextManager;
import com.stalemated.sts.util.TooltipWrapUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TooltipDimensionManager {

    public static DrawContext currentContext = null;
    public static TextRenderer currentTextRenderer = null;
    public static final int MIN_TOOLTIP_HEIGHT = 32;
    public static final int MIN_TOOLTIP_WIDTH = 64;
    public static final int TOOLTIP_PADDING_X = 8;
    private static final int TOOLTIP_PADDING_Y = 4;
    public static final int TITLE_BODY_VERTICAL_GAP = 2;
    public static List<TooltipComponent> processedTitleComponentList = new ArrayList<>();
    public static List<TooltipComponent> bodyComponentList = new ArrayList<>();
    public static Function<List<TooltipComponent>, Integer> pinnedHeightPredictor = null;

    public static final boolean IS_LT_LOADED = PlatformHelper.INSTANCE.isModLoaded("legendarytooltips");

    private static final DimensionCache widthCache = new DimensionCache(TOOLTIP_PADDING_X, MIN_TOOLTIP_WIDTH);
    private static final DimensionCache heightCache = new DimensionCache(TOOLTIP_PADDING_Y, MIN_TOOLTIP_HEIGHT);

    private static class DimensionCache {
        private final int padding;
        private int lastWindowSize = -1;
        private int lastConfigPercent = -1;
        private int cachedSize = -1;
        private final int minSideLength;

        DimensionCache(int padding, int minSideLength) {
            this.padding = padding;
            this.minSideLength = minSideLength;
        }

        int get(int currentWindowSize, int currentConfigPercent) {
            if (currentWindowSize != lastWindowSize || currentConfigPercent != lastConfigPercent) {
                this.lastWindowSize = currentWindowSize;
                this.lastConfigPercent = currentConfigPercent;

                int maxAllowedSize = currentWindowSize - padding;
                int safePercent = MathUtils.clamp(currentConfigPercent, 1, 100);
                this.cachedSize = MathUtils.clamp(maxAllowedSize * safePercent / 100, minSideLength, maxAllowedSize);
            }
            return this.cachedSize;
        }
    }

    private static int calculateComponentListHeight(List<TooltipComponent> components) {
        if (components.isEmpty()) return 0;
        int totalHeight = 0;

        for (int i = 0; i < components.size(); i++) {
            totalHeight += components.get(i).getHeight() + getPaddingOffset(i);
        }
        return totalHeight;
    }

    public static List<TooltipComponent> enforceHeightLimit(List<TooltipComponent> components, TooltipContext ctx) {
        if (components.isEmpty()) return components;

        int splitIndex = getSplitIndex(components);

        int scaledTooltipWidth = getScaledTooltipWidth();
        int scaledTooltipHeight = getScaledTooltipHeight();
        int componentWidth = getModelOffset(ctx != null ? ctx.itemStack() : null, components);
        
        int titleMaxWidth;
        TitleOverflowMode overflowMode = ConfigManager.getConfig().title_overflow_mode;
        if (StateManager.isTierifyTooltip || overflowMode == TitleOverflowMode.WRAP) {
            titleMaxWidth = Math.max(scaledTooltipWidth, MIN_TOOLTIP_WIDTH) - componentWidth;
        } else {
            titleMaxWidth = Math.max(scaledTooltipWidth, MIN_TOOLTIP_WIDTH);
        }

        List<TooltipComponent> pinned = new ArrayList<>(components.subList(0, splitIndex));
        List<TooltipComponent> scrollableContentRaw = components.subList(splitIndex, components.size());
        List<TooltipComponent> scrollableContent = scrollableContentRaw;

        processedTitleComponentList = new ArrayList<>(pinned);
        bodyComponentList = new ArrayList<>(scrollableContent);

        if (currentTextRenderer != null) {
            pinned = TitleOverflowStrategyFactory.getStrategy().processComponentPhase(pinned, currentTextRenderer, titleMaxWidth);
            processedTitleComponentList = new ArrayList<>(pinned);
            // Two-pass approach: not discounting the scrollbar's width
            scrollableContent = TooltipWrapUtil.wrapComponents(scrollableContentRaw, scaledTooltipWidth, currentTextRenderer, false);
        }

        List<TooltipComponent> combined = new ArrayList<>();
        combined.addAll(pinned);
        combined.addAll(scrollableContent);
        
        int totalHeight = calculateComponentListHeight(combined);

        if (totalHeight > scaledTooltipHeight && currentTextRenderer != null) {
            if (scrollableContentRaw.isEmpty()) {
                return combined;
            }

            // 2nd pass: discounts the scrollbar width
            scrollableContent = TooltipWrapUtil.wrapComponents(scrollableContentRaw, scaledTooltipWidth - ScrollableTooltipComponent.SCROLLBAR_WIDTH, currentTextRenderer, false);

            int pinnedHeight;
            if (pinnedHeightPredictor != null) {
                pinnedHeight = pinnedHeightPredictor.apply(pinned);
            } else {
                pinnedHeight = calculateComponentListHeight(pinned);
            }
            int scrollableHeight = scaledTooltipHeight - pinnedHeight;
            int availableHeight = Math.max(scrollableHeight, MIN_TOOLTIP_HEIGHT);

            List<TooltipComponent> finalList = new ArrayList<>(pinned);
            finalList.add(new ScrollableTooltipComponent(scrollableContent, pinned, availableHeight, scaledTooltipWidth, currentTextRenderer));

            return finalList;
        }
        TooltipScrollManager.updateMaxScroll(0);

        return combined;
    }

    public static int getSplitIndex(List<TooltipComponent> components) {
        int splitIndex = 1;
        if (IS_LT_LOADED) {
            splitIndex = LegendaryTooltipsCompat.getSplitIndex(components, splitIndex);
        }
        return Math.min(splitIndex, components.size());
    }

    public static ItemStack getCurrentStack() {
        TooltipContext ctx = TooltipContextManager.peek();
        return ctx != null ? ctx.itemStack() : null;
    }

    public static void setState(DrawContext context, TextRenderer textRenderer) {
        currentContext = context;
        currentTextRenderer = textRenderer;
    }

    public static void clearState() {
        currentContext = null;
        currentTextRenderer = null;
        TooltipContextManager.clear();
        processedTitleComponentList = new ArrayList<>();
        bodyComponentList = new ArrayList<>();
    }

    public static int getScaledTooltipHeight() {
        return heightCache.get(MinecraftClient.getInstance().getWindow().getScaledHeight(), ConfigManager.getConfig().max_height_percentage);
    }

    public static int getScaledTooltipWidth() {
        return widthCache.get(MinecraftClient.getInstance().getWindow().getScaledWidth(), ConfigManager.getConfig().max_width_percentage);
    }

    public static int getModelOffset() {
        return getModelOffset(getCurrentStack(), null);
    }

    public static int getModelOffset(ItemStack currentStack, List<TooltipComponent> currentComponents) {
        if (IS_LT_LOADED) {
            return LegendaryTooltipsCompat.getItemModelComponentWidth(currentStack, currentComponents);
        }
        return 0;
    }

    public static int getPaddingOffset(int i) {
        return (i == 0) ? TITLE_BODY_VERTICAL_GAP : 0;
    }
}
