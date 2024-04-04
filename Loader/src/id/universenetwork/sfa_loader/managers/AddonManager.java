package id.universenetwork.sfa_loader.managers;

import id.universenetwork.sfa_loader.enums.LoadPriority;
import id.universenetwork.sfa_loader.objects.Addon;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public final class AddonManager {
    private final Set<Addon> allAddons;

    public AddonManager(Set<Class<? extends AddonTemplate>> classes) {
        allAddons = classes.stream().map(Addon::new).collect(Collectors.toSet());
    }

    public Filter getWithFilter() {
        return new Filter();
    }

    public final class Filter {
        private Stream<Addon> addons = allAddons.stream();

        public Filter wasEnabled(boolean status) {
            addons = addons.filter(a -> a.isEnabledInConfig() == status);
            return this;
        }

        public Filter wasEnabled() {
            return wasEnabled(true);
        }

        public Filter hasPriority(LoadPriority priority) {
            addons = addons.filter(a -> a.getPriority().equals(priority));
            return this;
        }

        public Filter hasDependency(boolean status) {
            addons = addons.filter(a -> a.hasDependency() == status);
            return this;
        }

        public Filter hasDependency() {
            return hasDependency(true);
        }

        public Filter hasHook(boolean status) {
            addons = addons.filter(a -> a.hasHook() == status);
            return this;
        }

        public Filter hasHook() {
            return hasHook(true);
        }

        public Filter requireLibrary(boolean status) {
            addons = addons.filter(a -> a.requireLibrary() == status);
            return this;
        }

        public Filter requireLibrary() {
            return requireLibrary(true);
        }

        public Filter clearFilter() {
            addons = allAddons.stream();
            return this;
        }

        public Set<Addon> getResult() {
            return addons.collect(Collectors.toSet());
        }
    }
}