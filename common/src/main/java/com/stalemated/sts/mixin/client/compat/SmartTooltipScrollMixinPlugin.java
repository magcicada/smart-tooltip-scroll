package com.stalemated.sts.mixin.client.compat;

import com.stalemated.lib.helper.PlatformHelper;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SmartTooltipScrollMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("compat.iceberg")) {
            return PlatformHelper.isModLoadedAtLaunch("iceberg");
        }
        if (mixinClassName.contains("compat.emi")) {
            return PlatformHelper.isModLoadedAtLaunch("emi");
        }
        if (mixinClassName.contains("compat.puffish")) {
            return PlatformHelper.isModLoadedAtLaunch("puffish_skills");
        }
        if (mixinClassName.contains("compat.tierify")) {
            return PlatformHelper.isModLoadedAtLaunch("tiered");
        }
        if (mixinClassName.contains("compat.obscure")) {
            return PlatformHelper.isModLoadedAtLaunch("obscure_tooltips");
        }
        if (mixinClassName.contains("compat.spellengine")) {
            return PlatformHelper.isModLoadedAtLaunch("spell_engine");
        }
        if (mixinClassName.contains("compat.tooltipoverhaul")) {
            return PlatformHelper.isModLoadedAtLaunch("tooltipoverhaul");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
