package id.universenetwork.sfa_loader.template;

import id.universenetwork.sfa_loader.libraries.infinitylib.core.AddonConfig;

public abstract class AddonTemplate {
    public abstract void onLoad();

    public void onUnload() {
    }

    public AddonConfig getConfig() {
        try {
            return new AddonConfig("addons-config/" + getClass().getSimpleName() + "/config.yml");
        } catch (Exception ignored) {
        }
        return null;
    }
}