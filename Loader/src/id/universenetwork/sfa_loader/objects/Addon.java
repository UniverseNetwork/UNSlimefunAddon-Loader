package id.universenetwork.sfa_loader.objects;

import id.universenetwork.sfa_loader.annotations.*;
import id.universenetwork.sfa_loader.enums.LoadPriority;
import id.universenetwork.sfa_loader.enums.PaperRequirementLevel;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AbstractAddon;
import id.universenetwork.sfa_loader.managers.LibraryManager;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import id.universenetwork.sfa_loader.utils.LogUtils;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public final class Addon {
    private final Class<? extends AddonTemplate> addonClass;

    public Addon(Class<? extends AddonTemplate> addonClass) {
        this.addonClass = addonClass;
    }

    public boolean isEnabledInConfig() {
        return AbstractAddon.getAddonConfig().getBoolean("addons." + getName().toLowerCase());
    }

    public LoadPriority getPriority() {
        return addonClass.isAnnotationPresent(AddonLoadOrder.class) ?
                addonClass.getAnnotation(AddonLoadOrder.class).value() : LoadPriority.NORMAL;
    }

    public String getName() {
        if (addonClass.isAnnotationPresent(AddonInfo.class)) {
            String id = addonClass.getAnnotation(AddonInfo.class).name();
            if (!id.isEmpty()) return id;
        }
        return addonClass.getSimpleName();
    }

    public boolean hasDependency() {
        return addonClass.isAnnotationPresent(AddonDependencies.class);
    }

    public String[] getDependencies() {
        return hasDependency() ? addonClass.getAnnotation(AddonDependencies.class).value() : new String[]{};
    }

    public boolean requireLibrary() {
        return addonClass.isAnnotationPresent(AddonLibrary.class) || addonClass.isAnnotationPresent(AddonLibraries.class);
    }

    public AddonLibrary[] getLibraries() {
        AddonLibrary[] libs = new AddonLibrary[]{};
        if (addonClass.isAnnotationPresent(AddonLibrary.class))
            libs = addonClass.getAnnotationsByType(AddonLibrary.class);
        else if (addonClass.isAnnotationPresent(AddonLibraries.class))
            libs = addonClass.getAnnotation(AddonLibraries.class).value();
        return libs;
    }

    public void loadLibraries() {
        for (AddonLibrary lib : getLibraries()) {
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

    public boolean passPaperRequirement() {
        if (addonClass.isAnnotationPresent(AddonInfo.class)) {
            PaperRequirementLevel lvl = addonClass.getAnnotation(AddonInfo.class).requirePaper();
            List<String> txt = new ArrayList<>();
            txt.add("====================================================");
            if (lvl.equals(PaperRequirementLevel.MUST)) txt.add(" " + getName() + " only works if you use Paper");
            if (lvl.equals(PaperRequirementLevel.RECOMMENDED)) {
                txt.add(" " + getName() + " works better if you use Paper");
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

    public boolean hasHook() {
        return addonClass.isAnnotationPresent(AddonHooks.class);
    }

    public Set<String> getHooks() {
        Set<String> hooks = new HashSet<>();
        if (hasHook()) Collections.addAll(hooks, addonClass.getAnnotation(AddonHooks.class).value());
        return hooks;
    }

    @SuppressWarnings("unchecked")
    public <A extends AddonTemplate> A createNewInstance()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (A) addonClass.getConstructor().newInstance();
    }
}
