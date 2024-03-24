package id.universenetwork.sfa_loader.managers;


import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AbstractAddon;
import id.universenetwork.sfa_loader.objects.SpecialCommandSender;
import id.universenetwork.sfa_loader.utils.LogUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.BuilderDecorator;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.description.CommandDescription;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.logging.Level;

@UtilityClass
public class CommandManager {
    private boolean initialized = false;
    private AnnotationParser<SpecialCommandSender> parser;

    public void init() {
        if (initialized) throw new IllegalStateException("Command Manager is already initialized!");
        LogUtils.info("&eInitializing Command Manager...");
        try {
            ExecutionCoordinator<SpecialCommandSender> executionCoordinator = ExecutionCoordinator.simpleCoordinator();
            SenderMapper<CommandSender, SpecialCommandSender> senderMapper = SenderMapper.create(
                    SpecialCommandSender::new,
                    SpecialCommandSender::getSender
            );
            PaperCommandManager<SpecialCommandSender> manager = new PaperCommandManager<>(
                    AbstractAddon.getInstance(), executionCoordinator, senderMapper);
            parser = new AnnotationParser<>(manager, SpecialCommandSender.class);
            if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) manager.registerBrigadier();
            if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
                manager.registerAsynchronousCompletions();
            parser.registerBuilderDecorator(
                    BuilderDecorator.defaultDescription(CommandDescription.commandDescription("No description"))
            );
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