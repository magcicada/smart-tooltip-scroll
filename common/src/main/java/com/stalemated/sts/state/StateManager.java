package com.stalemated.sts.state;

import com.stalemated.lib.helper.PlatformHelper;

public class StateManager {
    public static boolean isTierifyTooltip = false;
    public static final boolean IS_LT_LOADED = PlatformHelper.isModLoaded("legendarytooltips");
    public static final boolean IS_TO_LOADED = PlatformHelper.isModLoaded("tooltipoverhaul");
    public static final boolean IS_OT_LOADED = PlatformHelper.isModLoaded("obscure_tooltips");
}
