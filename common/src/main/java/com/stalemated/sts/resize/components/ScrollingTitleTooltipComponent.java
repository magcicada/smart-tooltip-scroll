package com.stalemated.sts.resize.components;

import com.stalemated.sts.config.ConfigManager;
import com.stalemated.sts.resize.TooltipDimensionManager;
import com.stalemated.sts.util.OrderedTextUtil;
import com.stalemated.sts.util.StsManagedTitle;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;
import com.stalemated.lib.util.math.ScrollMathUtil;
import com.stalemated.lib.util.math.ScrollClock;

public class ScrollingTitleTooltipComponent extends OrderedTextTooltipComponent implements TooltipComponent, StsManagedTitle {
    private static final ScrollClock TITLE_CLOCK = new ScrollClock();
    private final OrderedText text;
    private final int maxTitleWidth;

    public ScrollingTitleTooltipComponent(OrderedText text, int maxTitleWidth) {
        super(text);
        this.text = text;
        this.maxTitleWidth = maxTitleWidth;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int textWidth = textRenderer.getWidth(this.text);
        return Math.min(textWidth, this.maxTitleWidth);
    }

    private int getOffset() {
        return TooltipDimensionManager.getModelOffset(
            TooltipDimensionManager.getCurrentStack(),
            TooltipDimensionManager.processedTitleComponentList
        );
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        int textWidth = textRenderer.getWidth(this.text);
        if (textWidth <= this.maxTitleWidth) {
            textRenderer.draw(this.text, (float) x, (float) y, -1, true, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            return;
        }

        int offset = getOffset();
        int overflowWidth = textWidth - this.maxTitleWidth;
        int identity = OrderedTextUtil.getStringFromOrderedText(this.text).hashCode();
        long elapsedTime = TITLE_CLOCK.elapsedMillis(identity);
        int scrollOffset = ScrollMathUtil.calculateScrollOffset(overflowWidth, ConfigManager.getConfig().title_scroll_speed, ConfigManager.getConfig().title_scroll_pause_time_ms, elapsedTime);
        int startX = x;
        if (!TooltipDimensionManager.handlesModelOffsetNatively()) startX += offset;
        int endX = x + this.maxTitleWidth;

        vertexConsumers.draw();

        Matrix4f translatedMatrix = new Matrix4f(matrix);
        translatedMatrix.translate(0, 0, 400);

        DrawContext context = TooltipDimensionManager.currentContext;

        if (context != null) {
            context.enableScissor(startX, y, endX, y + getHeight());
        }

        textRenderer.draw(this.text, (float) (x - scrollOffset), (float) y, -1, true, translatedMatrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        vertexConsumers.draw();

        if (context != null) {
            context.disableScissor();
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
    }
}
