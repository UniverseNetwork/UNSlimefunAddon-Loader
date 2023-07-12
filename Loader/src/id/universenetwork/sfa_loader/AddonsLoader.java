package id.universenetwork.sfa_loader;

import id.universenetwork.sfa_loader.annotations.AddonDependencies;
import id.universenetwork.sfa_loader.annotations.AddonHooks;
import id.universenetwork.sfa_loader.annotations.AddonLibraries;
import id.universenetwork.sfa_loader.annotations.AddonLibrary;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.SlimefunAddonInstance;
import id.universenetwork.sfa_loader.managers.LibraryManager;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import id.universenetwork.sfa_loader.utils.LogUtils;
import id.universenetwork.sfa_loader.utils.TextUtils;
import id.universenetwork.sfa_loader.utils.TookTimeUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

@UtilityClass
public class AddonsLoader {
    @Getter
    private final static Set<Class<? extends AddonTemplate>> allAddonsClasses = new HashSet<>();
    private final static Set<Class<? extends AddonTemplate>> addonsWithHooksClasses = new HashSet<>();
    @Getter
    private final static Set<AddonTemplate> loadedAddons = new HashSet<>();

    public void loadEnabledAddons() {
        Reflections reflections = new Reflections("id.universenetwork.sfa_loader.addons");
        allAddonsClasses.addAll(reflections.getSubTypesOf(AddonTemplate.class));

        new SlimefunAddonInstance("UniverseNetwork", "SlimefunAddon-Loader");

        LogUtils.info("&eStart loading the enabled addons in the configuration file...");
        TookTimeUtils tookTime = new TookTimeUtils();

        for (Class<? extends AddonTemplate> addon : allAddonsClasses)
            if (AbstractAddon.config().getBoolean("addons." + addon.getSimpleName().toLowerCase()))
                loadAddon(addon, true);
        if (!addonsWithHooksClasses.isEmpty())
            for (Class<? extends AddonTemplate> addon : addonsWithHooksClasses)
                loadAddonWithHooks(addon);

        String str = "&bTook " + tookTime.getTime() + "ms &ato load all enabled addons to Slimefun!";
        LogUtils.info(str);
    }

    public void loadAddon(Class<? extends AddonTemplate> addonClass, boolean scanHooks) {
        if (addonClass.isAnnotationPresent(AddonHooks.class) && scanHooks) {
            addonsWithHooksClasses.add(addonClass);
            return;
        }

        try {
            final AddonTemplate addon = addonClass.getConstructor().newInstance();

            if (loadedAddons.contains(addon)) throw new IllegalStateException(
                    addonClass.getSimpleName() + " addon is already loaded!");

            if (addonClass.isAnnotationPresent(AddonLibrary.class))
                loadAddonLibraries(addonClass.getAnnotationsByType(AddonLibrary.class));

            else if (addonClass.isAnnotationPresent(AddonLibraries.class))
                loadAddonLibraries(addonClass.getAnnotation(AddonLibraries.class).value());

            final AddonDependencies dependencies = addonClass.getAnnotation(AddonDependencies.class);
            boolean hasDependency = false;

            String str = "&bSuccessfully loaded &d" + addonClass.getSimpleName() + " &baddon!";

            if (dependencies != null) {
                hasDependency = true;
                for (String dpy : dependencies.value())
                    if (!Bukkit.getPluginManager().isPluginEnabled(dpy)) {
                        str = "&e" + TextUtils.convertArraysToString(dependencies.value()) +
                                " not found. &cYou need &e" +
                                TextUtils.convertArraysToString(dependencies.value()) + " to use &d"
                                + addonClass.getSimpleName() + " &caddon!";
                        LogUtils.severe(str);
                        return;
                    }
            }

            addon.onLoad();
            loadedAddons.add(addon);

            if (hasDependency) str = StringUtils.replace(str, "&bS", "&a" +
                    TextUtils.convertArraysToString(dependencies.value()) + " found. &bS");

            LogUtils.info(str);
        } catch (Exception e) {
            String str = "An error occurred while loading &d" + addonClass.getSimpleName() + " &caddon!";
            LogUtils.log(Level.SEVERE, str, e);
        }
    }

    private void loadAddonWithHooks(Class<? extends AddonTemplate> addonClass) {
        final Set<String> hooks = Arrays.stream(addonClass.getAnnotation(AddonHooks.class)
                .value()).collect(Collectors.toSet());
        for (Iterator<String> iterator = hooks.iterator(); iterator.hasNext(); ) {
            String hook = iterator.next();
            if (AbstractAddon.config().getBoolean("addons." + hook.toLowerCase())) {
                if (isAddonLoaded(hook)) {
                    addonsWithHooksClasses.remove(addonClass);
                    continue;
                } else {
                    addonsWithHooksClasses.remove(addonClass);
                    addonsWithHooksClasses.add(addonClass);
                    return;
                }
            }
            iterator.remove();
        }

        try {
            final AddonTemplate addon = addonClass.getConstructor().newInstance();

            if (loadedAddons.contains(addon)) throw new IllegalStateException(
                    addonClass.getSimpleName() + " addon is already loaded!");

            if (addonClass.isAnnotationPresent(AddonLibrary.class))
                loadAddonLibraries(addonClass.getAnnotationsByType(AddonLibrary.class));

            else if (addonClass.isAnnotationPresent(AddonLibraries.class))
                loadAddonLibraries(addonClass.getAnnotation(AddonLibraries.class).value());

            final AddonDependencies dependencies = addonClass.getAnnotation(AddonDependencies.class);
            boolean hasDependency = false;

            String str = "&bSuccessfully loaded &d" + addonClass.getSimpleName() + " &baddon!";

            if (dependencies != null) {
                hasDependency = true;
                for (String dpy : dependencies.value())
                    if (!Bukkit.getPluginManager().isPluginEnabled(dpy)) {
                        str = "&e" + TextUtils.convertArraysToString(dependencies.value()) +
                                " not found. &cYou need &e" +
                                TextUtils.convertArraysToString(dependencies.value()) + " to use &d"
                                + addonClass.getSimpleName() + " &caddon!";
                        LogUtils.severe(str);
                        return;
                    }
            }

            addon.onLoad();
            loadedAddons.add(addon);

            if (!hooks.isEmpty()) str = StringUtils.replace(str, "!", ", &awith &d" +
                    TextUtils.convertArraysToString(hooks.toArray(new String[0])) + " &asupport!");

            if (hasDependency) str = StringUtils.replace(str, "&bS", "&a" +
                    TextUtils.convertArraysToString(dependencies.value()) + " found. &bS");

            LogUtils.info(str);
        } catch (Exception e) {
            String str = "An error occurred while loading &d" + addonClass.getSimpleName() + " &caddon!";
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

    private void loadAddonLibraries(AddonLibrary[] libraries) {
        for (AddonLibrary lib : libraries) {
            String packageRelocation = lib.packageRelocation();
            String packageRelocationName = lib.packageRelocationName();
            String repository = lib.packageRelocationName();

            if (packageRelocation.isEmpty()) packageRelocation = null;
            if (packageRelocationName.isEmpty()) packageRelocationName = null;
            if (repository.isEmpty()) repository = null;

            LibraryManager.loadLibraries(
                    LibraryManager.createLibrary(
                            lib.groupId(),
                            lib.artifactId(),
                            lib.version(),
                            packageRelocation,
                            packageRelocationName,
                            repository
                    )
            );
        }
    }
}