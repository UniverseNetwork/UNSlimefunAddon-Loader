package id.universenetwork.sfa_loader.utils;

import id.universenetwork.sfa_loader.libraries.infinitylib.core.AbstractAddon;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extends the normal Bukkit Logger to write Colors
 *
 * @author Timeout
 * @author ARVIN3108 ID
 * @see <a href="https://www.spigotmc.org/threads/colors-in-console.87576">Original code/threads</a>
 */
@UtilityClass
@SuppressWarnings("deprecation")
public class LogUtils {
    private final String COLOR_PATTERN = "\u001b[38;5;%dm";
    private final String FORMAT_PATTERN = "\u001b[%dm";

    /**
     * Logs a message in the console.
     * The message will be converted in colored strings with the chosen prefix in config.yml
     *
     * @param lvl the Level of the log
     * @param msg the message
     * @param e   the exception
     * @author Timeout
     * @author ARVIN3108 ID
     * @see Logger#log(Level, String, Throwable)
     */
    public void log(Level lvl, String msg, Throwable e) {
        if (lvl.equals(Level.WARNING))
            Bukkit.getLogger().log(lvl, checkAndConvert("%p% &e" + msg), e);
        if (lvl.equals(Level.SEVERE))
            Bukkit.getLogger().log(lvl, checkAndConvert("%p% &c" + msg), e);
        else Bukkit.getLogger().log(lvl, checkAndConvert("%p% &r" + msg), e);
    }

    /**
     * Logs a message in the console.
     * The message will be converted in colored strings with the chosen prefix in config.yml
     *
     * @param msg the message
     * @author ARVIN3108 ID
     * @see Logger#info(String)
     */
    public void info(String msg) {
        TextUtils.send(Bukkit.getConsoleSender(), "%p% &r" + msg);
    }

    /**
     * Logs a warn message in the console.
     * The message will be converted in colored strings with the chosen prefix in config.yml
     *
     * @param msg the message
     * @author ARVIN3108 ID
     * @see Logger#warning(String)
     */
    public void warning(String msg) {
        Bukkit.getLogger().warning(checkAndConvert("%p% &e" + msg));
    }

    /**
     * Logs an error message in the console.
     * The message will be converted in colored strings with the chosen prefix in config.yml
     *
     * @param msg the message
     * @author ARVIN3108 ID
     * @see Logger#severe(String)
     */
    public void severe(String msg) {
        Bukkit.getLogger().severe(checkAndConvert("%p% &c" + msg));
    }

    /**
     * Check if we can convert with ChatColor or not
     *
     * @param msg the message. Can be null
     * @return the converted message from
     * {@link ChatColor#translateAlternateColorCodes(char, String)} or
     * {@link #convertStringMessage(String)}
     */
    private String checkAndConvert(String msg) {
        if (JavaUtils.isClassDeprecated(ChatColor.class)) return convertStringMessage(msg);
        return TextUtils.translateColor(msg);
    }

    /**
     * Converts a String with Minecraft-ColorCodes into Ansi-Colors.
     * Returns null if the message is null
     *
     * @param msg the message. Can be null
     * @return the converted message or null if the message is null
     * @author Timeout
     * @author ARVIN3108 ID
     */
    private String convertStringMessage(String msg) {
        // Continue if String is neither not null nor empty
        if (msg != null && !msg.isEmpty()) {
            // replace %p% with prefix
            msg = StringUtils.replace(msg, "%p%", AbstractAddon.config()
                    .getString("plugin-settings.prefix"));
            // copy of string
            String msgCopy = String.copyValueOf(msg.toCharArray()) + ConsoleColor.RESET.ansiColor;
            // create Matcher to search for color codes
            Matcher matcher = Pattern.compile(String.format("(%c[0-9a-fk-or])(?!.*\1)", '&')).matcher(msg);
            // run through result
            while (matcher.find()) {
                // get Result
                String result = matcher.group(1);
                // get ColorCode
                ConsoleColor color = ConsoleColor.getColorByCode(result.charAt(1));
                // replace color
                msgCopy = msgCopy.replace(result, color.getAnsiColor());
            }
            // return converted String
            return msgCopy;
        }
        // return message for nothing to compile
        return msg;
    }

    /**
     * Represents a set of Minecraft-ColorCodes and their ANSI-Codes
     *
     * @author Timeout
     */
    private enum ConsoleColor {

        BLACK('0', COLOR_PATTERN, 0),
        DARK_GREEN('2', COLOR_PATTERN, 2),
        DARK_RED('4', COLOR_PATTERN, 1),
        GOLD('6', COLOR_PATTERN, 172),
        DARK_GREY('8', COLOR_PATTERN, 8),
        GREEN('a', COLOR_PATTERN, 10),
        RED('c', COLOR_PATTERN, 9),
        YELLOW('e', COLOR_PATTERN, 11),
        DARK_BLUE('1', COLOR_PATTERN, 4),
        DARK_AQUA('3', COLOR_PATTERN, 30),
        DARK_PURPLE('5', COLOR_PATTERN, 54),
        GRAY('7', COLOR_PATTERN, 246),
        BLUE('9', COLOR_PATTERN, 4),
        AQUA('b', COLOR_PATTERN, 51),
        LIGHT_PURPLE('d', COLOR_PATTERN, 13),
        WHITE('f', COLOR_PATTERN, 15),
        STRIKETHROUGH('m', FORMAT_PATTERN, 9),
        ITALIC('o', FORMAT_PATTERN, 3),
        BOLD('l', FORMAT_PATTERN, 1),
        UNDERLINE('n', FORMAT_PATTERN, 4),
        RESET('r', FORMAT_PATTERN, 0);


        private final char bukkitColor;
        private final String ansiColor;

        ConsoleColor(char bukkitColor, String pattern, int ansiCode) {
            this.bukkitColor = bukkitColor;
            this.ansiColor = String.format(pattern, ansiCode);
        }

        /**
         * Searches if the code is a valid color code and returns the right enum
         *
         * @param code the Minecraft-ColorCode without Formatter-Char
         * @return the Color enum or null if no enum can be found
         * @author Timeout
         */
        public static ConsoleColor getColorByCode(char code) {
            // run through colors
            for (ConsoleColor color : values()) {
                // check code
                if (color.bukkitColor == code) return color;
            }
            // return null for not found
            throw new IllegalArgumentException("Color with code " + code + " does not exists");
        }

        /**
         * Returns the ANSI-ColorCode of the color code
         *
         * @return the Ansi-ColorCode
         * @author Timeout
         */
        public String getAnsiColor() {
            return ansiColor;
        }
    }
}