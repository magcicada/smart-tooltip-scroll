package com.stalemated.sts.gui.screen;

import com.stalemated.lib.compat.yacl.controller.builder.SelectDropdownControllerBuilder;
import com.stalemated.sts.config.ConfigManager;
import com.stalemated.sts.config.STSConfig;
import com.stalemated.sts.scroll.easing.ScrollEasingStyle;
import com.stalemated.sts.resize.TitleOverflowMode;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class TooltipDimensionsScreen {

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("sts.tooltip_dimensions_screen.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("sts.tooltip_dimensions_screen.title"))
                        .group(createCustomDimensionsGroup())
                        .group(createTitleOptionsGroup())
                        .group(createScrollingGroup())
                        .group(createCompatibilityGroup())
                        .build())
                .build()
                .generateScreen(parent);
    }

    private static OptionGroup createCustomDimensionsGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen.category.custom_dimensions"))
                .option(createBooleanOption("custom_tooltip_dimensions", "enable_custom_dimensions", cfg -> cfg.custom_tooltip_dimensions, true))
                .option(createIntOption("max_height_percentage", "max_height_percentage", 1, 100, 1, cfg -> cfg.max_height_percentage, 50))
                .option(createIntOption("max_width_percentage", "max_width_percentage", 1, 100, 1, cfg -> cfg.max_width_percentage, 50))
                .build();
    }

    private static OptionGroup createTitleOptionsGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen.category.title_options"))
                .option(createEnumOption(
                        "title_overflow_mode",
                        "title_overflow_mode",
                        mode -> Text.translatable("sts.tooltip_dimensions_screen.title_overflow_mode." + mode.name().toLowerCase()),
                        cfg -> cfg.title_overflow_mode,
                        TitleOverflowMode.SCROLL
                ))
                .option(createBooleanOption("title_centering", "enable_title_centering", cfg -> cfg.title_centering, true))
                .option(createDoubleOption(
                        "title_scroll_speed",
                        "title_scroll_speed",
                        1.0, 100.0, 1.0,
                        val -> Text.of(String.valueOf((int) Math.round(val))),
                        cfg -> cfg.title_scroll_speed,
                        25.0
                ))
                .option(createIntOption("title_scroll_pause_time_ms", "title_scroll_pause_time", 0, 5000, 100, cfg -> cfg.title_scroll_pause_time_ms, 2000))
                .build();
    }

    private static OptionGroup createScrollingGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen.category.scrolling"))
                .option(createFloatOption(
                        "scroll_smoothness",
                        "scroll_smoothness",
                        0.0f, 1.0f, 0.01f,
                        val -> Text.of(new BigDecimal(Float.toString(val)).setScale(2, RoundingMode.HALF_UP).toString()),
                        cfg -> cfg.scroll_smoothness,
                        0.25f
                ))
                .option(createFloatOption(
                        "lines_per_scroll",
                        "lines_per_scroll",
                        0.5f, 5.0f, 0.5f,
                        val -> Text.of(new BigDecimal(Float.toString(val)).setScale(1, RoundingMode.HALF_UP).toString()),
                        cfg -> cfg.lines_per_scroll,
                        1.5f
                ))
                .option(createEnumOption(
                        "scroll_easing_style",
                        "scroll_easing_style",
                        style -> Text.translatable("sts.tooltip_dimensions_screen.scroll_easing_style." + style.name().toLowerCase()),
                        cfg -> cfg.scroll_easing_style,
                        ScrollEasingStyle.SMOOTH
                ))
                .option(createBooleanOption(
                        "lock_container_scrolling",
                        "lock_container_scrolling",
                        cfg -> cfg.lock_container_scrolling,
                        true,
                        Text.translatable("sts.tooltip_dimensions_screen.lock_container_scrolling.warning")
                ))
                .build();
    }

    private static OptionGroup createCompatibilityGroup() {
        return OptionGroup.createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen.category.compatibility"))
                .option(createBooleanOption("obscure_compat", "obscure_compat", cfg -> cfg.obscure_compat, true))
                .option(createBooleanOption("tooltip_overhaul_compat", "tooltip_overhaul_compat", cfg -> cfg.tooltip_overhaul_compat, true))
                .build();
    }

    private static Option<Boolean> createBooleanOption(String configKey, String langKey, Function<STSConfig, Boolean> getter, boolean defaultValue) {
        return createBooleanOption(configKey, langKey, getter, defaultValue, null);
    }

    private static Option<Boolean> createBooleanOption(String configKey, String langKey, Function<STSConfig, Boolean> getter, boolean defaultValue, Text warning) {
        OptionDescription desc = warning != null
                ? OptionDescription.of(Text.translatable("sts.tooltip_dimensions_screen." + langKey + ".description"), warning)
                : OptionDescription.of(Text.translatable("sts.tooltip_dimensions_screen." + langKey + ".description"));
        return Option.<Boolean>createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen." + langKey))
                .description(desc)
                .binding(
                        defaultValue,
                        () -> getter.apply(ConfigManager.getConfig()),
                        val -> ConfigManager.updateOption(configKey, val)
                )
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private static Option<Integer> createIntOption(String configKey, String langKey, int min, int max, int step, Function<STSConfig, Integer> getter, int defaultValue) {
        return Option.<Integer>createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen." + langKey))
                .description(OptionDescription.of(Text.translatable("sts.tooltip_dimensions_screen." + langKey + ".description")))
                .binding(
                        defaultValue,
                        () -> getter.apply(ConfigManager.getConfig()),
                        val -> ConfigManager.updateOption(configKey, val)
                )
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, max).step(step))
                .build();
    }

    private static Option<Float> createFloatOption(String configKey, String langKey, float min, float max, float step, ValueFormatter<Float> formatter, Function<STSConfig, Float> getter, float defaultValue) {
        return Option.<Float>createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen." + langKey))
                .description(OptionDescription.of(Text.translatable("sts.tooltip_dimensions_screen." + langKey + ".description")))
                .binding(
                        defaultValue,
                        () -> getter.apply(ConfigManager.getConfig()),
                        val -> ConfigManager.updateOption(configKey, val)
                )
                .controller(opt -> {
                    FloatSliderControllerBuilder builder = FloatSliderControllerBuilder.create(opt).range(min, max).step(step);
                    if (formatter != null) builder.formatValue(formatter);
                    return builder;
                })
                .build();
    }

    private static Option<Double> createDoubleOption(String configKey, String langKey, double min, double max, double step, ValueFormatter<Double> formatter, Function<STSConfig, Double> getter, double defaultValue) {
        return Option.<Double>createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen." + langKey))
                .description(OptionDescription.of(Text.translatable("sts.tooltip_dimensions_screen." + langKey + ".description")))
                .binding(
                        defaultValue,
                        () -> getter.apply(ConfigManager.getConfig()),
                        val -> ConfigManager.updateOption(configKey, val)
                )
                .controller(opt -> {
                    DoubleSliderControllerBuilder builder = DoubleSliderControllerBuilder.create(opt).range(min, max).step(step);
                    if (formatter != null) builder.formatValue(formatter);
                    return builder;
                })
                .build();
    }

    private static <E extends Enum<E>> Option<E> createEnumOption(String configKey, String langKey, ValueFormatter<E> formatter, Function<STSConfig, E> getter, E defaultValue) {
        return Option.<E>createBuilder()
                .name(Text.translatable("sts.tooltip_dimensions_screen." + langKey))
                .description(OptionDescription.of(Text.translatable("sts.tooltip_dimensions_screen." + langKey + ".description")))
                .binding(
                        defaultValue,
                        () -> getter.apply(ConfigManager.getConfig()),
                        val -> ConfigManager.updateOption(configKey, val)
                )
                .controller(opt -> SelectDropdownControllerBuilder.createEnum(opt).formatValue(formatter))
                .build();
    }
}
