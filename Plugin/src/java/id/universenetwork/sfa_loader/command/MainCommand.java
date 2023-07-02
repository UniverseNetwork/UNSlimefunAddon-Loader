package id.universenetwork.sfa_loader.command;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import id.universenetwork.sfa_loader.AddonsLoader;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.AddonConfig;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import id.universenetwork.sfa_loader.utils.LogUtils;
import id.universenetwork.sfa_loader.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class MainCommand {
    @CommandMethod("slimefunaddon-loader|sfa-loader|sfal|sfa")
    @CommandDescription("Main command of SlimefunAddon-Loader")
    public void cmd(final CommandSender sender) {
        sender.sendMessage("");
        TextUtils.sendCentered(sender, "&aSF&dAddon &bLoader &6v" + AbstractAddon.instance()
                .getDescription().getVersion());
        TextUtils.sendCentered(sender, "&dMade By &bARVIN&a3108 &cI&fD &dfor &bUniverse&eNetwork");
        sender.sendMessage("");
    }

    @CommandMethod("slimefunaddon-loader|sfa-loader|sfal|sfa reload-config all")
    @CommandPermission("sfaloader.command.reload.all")
    public void cmdReloadAll(final CommandSender sender) {
        LogUtils.info("&eReloading all configuration files...");
        AbstractAddon.config().reload();
        Set<AddonTemplate> loadedAddon = AddonsLoader.getLoadedAddons();
        for (AddonTemplate addon : loadedAddon) {
            AddonConfig config = addon.getConfig();
            if (config != null) config.reload();
        }
        if (sender instanceof Player) TextUtils.send(sender,
                "%p% &aAll configuration files have been reloaded!");
        LogUtils.info("&aAll configuration files have been reloaded!");
    }

    @CommandMethod("slimefunaddon-loader|sfa-loader|sfal|sfa reload-config plugin")
    @CommandPermission("sfaloader.command.reload.plugin")
    public void cmdReloadPlugin(final CommandSender sender) {
        LogUtils.info("&eReloading plugin configuration files...");
        AbstractAddon.config().reload();
        if (sender instanceof Player) TextUtils.send(sender,
                "%p% &aPlugin configuration files have been reloaded!");
        LogUtils.info("&aPlugin configuration files have been reloaded!");
    }

    @CommandMethod("slimefunaddon-loader|sfa-loader|sfal|sfa reload-config addons [addon]")
    @CommandPermission("sfaloader.command.reload.addons")
    public void cmdReloadPlugin(final CommandSender sender,
                                final @Argument(value = "addon", defaultValue = "all",
                                        suggestions = "enabledAddonsList") String addonName) {
        final Set<AddonTemplate> loadedAddon = AddonsLoader.getLoadedAddons();
        if (addonName.equals("all")) {
            LogUtils.info("&eReloading all addons configuration files...");
            for (AddonTemplate addon : loadedAddon) {
                AddonConfig config = addon.getConfig();
                if (config != null) config.reload();
            }
            if (sender instanceof Player) TextUtils.send(sender,
                    "%p% &aAll addons configuration files have been reloaded!");
            LogUtils.info("&aAll addons configuration files have been reloaded!");
        } else if (AddonsLoader.isAddonLoaded(addonName)) {
            Set<AddonTemplate> filtered = loadedAddon.stream().filter(addon ->
                            addon.getClass().getSimpleName().equalsIgnoreCase(addonName))
                    .collect(Collectors.toSet());
            for (AddonTemplate addon : filtered) {
                final AddonConfig cfg = addon.getConfig();
                if (cfg == null) {
                    TextUtils.send(sender, "%p% &d" + addon.getClass().getSimpleName() +
                            " &caddon has no configuration file!");
                    return;
                }
                LogUtils.info("&eReloading the &d" + addon.getClass().getSimpleName() +
                        " &eaddon configuration file...");
                cfg.reload();
                if (sender instanceof Player) TextUtils.send(sender, "%p% &d" + addon.getClass()
                        .getSimpleName() + " &aaddon configuration file have been reloaded!");
                LogUtils.info("&d" + addon.getClass().getSimpleName() +
                        " &aaddon configuration file have been reloaded!");
            }
        } else TextUtils.send(sender, "%p% &cPlease provide a valid addon name!");
    }

    @Suggestions("enabledAddonsList")
    public List<String> enabledAddonsList(CommandContext<CommandSender> sender, String context) {
        final List<String> list = new ArrayList<>();
        final Set<AddonTemplate> addons = AddonsLoader.getLoadedAddons();
        for (AddonTemplate addon : addons)
            if (addon.getConfig() != null) list.add(addon.getClass().getSimpleName());
        return list;
    }
}