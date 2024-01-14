package id.universenetwork.sfa_loader.template;

import id.universenetwork.sfa_loader.libraries.infinitylib.core.AddonConfig;
import lombok.Getter;

@Getter
public abstract class AddonTemplate {
    private AddonConfig config;

    public AddonTemplate() {
        try {
            config = new AddonConfig("addons-config/" + getClass().getSimpleName() + "/config.yml");
        } catch (Exception ignore) {
        }
    }

    public abstract void onLoad();

    public void onUnload() {
    }
}