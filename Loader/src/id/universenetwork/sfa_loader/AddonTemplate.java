package id.universenetwork.sfa_loader;

import id.universenetwork.infinitylib.core.AddonConfig;
import lombok.Getter;

public abstract class AddonTemplate {
    @Getter
    private final AddonConfig config;

    public AddonTemplate() {
        this.config = new AddonConfig(getClass().getSimpleName() + "/config.yml");
    }

    public abstract void onLoad();

    public void onUnload() {
    }
}