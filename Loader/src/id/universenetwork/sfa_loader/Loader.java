package id.universenetwork.sfa_loader;

import id.universenetwork.infinitylib.core.AbstractAddon;
import lombok.experimental.UtilityClass;
import org.reflections.Reflections;

import java.util.Set;

@UtilityClass
public class Loader {
    public static void loadAddons() {
        // TODO: Will be added later
        // new SlimefunAddonInstance("UniverseNetwork", "SlimefunAddon-Loader");
        // long time = System.currentTimeMillis();
        final Set<Class<? extends AddonTemplate>> classes = getAllAddonsClasses();
        for (Class<? extends AddonTemplate> c : classes) {
            AbstractAddon.instance().getLogger().info(c.getName());
        }
    }

    public static Set<Class<? extends AddonTemplate>> getAllAddonsClasses() {
        Reflections reflections = new Reflections("id.universenetwork.sfa_loader.addons");
        return reflections.getSubTypesOf(AddonTemplate.class);
    }
}