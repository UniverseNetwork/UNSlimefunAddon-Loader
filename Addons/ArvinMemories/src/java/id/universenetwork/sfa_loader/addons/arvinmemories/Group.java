package id.universenetwork.sfa_loader.addons.arvinmemories;

import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AbstractAddon;
import id.universenetwork.sfa_loader.utils.TextUtils;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("deprecation")
public class Group extends FlexItemGroup {
    private final ArvinMemories instance;

    private static final String TITLE = "&bArvin's &dMemo&7ries";
    private static final int GUIDE_BACK = 1;
    private static final int MEMORIES = 22;
    private static final int SOCIAL = 31;
    private static final int YT = 20;
    private static final int IG = 29;
    private static final int TWITTER = 24;
    private static final int DISCORD = 33;

    private static final int[] HEADER = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] FOOTER = new int[]{45, 46, 47, 48, 49, 50, 51, 52, 53};

    protected Group(ArvinMemories instance) {
        super(AbstractAddon.createKey("AM_GROUP"), new CustomItemStack(
                PlayerHead.getItemStack(PlayerSkin.fromBase64("ewogICJ0aW1lc3RhbXAiIDogMTY4OTM4Mzg4MzE0NSwKICAicHJvZmlsZUlkIiA6ICI2N2NiNzdmNDdkMzg0OGUyODYyZGVkYTU1NTA2ZWI4YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBUlZJTjMxMDgiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzJhMjk3ODA0ZTc2NTNmNGY5ODI0ZWI5N2U1MjQzMTRiMjQ2ZmNmNjVkYzc3YmJkMzcwNjJjZjc5ZjYxMjQ0YyIKICAgIH0KICB9Cn0=")), TITLE));
        this.instance = instance;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isVisible(Player player, PlayerProfile profile, SlimefunGuideMode mode) {
        return true;
    }

    @Override
    public void open(Player player, PlayerProfile profile, SlimefunGuideMode mode) {
        final ChestMenu chestMenu = new ChestMenu("&bArvin's &dMemo&7ries");

        for (int slot : HEADER)
            chestMenu.addItem(slot, ChestMenuUtils.getBackground(), (player1, i1, itemStack, clickAction) -> false);

        for (int slot : FOOTER)
            chestMenu.addItem(slot, ChestMenuUtils.getBackground(), (player1, i1, itemStack, clickAction) -> false);

        chestMenu.setEmptySlotsClickable(false);
        for (int slot : FOOTER) {
            chestMenu.replaceExistingItem(slot, ChestMenuUtils.getBackground());
            chestMenu.addMenuClickHandler(slot, ((player1, i, itemStack, clickAction) -> false));
        }

        // Back
        chestMenu.replaceExistingItem(
                GUIDE_BACK,
                ChestMenuUtils.getBackButton(
                        player,
                        "&7" + Slimefun.getLocalization().getMessage("guide.back.guide")
                )
        );
        chestMenu.addMenuClickHandler(GUIDE_BACK, (p, s, is, c) -> {
            SlimefunGuide.openMainMenu(profile, mode, 1);
            return false;
        });

        // Memories
        chestMenu.replaceExistingItem(MEMORIES, memories());
        chestMenu.addMenuClickHandler(MEMORIES, (p, i, is, c) -> {
            player.closeInventory();
            TextUtils.sendCentered(player,
                    "", "&b&lArv&a&lin's &d&lMemo&7&lries", "&7Catatan yang &bArv&ain &7tulis sebelum pergi", "");
            TextUtils.send(player, instance.getConfig().getStringList("memories").toArray(new String[0]));
            return false;
        });

        // Social Media
        AtomicBoolean hidden = new AtomicBoolean(true);
        chestMenu.replaceExistingItem(SOCIAL, new CustomItemStack(Material.PAPER, "&bSocial Media",
                "&eClick to see all social media that &bArv&ain &ehas"));
        chestMenu.addMenuClickHandler(SOCIAL, (p, i, is, c) -> {
            if (hidden.get()) {
                chestMenu.replaceExistingItem(SOCIAL, new CustomItemStack(Material.PAPER, "&bSocial Media",
                        "&eClick to hide all social media that &bArv&ain &ehas"));
                chestMenu.replaceExistingItem(YT, new CustomItemStack(
                        PlayerHead.getItemStack(PlayerSkin.fromHashCode("3488545d57c9eed52c3e547e96c45dabbb7cf5f98d4c8fe61dc6f69aba0aef96")),
                        "&cYou&fTube",
                        "&aKeaktifan: &7Jarang", " ",
                        "&eClick to go to social media"));
                chestMenu.replaceExistingItem(IG, new CustomItemStack(
                        PlayerHead.getItemStack(PlayerSkin.fromHashCode("ac88d6163fabe7c5e62450eb37a074e2e2c88611c998536dbd8429faa0819453")),
                        "&dInstagram",
                        "&aKeaktifan: &eAktif", " ",
                        "&eClick to go to social media"));
                chestMenu.replaceExistingItem(TWITTER, new CustomItemStack(
                        PlayerHead.getItemStack(PlayerSkin.fromHashCode("cc745a06f537aea80505559149ea16bd4a84d4491f12226818c3881c08e860fc")),
                        "&bTwitter",
                        "&aKeaktifan: &6Kadang-kadang", " ",
                        "&eClick to go to social media"));
                chestMenu.replaceExistingItem(DISCORD, new CustomItemStack(
                        PlayerHead.getItemStack(PlayerSkin.fromHashCode("6da863c3c89357a727d61d9b82d12a42bb2f1711bd95e221107340dfda2d852e")),
                        "&9Discord",
                        "&aKeaktifan: &eAktif", " ",
                        "&eClick to go to social media"));
                hidden.set(false);
            } else {
                chestMenu.replaceExistingItem(SOCIAL, new CustomItemStack(Material.PAPER, "&bSocial Media",
                        "&eClick to see all social media that &bArv&ain &ehas"));
                chestMenu.replaceExistingItem(YT, new ItemStack(Material.AIR));
                chestMenu.replaceExistingItem(IG, new ItemStack(Material.AIR));
                chestMenu.replaceExistingItem(TWITTER, new ItemStack(Material.AIR));
                chestMenu.replaceExistingItem(DISCORD, new ItemStack(Material.AIR));
                hidden.set(true);
            }

            return false;
        });

        // Social Media - YT
        chestMenu.addMenuClickHandler(YT, (p, i, is, c) -> {
            if (!hidden.get()) {
                final TextComponent link = new TextComponent(
                        TextUtils.translateColor("&c&lYou&f&lTube&e: &7&oClick here"));
                link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://youtube.com/ARVIN3108ID"));
                link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
                        new TextComponent(TextUtils.translateColor("&eClick here to open social media"))
                }));

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(link);
                player.sendMessage("");
            }
            return false;
        });

        // Social Media - IG
        chestMenu.addMenuClickHandler(IG, (p, i, is, c) -> {
            if (!hidden.get()) {
                final TextComponent link = new TextComponent(
                        TextUtils.translateColor("&d&lInstagram&e: &7&oClick here"));
                link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://instagram.com/arvin3108.id"));
                link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
                        new TextComponent(TextUtils.translateColor("&eClick here to open social media"))
                }));

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(link);
                player.sendMessage("");
            }
            return false;
        });

        // Social Media - Twitter
        chestMenu.addMenuClickHandler(TWITTER, (p, i, is, c) -> {
            if (!hidden.get()) {
                final TextComponent link = new TextComponent(
                        TextUtils.translateColor("&b&lTwitter&e: &7&oClick here"));
                link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/ARVIN3108_ID"));
                link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
                        new TextComponent(TextUtils.translateColor("&eClick here to open social media"))
                }));

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(link);
                player.sendMessage("");
            }
            return false;
        });

        // Social Media - Discord
        chestMenu.addMenuClickHandler(DISCORD, (p, i, is, c) -> {
            if (!hidden.get()) {
                final TextComponent link = new TextComponent(
                        TextUtils.translateColor("&9&lDiscord &6(Username Only)&e: &7&oClick here to copy"));
                link.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "arvin3108.id"));
                link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
                        new TextComponent(TextUtils.translateColor("&eClick here to copy social media id"))
                }));

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(link);
                player.sendMessage("");
            }
            return false;
        });

        chestMenu.open(player);
    }

    private ItemStack memories() {
        ItemStack item = new CustomItemStack(Material.WRITTEN_BOOK, "&dMemo&7ries");
        BookMeta meta = (BookMeta) item.getItemMeta();
        final List<String> lore = Arrays.asList(
                " ",
                ChatColor.YELLOW + "Click to get the contents of this book");
        meta.setLore(lore);
        meta.setAuthor(TextUtils.translateColor("&bARVIN&a3108"));
        item.setItemMeta(meta);
        return item;
    }
}