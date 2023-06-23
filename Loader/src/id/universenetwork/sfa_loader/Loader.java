package id.universenetwork.sfa_loader;

import id.universenetwork.libraries.infinitylib.core.AbstractAddon;
import id.universenetwork.libraries.infinitylib.core.SlimefunAddonInstance;
import id.universenetwork.sfa_loader.annotation.AddonDependencies;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import id.universenetwork.sfa_loader.utils.LogUtils;
import id.universenetwork.sfa_loader.utils.TextUtils;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@UtilityClass
public class Loader {
    private final static Set<AddonTemplate> loadedAddons = new HashSet<>();

    public void loadAddons() {
        new SlimefunAddonInstance("UniverseNetwork", "SlimefunAddon-Loader");
        long time = System.currentTimeMillis();
        Set<Class<? extends AddonTemplate>> classes = getAllAddonsClasses();
        ConfigurationSection msg = AbstractAddon.config().getConfigurationSection("loader-settings.load-msg");
        for (Class<? extends AddonTemplate> c : classes) {
            try {
                if (AbstractAddon.config().getBoolean("addons." + c.getSimpleName().toLowerCase())) {
                    AddonTemplate addon = c.getConstructor().newInstance();
                    AddonDependencies dependencies = c.getDeclaredAnnotation(AddonDependencies.class);
                    int status = 0;
                    if (dependencies != null) {
                        status = 1;
                        for (String dpy : dependencies.value())
                            if (!Bukkit.getPluginManager().isPluginEnabled(dpy)) status = 2;
                    }
                    if (status == 0 || status == 1) {
                        addon.onLoad();
                        loadedAddons.add(addon);
                    }
                    switch (status) {
                        default:
                            String str1 = msg.getString("normal", "&bSuccessfully registered &d%addon% &baddon!");
                            str1 = StringUtils.replace(str1, "%addon%", c.getSimpleName());
                            LogUtils.info(str1);
                            break;
                        case 1:
                            String str2 = msg.getString("with-dependencies", "&a%dependencies% found. " +
                                    "&bSuccessfully registered &d%addon% &baddon!");
                            str2 = StringUtils.replaceEach(str2, new String[]{"%addon%", "%dependencies%"},
                                    new String[]{c.getSimpleName(), TextUtils.convertArraysToString(dependencies.value())});
                            LogUtils.info(str2);
                            break;
                        case 2:
                            String str3 = msg.getString("with-dependencies", "&e%dependencies% not found. " +
                                    "&cYou need &e%dependencies% to use &d%addon% &caddon!");
                            str3 = StringUtils.replaceEach(str3, new String[]{"%addon%", "%dependencies%"},
                                    new String[]{c.getSimpleName(), TextUtils.convertArraysToString(dependencies.value())});
                            LogUtils.severe(str3);
                    }
                }
            } catch (Exception e) {
                String str = msg.getString("error", "An error occurred while registering &d%addon% &caddon!");
                str = StringUtils.replace(str, "%addon%", c.getSimpleName());
                LogUtils.log(Level.SEVERE, str, e);
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