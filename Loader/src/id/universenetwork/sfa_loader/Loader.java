package id.universenetwork.sfa_loader;

import id.universenetwork.infinitylib.core.AbstractAddon;
import id.universenetwork.infinitylib.core.SlimefunAddonInstance;
import id.universenetwork.sfa_loader.templates.AddonTemplate;
import lombok.experimental.UtilityClass;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class Loader {
    private final static Set<AddonTemplate> loadedAddons = new HashSet<>();

    public void loadAddons() {
        new SlimefunAddonInstance("UniverseNetwork", "SlimefunAddon-Loader");
        long time = System.currentTimeMillis();
        Set<Class<? extends AddonTemplate>> classes = getAllAddonsClasses();
        for (Class<? extends AddonTemplate> c : classes) {
            try {
                if (AbstractAddon.config().getBoolean("addons." + c.getSimpleName().toLowerCase())) {
                    AddonTemplate addon = c.getConstructor().newInstance();
                    addon.onLoad();
                    loadedAddons.add(addon);
                }
            } catch (Exception e) { // Ignored for now
                e.printStackTrace();
            }
        }
    }

    public void unloadAddons() {
        for (AddonTemplate addon : loadedAddons) addon.onUnload();
    }

    public Set<Class<? extends AddonTemplate>> getAllAddonsClasses() {
        Reflections reflections = new Reflections("id.universenetwork.sfa_loader.addons");
        return reflections.getSubTypesOf(AddonTemplate.class);
    }

    public Set<AddonTemplate> getLoadedAddons() {
        return loadedAddons;
    }
}