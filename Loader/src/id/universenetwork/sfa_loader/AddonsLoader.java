package id.universenetwork.sfa_loader;

import id.universenetwork.sfa_loader.annotations.*;
import id.universenetwork.sfa_loader.enums.PaperRequirementLevel;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.SlimefunAddonInstance;
import id.universenetwork.sfa_loader.managers.LibraryManager;
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

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@UtilityClass
public class AddonsLoader {
    @Getter
    private final Set<Class<? extends AddonTemplate>> allAddonsClasses = new HashSet<>();
    private final Set<Class<? extends AddonTemplate>> addonsWithHooksClasses = new HashSet<>();
    @Getter
    private final Set<AddonTemplate> loadedAddons = new HashSet<>();
    private final Map<Class<? extends AddonTemplate>, String> addonName = new HashMap<>();

    public void loadEnabledAddons() {
        Reflections reflections = new Reflections("id.universenetwork.sfa_loader.addons");
        allAddonsClasses.addAll(reflections.getSubTypesOf(AddonTemplate.class));

        new SlimefunAddonInstance("UniverseNetwork", "SlimefunAddon-Loader");

        LogUtils.info("&eStart loading the enabled addons in the configuration file...");
        TookTimeUtils tookTime = new TookTimeUtils();

        for (Class<? extends AddonTemplate> addon : allAddonsClasses) {
            String name = getAddonName(addon);
            if (AbstractAddon.config().getBoolean("addons." + name.toLowerCase()))
                loadAddon(addon, name);
        }
        if (!addonsWithHooksClasses.isEmpty())
            for (Iterator<Class<? extends AddonTemplate>> i = addonsWithHooksClasses.iterator(); i.hasNext(); )
                loadAddonWithHooks(i);

        String str = "&bTook " + tookTime.getTime() + "ms &ato load all enabled addons to Slimefun!";
        LogUtils.info(str);
    }

    private void loadAddon(Class<? extends AddonTemplate> addonClass, String name) {
        if (addonClass.isAnnotationPresent(AddonHooks.class)) {
            addonsWithHooksClasses.add(addonClass);
            addonName.put(addonClass, name);
            return;
        }

        try {
            final AddonTemplate addon = addonClass.getConstructor().newInstance();

            if (loadedAddons.contains(addon)) throw new IllegalStateException(
                    name + " addon is already loaded!");

            if (addonClass.isAnnotationPresent(AddonLibrary.class))
                loadAddonLibraries(addonClass.getAnnotationsByType(AddonLibrary.class));

            else if (addonClass.isAnnotationPresent(AddonLibraries.class))
                loadAddonLibraries(addonClass.getAnnotation(AddonLibraries.class).value());

            final AddonDependencies dependencies = addonClass.getAnnotation(AddonDependencies.class);
            boolean hasDependency = false;

            String str = "&bSuccessfully loaded &d" + name + " &baddon!";

            if (dependencies != null) {
                hasDependency = true;
                for (String dpy : dependencies.value())
                    if (!Bukkit.getPluginManager().isPluginEnabled(dpy)) {
                        str = "&e" + TextUtils.convertArraysToString(dependencies.value()) +
                                " not found. &cYou need &e" +
                                TextUtils.convertArraysToString(dependencies.value()) + " to use &d"
                                + name + " &caddon!";
                        LogUtils.severe(str);
                        return;
                    }
            }

            if (!PaperLib.isPaper()) if (!passPaperRequirements(addonClass, name)) return;

            addon.onLoad();
            loadedAddons.add(addon);

            if (hasDependency) str = StringUtils.replace(str, "&bS", "&a" +
                    TextUtils.convertArraysToString(dependencies.value()) + " found. &bS");

            LogUtils.info(str);
        } catch (Exception e) {
            String str = "An error occurred while loading &d" + name + " &caddon!";
            LogUtils.log(Level.SEVERE, str, e);
        }
    }

    private void loadAddonWithHooks(Iterator<Class<? extends AddonTemplate>> addonIterator) {
        final Class<? extends AddonTemplate> addonClass = addonIterator.next();
        final Set<String> hooks = Arrays.stream(addonClass.getAnnotation(AddonHooks.class)
                .value()).collect(Collectors.toSet());
        for (Iterator<String> iterator = hooks.iterator(); iterator.hasNext(); ) {
            String hook = iterator.next();
            if (AbstractAddon.config().getBoolean("addons." + hook.toLowerCase())) {
                if (isAddonLoaded(hook)) continue;
                addonIterator.remove();
                addonsWithHooksClasses.add(addonClass);
                return;
            }
            iterator.remove();
        }

        String name = addonName.get(addonClass);

        try {
            final AddonTemplate addon = addonClass.getConstructor().newInstance();

            if (loadedAddons.contains(addon)) throw new IllegalStateException(
                    name + " addon is already loaded!");

            if (addonClass.isAnnotationPresent(AddonLibrary.class))
                loadAddonLibraries(addonClass.getAnnotationsByType(AddonLibrary.class));

            else if (addonClass.isAnnotationPresent(AddonLibraries.class))
                loadAddonLibraries(addonClass.getAnnotation(AddonLibraries.class).value());

            final AddonDependencies dependencies = addonClass.getAnnotation(AddonDependencies.class);
            boolean hasDependency = false;

            String str = "&bSuccessfully loaded &d" + name + " &baddon!";

            if (dependencies != null) {
                hasDependency = true;
                for (String dpy : dependencies.value())
                    if (!Bukkit.getPluginManager().isPluginEnabled(dpy)) {
                        str = "&e" + TextUtils.convertArraysToString(dependencies.value()) +
                                " not found. &cYou need &e" +
                                TextUtils.convertArraysToString(dependencies.value()) + " to use &d"
                                + name + " &caddon!";
                        LogUtils.severe(str);
                        return;
                    }
            }

            if (!PaperLib.isPaper()) if (!passPaperRequirements(addonClass, name)) return;

            addon.onLoad();
            loadedAddons.add(addon);

            if (!hooks.isEmpty()) str = StringUtils.replace(str, "!", ", &awith &d" +
                    TextUtils.convertArraysToString(hooks.toArray(new String[0])) + " &asupport!");

            if (hasDependency) str = StringUtils.replace(str, "&bS", "&a" +
                    TextUtils.convertArraysToString(dependencies.value()) + " found. &bS");

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

    private void loadAddonLibraries(AddonLibrary[] libraries) {
        for (AddonLibrary lib : libraries) {
            String packageRelocation = lib.packageRelocation();
            String packageRelocationName = lib.packageRelocationName();
            String repository = lib.repository();

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

    private boolean passPaperRequirements(Class<? extends AddonTemplate> addon, String name) {
        if (addon.isAnnotationPresent(AddonInfo.class)) {
            PaperRequirementLevel lvl = addon.getAnnotation(AddonInfo.class).requirePaper();
            List<String> txt = new ArrayList<>();
            txt.add("====================================================");
            if (lvl.equals(PaperRequirementLevel.MUST)) txt.add(" " + name + " only works if you use Paper");
            if (lvl.equals(PaperRequirementLevel.RECOMMENDED)) {
                txt.add(" " + name + " works better if you use Paper");
                txt.add(" as your server software.");
            }
            if (System.getProperty("paperlib.shown-benefits") == null) {
                System.setProperty("paperlib.shown-benefits", "1");
                txt.add("");
                txt.add(" Paper offers significant performance improvements,");
                txt.add(" bug fixes, security enhancements and optional");
                txt.add(" features for server owners to enhance their server.");
                txt.add("");
                txt.add(" Paper includes Timings v2, which is significantly");
                txt.add(" better at diagnosing lag problems over v1.");
                txt.add("");
                txt.add(" All of your plugins should still work, and the");
                txt.add(" Paper community will gladly help you fix any issues.");
                txt.add("");
                txt.add(" Join the Paper Community @ https://papermc.io");
            }
            txt.add("====================================================");
            if (lvl.equals(PaperRequirementLevel.MUST)) {
                for (String s : txt) LogUtils.severe(s);
                return false;
            }
        }
        return true;
    }

    public String getAddonName(Class<? extends AddonTemplate> addon) {
        if (addon.isAnnotationPresent(AddonInfo.class)) {
            String id = addon.getAnnotation(AddonInfo.class).name();
            if (!id.isEmpty()) return id;
        }
        return addon.getSimpleName();
    }
}