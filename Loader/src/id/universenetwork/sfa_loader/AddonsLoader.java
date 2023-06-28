package id.universenetwork.sfa_loader;

import id.universenetwork.sfa_loader.annotation.AddonDependencies;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.SlimefunAddonInstance;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import id.universenetwork.sfa_loader.utils.LogUtils;
import id.universenetwork.sfa_loader.utils.TextUtils;
import id.universenetwork.sfa_loader.utils.TookTimeUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@UtilityClass
public class AddonsLoader {
    @Getter
    private final static Set<Class<? extends AddonTemplate>> allAddonsClasses = new HashSet<>();
    @Getter
    private final static Set<AddonTemplate> loadedAddons = new HashSet<>();

    public void loadEnabledAddons() {
        Reflections reflections = new Reflections("id.universenetwork.sfa_loader.addons");
        allAddonsClasses.addAll(reflections.getSubTypesOf(AddonTemplate.class));

        new SlimefunAddonInstance("UniverseNetwork", "SlimefunAddon-Loader");
        LogUtils.info("&eStart loading the enabled addons in the configuration file...");
        TookTimeUtils tookTime = new TookTimeUtils();
        for (Class<? extends AddonTemplate> addon : allAddonsClasses) {
            if (AbstractAddon.config().getBoolean("addons." + addon.getSimpleName().toLowerCase()))
                loadAddon(addon);
        }
        String str = "&bTook " + tookTime.getTime() + "ms &ato load all enabled addons to Slimefun!";
        LogUtils.info(str);
    }

    public void loadAddon(Class<? extends AddonTemplate> addonClass) {
        try {
            final AddonTemplate addon = addonClass.getConstructor().newInstance();
            if (loadedAddons.contains(addon)) throw new IllegalStateException(
                    addonClass.getSimpleName() + " addon is already loaded!");
            final AddonDependencies dependencies = addonClass.getDeclaredAnnotation(AddonDependencies.class);
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
                    String str1 = "&bSuccessfully loaded &d" + addonClass.getSimpleName() + " &baddon!";
                    LogUtils.info(str1);
                    break;
                case 1:
                    String str2 = "&a" + TextUtils.convertArraysToString(dependencies.value()) +
                            " found. &bSuccessfully loaded &d" + addonClass.getSimpleName() + " &baddon!";
                    LogUtils.info(str2);
                    break;
                case 2:
                    String str3 = "&e" + TextUtils.convertArraysToString(dependencies.value()) +
                            " not found. &cYou need &e" +
                            TextUtils.convertArraysToString(dependencies.value()) + " to use &d"
                            + addonClass.getSimpleName() + " &caddon!";
                    LogUtils.severe(str3);
            }
        } catch (Exception e) {
            String str = "An error occurred while loading &d" + addonClass.getSimpleName() + " &caddon!";
            LogUtils.log(Level.SEVERE, str, e);
        }
    }

    public void unloadAllAddons() {
        for (AddonTemplate addon : loadedAddons) addon.onUnload();
    }
}