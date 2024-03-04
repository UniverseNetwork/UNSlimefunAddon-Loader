package id.universenetwork.sfa_loader.template;

import id.universenetwork.sfa_loader.AddonsLoader;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AddonConfig;
import lombok.Getter;

import java.io.File;

@Getter
public abstract class AddonTemplate {
    private AddonConfig config;
    private final String addonFolderString = "addons-config" + File.separator + AddonsLoader.getAddonName(getClass());

    public AddonTemplate() {
        try {
            config = new AddonConfig(addonFolderString + File.separator + "config.yml");
        } catch (Exception ignore) {
        }
    }

    public abstract void onLoad();

    public void onUnload() {
    }
}