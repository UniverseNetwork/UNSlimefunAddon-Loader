package id.universenetwork.sfa_loader.command;


import id.universenetwork.sfa_loader.AddonsLoader;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AbstractAddon;
import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AddonConfig;
import id.universenetwork.sfa_loader.objects.Addon;
import id.universenetwork.sfa_loader.objects.SpecialCommandSender;
import id.universenetwork.sfa_loader.template.AddonTemplate;
import id.universenetwork.sfa_loader.utils.LogUtils;
import id.universenetwork.sfa_loader.utils.TextUtils;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.exception.ExceptionHandler;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.NoPermissionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class MainCommand {
    @Command(value = "slimefunaddon-loader|sfa-loader|sfal|sfa")
    @CommandDescription("Main command of SlimefunAddon-Loader")
    public void cmd(final SpecialCommandSender sender) {
        TextUtils.sendCentered(sender.getSender(),
                "", "&aSF&dAddon &bLoader &6v" + AbstractAddon.getInstance().getDescription().getVersion(),
                "&dMade By &bARVIN&a3108 &cI&fD &dfor &bUniverse&eNetwork", "");
    }

    @Command("slimefunaddon-loader|sfa-loader|sfal|sfa reload-config all")
    @Permission("sfaloader.command.reload.all")
    public void cmdReloadAll(final SpecialCommandSender sender) {
        LogUtils.info("&eReloading all configuration files...");
        AbstractAddon.getAddonConfig().reload();
        Set<AddonTemplate> loadedAddon = AddonsLoader.getLoadedAddons();
        for (AddonTemplate addon : loadedAddon) {
            AddonConfig config = addon.getConfig();
            if (config != null) config.reload();
        }
        if (sender.isPlayer()) TextUtils.send(sender,
                "%p% &aAll configuration files have been reloaded!");
        LogUtils.info("&aAll configuration files have been reloaded!");
    }

    @Command("slimefunaddon-loader|sfa-loader|sfal|sfa reload-config plugin")
    @Permission("sfaloader.command.reload.plugin")
    public void cmdReloadPlugin(final SpecialCommandSender sender) {
        LogUtils.info("&eReloading plugin configuration files...");
        AbstractAddon.getAddonConfig().reload();
        if (sender.isPlayer()) TextUtils.send(sender,
                "%p% &aPlugin configuration files have been reloaded!");
        LogUtils.info("&aPlugin configuration files have been reloaded!");
    }

    @Command("slimefunaddon-loader|sfa-loader|sfal|sfa reload-config addons [addon]")
    @Permission("sfaloader.command.reload.addons")
    public void cmdReloadAddon(final SpecialCommandSender sender,
                               final @Argument(value = "addon", suggestions = "enabledAddonsList") @Default("all") String addonName) {
        final Set<AddonTemplate> loadedAddon = AddonsLoader.getLoadedAddons();
        if (addonName.equals("all")) {
            LogUtils.info("&eReloading all addons configuration files...");
            for (AddonTemplate addon : loadedAddon) {
                AddonConfig config = addon.getConfig();
                if (config != null) config.reload();
            }
            if (sender.isPlayer()) TextUtils.send(sender.getSender(),
                    "%p% &aAll addons configuration files have been reloaded!");
            LogUtils.info("&aAll addons configuration files have been reloaded!");
        } else if (AddonsLoader.isAddonLoaded(addonName)) {
            Set<AddonTemplate> filtered = loadedAddon.stream().filter(addon ->
                            new Addon(addon.getClass()).getName().equalsIgnoreCase(addonName))
                    .collect(Collectors.toSet());
            for (AddonTemplate addon : filtered) {
                final String name = new Addon(addon.getClass()).getName();
                final AddonConfig cfg = addon.getConfig();
                if (cfg == null) {
                    TextUtils.send(sender, "%p% &d" + name + " &caddon has no configuration file!");
                    return;
                }
                LogUtils.info("&eReloading the &d" + name + " &eaddon configuration file...");
                cfg.reload();
                if (sender.isPlayer())
                    TextUtils.send(sender, "%p% &d" + name + " &aaddon configuration file have been reloaded!");
                LogUtils.info("&d" + name + " &aaddon configuration file have been reloaded!");
            }
        } else TextUtils.send(sender, "%p% &cPlease provide a valid addon name!");
    }

    @Suggestions("enabledAddonsList")
    public List<String> enabledAddonsList(CommandContext<SpecialCommandSender> sender, String context) {
        return AddonsLoader.getLoadedAddons().stream().filter(a -> a.getConfig() != null)
                .map(a -> new Addon(a.getClass()).getName()).collect(Collectors.toList());
    }

    @ExceptionHandler(NoPermissionException.class)
    public void handleNoPerm(SpecialCommandSender sender) {
        LogUtils.warning("Someone tried illegal actions!");
        sender.sendMessage(AbstractAddon.getAddonConfig().getString("plugin-settings.no-perm"));
    }
}