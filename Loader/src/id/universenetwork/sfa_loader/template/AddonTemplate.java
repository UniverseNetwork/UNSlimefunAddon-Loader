package id.universenetwork.sfa_loader.template;

import id.universenetwork.sfa_loader.AddonsLoader;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AddonConfig;
import id.universenetwork.sfa_loader.objects.Addon;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;

@Getter
public abstract class AddonTemplate {
    private AddonConfig config;
    private final String addonFolderString = "addons-config/" + new Addon(getClass()).getName();
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

    public InputStream getResource(String path) {
        return AbstractAddon.getInstance().getResource(addonFolderString + "/" + path);
    }
}