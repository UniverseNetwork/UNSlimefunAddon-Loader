package id.universenetwork.sfa_loader.manager;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import id.universenetwork.sfa_loader.libraries.infinitylib.core.AbstractAddon;
import id.universenetwork.sfa_loader.utils.LogUtils;
import id.universenetwork.sfa_loader.utils.TextUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;

import java.util.function.Function;
import java.util.logging.Level;

@UtilityClass
public class CommandManager {
    private boolean initialized = false;
    private AnnotationParser<CommandSender> parser;

    public void init() {
        if (initialized) throw new IllegalStateException("Command Manager is already initialized!");
        LogUtils.info("&eInitializing Command Manager...");
        try {
            Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>>
                    executionCoordinatorFunction = CommandExecutionCoordinator.simpleCoordinator();
            Function<CommandSender, CommandSender> mapperFunction = Function.identity();
            PaperCommandManager<CommandSender> manager = new PaperCommandManager<>(
                    AbstractAddon.instance(), executionCoordinatorFunction, mapperFunction, mapperFunction);
            Function<ParserParameters, CommandMeta> commandMetaFunction = param -> CommandMeta.simple()
                    .with(CommandMeta.DESCRIPTION, param.get(
                            StandardParameters.DESCRIPTION, "No description"))
                    .build();
            parser = new AnnotationParser<>(manager, CommandSender.class, commandMetaFunction);
            if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) manager.registerBrigadier();
            if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
                manager.registerAsynchronousCompletions();
            manager.registerExceptionHandler(NoPermissionException.class, (sender, e) ->
                    TextUtils.send(sender, AbstractAddon.config()
                            .getString("plugin-settings.no-perm")));
            LogUtils.info("&aCommand Manager has been initialized!");
            initialized = true;
        } catch (Exception e) {
            LogUtils.log(Level.SEVERE, "An error occurred initializing Command Manager!", e);
        }
    }

    public void register(Object... cmds) {
        for (Object cmd : cmds)
            try {
                parser.parse(cmd);
            } catch (Exception e) {
                LogUtils.log(Level.SEVERE, "Failed to register command class: &e"
                        + cmd.getClass().getSimpleName(), e);
            }
    }
}