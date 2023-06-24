package id.universenetwork.sfa_loader.template;

import id.universenetwork.sfa_loader.libraries.infinitylib.core.AddonConfig;
import lombok.Getter;

public abstract class AddonTemplate {
    @Getter
    private static AddonConfig config;

    public AddonTemplate() {
        AddonConfig cfg = null;
        try {
            cfg = new AddonConfig("addons-config/" + getClass().getSimpleName() + "/config.yml");
        } catch (Exception ignored) {
        }
        config = cfg;
    }

    public abstract void onLoad();

    public void onUnload() {
    }
}