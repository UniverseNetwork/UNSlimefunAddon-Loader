package id.universenetwork.sfa_loader.objects;

import id.universenetwork.sfa_loader.utils.TextUtils;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Getter
public final class SpecialCommandSender implements Permissible {
    private final CommandSender sender;

    public SpecialCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player getPlayer() {
        return (Player) sender;
    }

    public boolean isConsole() {
        return sender instanceof ConsoleCommandSender;
    }

    public ConsoleCommandSender getConsole() {
        return (ConsoleCommandSender) sender;
    }

    public <T> boolean isAs(Class<T> type) {
        return sender.getClass().isAssignableFrom(type);
    }

    @SuppressWarnings("unchecked")
    public <T extends CommandSender> T getAs(Class<T> type) {
        return (T) sender;
    }

    public void sendMessage(@NotNull String s) {
        TextUtils.send(sender, s);
    }

    public void sendMessage(@NotNull String... strings) {
        TextUtils.send(sender, strings);
    }

    @NotNull
    public Server getServer() {
        return sender.getServer();
    }

    @NotNull
    public String getName() {
        return sender.getName();
    }

    @NotNull
    public CommandSender.Spigot spigot() {
        return sender.spigot();
    }

    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return sender.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return sender.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(@NotNull String s) {
        return sender.hasPermission(s);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return sender.addAttachment(plugin, s, b);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return sender.addAttachment(plugin);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return sender.addAttachment(plugin, s, b, i);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return sender.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        sender.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        sender.recalculatePermissions();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return sender.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return sender.isOp();
    }

    @Override
    public void setOp(boolean b) {
        sender.setOp(b);
    }
}