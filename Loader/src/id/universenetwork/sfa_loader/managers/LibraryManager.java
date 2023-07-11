package id.universenetwork.sfa_loader.managers;

import id.universenetwork.sfa_loader.libraries.infinitylib.core.AbstractAddon;
import id.universenetwork.sfa_loader.utils.LogUtils;
import lombok.experimental.UtilityClass;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class LibraryManager {
    private boolean initialized = false;
    private BukkitLibraryManager manager;

    public void init() {
        if (initialized) throw new IllegalStateException("External Library Manager is already initialized!");
        LogUtils.info("&eInitializing External Library Manager...");
        manager = new BukkitLibraryManager(AbstractAddon.instance());

        // Add Repository
        manager.addMavenCentral();
        manager.addJitPack();

        loadLibraries(
                createLibrary("com{}github{}ronmamo", "reflections", "f5514b1")
        );
        LogUtils.info("&aExternal Library Manager has been initialized!");
        initialized = true;
    }

    public Library createLibrary(String groupId, String artifactId, String version) {
        return createLibrary(groupId, artifactId, version, null);
    }

    public Library createLibrary(String groupId, String artifactId, String version, String packageRelocation) {
        return createLibrary(groupId, artifactId, version, packageRelocation, null);
    }

    public Library createLibrary(String groupId, String artifactId, String version,
                                 String packageRelocation, String packageRelocationName) {
        final Library.Builder builder = Library.builder();
        builder.groupId(groupId).artifactId(artifactId).version(version);
        if (packageRelocation != null) {
            String[] splitted = StringUtils.split(packageRelocation, "{}");
            String relocationName = packageRelocationName != null ?
                    packageRelocationName : splitted[splitted.length - 1];
            builder.relocate(packageRelocation, "id{}universenetwork{}sfa_loader{}libraries{}"
                    + relocationName);
        }
        return builder.build();
    }

    public void loadLibraries(Library... libs) {
        for (Library lib : libs) {
            manager.loadLibrary(lib);
            LogUtils.info("&bLoaded library &e" + lib.getPath());
        }
    }
}