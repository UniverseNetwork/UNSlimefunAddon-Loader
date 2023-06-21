package id.universenetwork.sfa_loader.templates;

import id.universenetwork.infinitylib.core.AddonConfig;
import lombok.Getter;

public abstract class AddonTemplate {
    @Getter
    private AddonConfig config;

    public AddonTemplate() {
        try {
            this.config = new AddonConfig("addons-config/" + getClass().getSimpleName() + "/config.yml");
            return;
        } catch (Exception ignored) {
        }
        this.config = null;
    }

    public abstract void onLoad();

    public void onUnload() {
    }
}