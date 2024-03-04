package id.universenetwork.sfa_loader.template;

import id.universenetwork.sfa_loader.AddonsLoader;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AddonConfig;
import lombok.Getter;

import java.io.File;

@Getter
public abstract class AddonTemplate {
    private AddonConfig config;
    private final String addonFolderString = "addons-config/" + AddonsLoader.getAddonName(getClass());
    private final File addonFolder = new File(AbstractAddon.getInstance().getDataFolder(), addonFolderString);

    public AddonTemplate() {
        try {
            config = new AddonConfig(new File(addonFolder, "config.yml"));
        } catch (Exception ignore) {
        }
    }

    public abstract void onLoad();

    public void onUnload() {
    }
}