package id.universenetwork.sfa_loader.template;

import id.universenetwork.sfa_loader.AddonsLoader;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.AddonConfig;
import lombok.Getter;

@Getter
public abstract class AddonTemplate {
    private AddonConfig config;

    public AddonTemplate() {
        try {
            config = new AddonConfig("addons-config/" + AddonsLoader.getAddonName(getClass()) + "/config.yml");
        } catch (Exception ignore) {
        }
    }

    public abstract void onLoad();

    public void onUnload() {
    }
}