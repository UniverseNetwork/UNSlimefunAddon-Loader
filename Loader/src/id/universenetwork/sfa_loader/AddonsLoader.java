package id.universenetwork.sfa_loader;

import id.universenetwork.sfa_loader.enums.LoadPriority;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.SlimefunAddonInstance;
import id.universenetwork.sfa_loader.managers.AddonManager;
import id.universenetwork.sfa_loader.objects.Addon;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import id.universenetwork.sfa_loader.utils.LogUtils;
import id.universenetwork.sfa_loader.utils.TextUtils;
import id.universenetwork.sfa_loader.utils.TookTimeUtils;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

@UtilityClass
public class AddonsLoader {
    @Getter
    private AddonManager addonManager;
    private Set<Addon> addonsWithHooks = new HashSet<>();
    @Getter
    private final Set<AddonTemplate> loadedAddons = new HashSet<>();

    public void loadEnabledAddons() {
        Reflections reflections = new Reflections("id.universenetwork.sfa_loader.addons");
        addonManager = new AddonManager(reflections.getSubTypesOf(AddonTemplate.class));

        new SlimefunAddonInstance("UniverseNetwork", "SlimefunAddon-Loader").initialize();

        LogUtils.info("&eStart loading the enabled addons in the configuration file...");
        TookTimeUtils tookTime = new TookTimeUtils();

        AddonManager.Filter filtered = addonManager.getWithFilter();

        filtered.wasEnabled().hasPriority(LoadPriority.HIGHEST).hasHook(false);
        for (Addon addon : filtered.getResult()) loadAddon(addon);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.HIGHEST).hasHook();
        addonsWithHooks = filtered.getResult();
        if (!addonsWithHooks.isEmpty())
            for (Iterator<Addon> i = addonsWithHooks.iterator(); i.hasNext(); )
                loadAddonWithHooks(i);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.HIGH).hasHook(false);
        for (Addon addon : filtered.getResult()) loadAddon(addon);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.HIGH).hasHook();
        addonsWithHooks = filtered.getResult();
        if (!addonsWithHooks.isEmpty())
            for (Iterator<Addon> i = addonsWithHooks.iterator(); i.hasNext(); )
                loadAddonWithHooks(i);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.NORMAL).hasHook(false);
        for (Addon addon : filtered.getResult()) loadAddon(addon);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.NORMAL).hasHook();
        addonsWithHooks = filtered.getResult();
        if (!addonsWithHooks.isEmpty())
            for (Iterator<Addon> i = addonsWithHooks.iterator(); i.hasNext(); )
                loadAddonWithHooks(i);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.LOW).hasHook(false);
        for (Addon addon : filtered.getResult()) loadAddon(addon);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.LOW).hasHook();
        addonsWithHooks = filtered.getResult();
        if (!addonsWithHooks.isEmpty())
            for (Iterator<Addon> i = addonsWithHooks.iterator(); i.hasNext(); )
                loadAddonWithHooks(i);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.LOWEST).hasHook(false);
        for (Addon addon : filtered.getResult()) loadAddon(addon);

        filtered.clearFilter().wasEnabled().hasPriority(LoadPriority.LOWEST).hasHook();
        addonsWithHooks = filtered.getResult();
        if (!addonsWithHooks.isEmpty())
            for (Iterator<Addon> i = addonsWithHooks.iterator(); i.hasNext(); )
                loadAddonWithHooks(i);

        String str = "&bTook " + tookTime.getTime() + "ms &ato load all enabled addons to Slimefun!";
        LogUtils.info(str);
    }

    private void loadAddon(Addon addonObj) {
        final String name = addonObj.getName();

        try {
            final AddonTemplate addon = addonObj.createNewInstance();

            if (loadedAddons.contains(addon)) throw new IllegalStateException(
                    name + " addon is already loaded!");

            if (addonObj.requireLibrary()) addonObj.loadLibraries();

            String str = "&bSuccessfully loaded &d" + name + " &baddon!";

            final String[] dependencies = addonObj.getDependencies();
            if (addonObj.hasDependency()) {
                for (String dpy : dependencies)
                    if (!Bukkit.getPluginManager().isPluginEnabled(dpy)) {
                        str = "&e" + TextUtils.convertArraysToString(dependencies) +
                                " not found. &cYou need &e" +
                                TextUtils.convertArraysToString(dependencies) + " to use &d"
                                + name + " &caddon!";
                        LogUtils.severe(str);
                        return;
                    }
            }

            if (!PaperLib.isPaper()) if (!addonObj.passPaperRequirement()) return;

            addon.onLoad();
            loadedAddons.add(addon);

            if (addonObj.hasDependency()) str = StringUtils.replace(str, "&bS", "&a" +
                    TextUtils.convertArraysToString(dependencies) + " found. &bS");

            LogUtils.info(str);
        } catch (Exception e) {
            String str = "An error occurred while loading &d" + name + " &caddon!";
            LogUtils.log(Level.SEVERE, str, e);
        }
    }

    private void loadAddonWithHooks(Iterator<Addon> addonIterator) {
        final Addon addonObj = addonIterator.next();
        final Set<String> hooks = addonObj.getHooks();
        for (Iterator<String> iterator = hooks.iterator(); iterator.hasNext(); ) {
            String hook = iterator.next();
            if (AbstractAddon.getAddonConfig().getBoolean("addons." + hook.toLowerCase())) {
                if (isAddonLoaded(hook)) continue;
                addonIterator.remove();
                addonsWithHooks.add(addonObj);
                return;
            }
            iterator.remove();
        }

        final String name = addonObj.getName();

        try {
            final AddonTemplate addon = addonObj.createNewInstance();

            if (loadedAddons.contains(addon)) throw new IllegalStateException(
                    name + " addon is already loaded!");

            if (addonObj.requireLibrary()) addonObj.loadLibraries();

            String str = "&bSuccessfully loaded &d" + name + " &baddon!";

            final String[] dependencies = addonObj.getDependencies();
            if (addonObj.hasDependency()) {
                for (String dpy : dependencies)
                    if (!Bukkit.getPluginManager().isPluginEnabled(dpy)) {
                        str = "&e" + TextUtils.convertArraysToString(dependencies) +
                                " not found. &cYou need &e" +
                                TextUtils.convertArraysToString(dependencies) + " to use &d"
                                + name + " &caddon!";
                        LogUtils.severe(str);
                        return;
                    }
            }

            if (!PaperLib.isPaper()) if (!addonObj.passPaperRequirement()) return;

            addon.onLoad();
            loadedAddons.add(addon);

            if (!hooks.isEmpty()) str = StringUtils.replace(str, "!", ", &awith &d" +
                    TextUtils.convertArraysToString(hooks.toArray(new String[0])) + " &asupport!");

            if (addonObj.hasDependency()) str = StringUtils.replace(str, "&bS", "&a" +
                    TextUtils.convertArraysToString(dependencies) + " found. &bS");

            LogUtils.info(str);
        } catch (Exception e) {
            String str = "An error occurred while loading &d" + name + " &caddon!";
            LogUtils.log(Level.SEVERE, str, e);
        }
    }

    public boolean isAddonLoaded(String addonName) {
        return loadedAddons.stream().anyMatch(addon -> addon.getClass().getSimpleName()
                .equalsIgnoreCase(addonName));
    }

    public void unloadAllAddons() {
        for (AddonTemplate addon : loadedAddons) addon.onUnload();
    }
}