package com.stalemated.sts.config;

import com.stalemated.lib.config.SLibConfig;
import com.stalemated.lib.config.LocalConfigManager;
import com.stalemated.lib.config.OptionInfo;
import java.util.Map;

import static com.stalemated.sts.SmartTooltipScrollClient.LOGGER;
import static com.stalemated.sts.SmartTooltipScrollClient.MOD_ID;

public class ConfigManager {
    public static LocalConfigManager<STSConfig> MANAGER;

    public static void register() {
        if (MANAGER == null) {
            MANAGER = SLibConfig.local(MOD_ID, STSConfig.class).logger(LOGGER).register();
        }
    }

    public static STSConfig getConfig() {
        return MANAGER.get();
    }

    public static void updateOption(String key, Object value) {
        OptionInfo option = MANAGER.getOptionTree().get(key);
        if (option == null) throw new IllegalArgumentException("Unknown STS option: " + key);
        MANAGER.modifyOptions(Map.of(option, value));
    }
}
