package id.universenetwork.sfa_loader.utils;

import id.universenetwork.libraries.infinitylib.core.AbstractAddon;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@UtilityClass
public class TextUtils {
    /**
     * Send a message to {@link CommandSender} with prefix and tranlated color codes
     */
    public void send(CommandSender Sender, String Text) {
        Sender.sendMessage(translateColor(Text));
    }

    /**
     * Send a message to {@link Player} with prefix and tranlated color codes
     */
    public void send(Player Player, String Text) {
        Player.sendMessage(translateColor(Text));
    }

    /**
     * Translate color codes to actual color using Bukkit API and replace %p% with prefix
     */
    public String translateColor(String txt) {
        txt = StringUtils.replace(txt, "%p%",
                AbstractAddon.config().getString("plugin-settings.prefix"));
        return ChatColor.translateAlternateColorCodes('&', txt);
    }

    public String convertArraysToString(String[] array) {
        String txt = StringUtils.remove(StringUtils.remove(Arrays.toString(array), "["), "]");
        if (array.length == 2) return StringUtils.replace(txt, ",", " and");
        if (array.length > 2) {
            int i = 0;
            StringBuilder builder = new StringBuilder();
            for (String str : array) {
                ++i;
                if (i == array.length) builder.append("and ").append(str);
                else builder.append(str).append(", ");
            }
            return builder.toString();
        }
        return txt;
    }
}