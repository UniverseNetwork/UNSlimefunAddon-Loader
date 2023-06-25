package id.universenetwork.sfa_loader;

import id.universenetwork.sfa_loader.annotation.AddonDependencies;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.SlimefunAddonInstance;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import id.universenetwork.sfa_loader.utils.LogUtils;
import id.universenetwork.sfa_loader.utils.TextUtils;
import id.universenetwork.sfa_loader.utils.TookTimeUtils;
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
    private final ConfigurationSection msg = AbstractAddon.config()
            .getConfigurationSection("loader-settings.load-msg");

    public void loadEnabledAddons() {
        new SlimefunAddonInstance("UniverseNetwork", "SlimefunAddon-Loader");
        LogUtils.info(msg.getString("start",
                "&eStart loading the enabled addons in the configuration file..."));
        TookTimeUtils tookTime = new TookTimeUtils();
        Set<Class<? extends AddonTemplate>> classes = getAllAddonsClasses();
        for (Class<? extends AddonTemplate> addon : classes) {
            if (AbstractAddon.config().getBoolean("addons." + addon.getSimpleName().toLowerCase()))
                loadAddon(addon);
        }
        String str = msg.getString("completed",
                "&bTook %time%ms &ato register all enabled addons to Slimefun!");
        str = StringUtils.replace(str, "%time%", tookTime.getTime());
        LogUtils.info(str);
    }

    public void loadAddon(Class<? extends AddonTemplate> addonClass) {
        try {
            AddonTemplate addon = addonClass.getConstructor().newInstance();
            if (loadedAddons.contains(addon)) throw new IllegalStateException(
                    addonClass.getSimpleName() + " addon is already loaded!");
            AddonDependencies dependencies = addonClass.getDeclaredAnnotation(AddonDependencies.class);
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
                    String str1 = msg.getString("normal", "&bSuccessfully load &d%addon% &baddon!");
                    str1 = StringUtils.replace(str1, "%addon%", addonClass.getSimpleName());
                    LogUtils.info(str1);
                    break;
                case 1:
                    String str2 = msg.getString("with-dependencies", "&a%dependencies% found. " +
                            "&bSuccessfully loaded &d%addon% &baddon!");
                    str2 = StringUtils.replaceEach(str2, new String[]{"%addon%", "%dependencies%"},
                            new String[]{addonClass.getSimpleName(), TextUtils.convertArraysToString(
                                    dependencies.value())});
                    LogUtils.info(str2);
                    break;
                case 2:
                    String str3 = msg.getString("with-dependencies", "&e%dependencies% not found. " +
                            "&cYou need &e%dependencies% to use &d%addon% &caddon!");
                    str3 = StringUtils.replaceEach(str3, new String[]{"%addon%", "%dependencies%"},
                            new String[]{addonClass.getSimpleName(), TextUtils.convertArraysToString(
                                    dependencies.value())});
                    LogUtils.severe(str3);
            }
        } catch (Exception e) {
            String str = msg.getString("error", "An error occurred while loading &d%addon% &caddon!");
            str = StringUtils.replace(str, "%addon%", addonClass.getSimpleName());
            LogUtils.log(Level.SEVERE, str, e);
        }
    }

    public void unloadAllAddons() {
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