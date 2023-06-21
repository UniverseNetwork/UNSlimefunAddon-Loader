package id.universenetwork.sfa_loader.utils;

import id.universenetwork.infinitylib.core.AbstractAddon;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

import java.util.logging.Level;
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
public class Logger {
    private final String COLOR_PATTERN = "\u001b[38;5;%dm";
    private final String FORMAT_PATTERN = "\u001b[%dm";

    /**
     * Logs a message in the console. See {@link Bukkit#getLogger#log(Level, String, Throwable)}
     * The message will be converted in colored strings with the chosen prefix in config.yml
     *
     * @param lvl the Level of the log
     * @param msg the message
     * @param e   the exception
     * @author Timeout
     * @author ARVIN3108 ID
     */
    public void log(Level lvl, String msg, Throwable e) {
        if (lvl == Level.WARNING)
            Bukkit.getLogger().log(lvl, convertStringMessage("%p% &e" + msg), e);
        if (lvl == Level.SEVERE)
            Bukkit.getLogger().log(lvl, convertStringMessage("%p% &c" + msg), e);
        else Bukkit.getLogger().log(lvl, convertStringMessage("%p% &r" + msg), e);
    }

    /**
     * Logs a message in the console. See {@link Bukkit#getLogger#info(String)}
     * The message will be converted in colored strings with the chosen prefix in config.yml
     *
     * @param msg the message
     * @author ARVIN3108 ID
     */
    public void info(String msg) {
        Bukkit.getLogger().info(convertStringMessage("%p% &r" + msg));
    }

    /**
     * Logs a warn message in the console. See {@link Bukkit#getLogger#warning(String)}
     * The message will be converted in colored strings with the chosen prefix in config.yml
     *
     * @param msg the message
     * @author ARVIN3108 ID
     */
    public void warning(String msg) {
        Bukkit.getLogger().warning(convertStringMessage("%p% &e" + msg));
    }

    /**
     * Logs a error message in the console. See {@link Bukkit#getLogger#severe(String)}
     * The message will be converted in colored strings with the chosen prefix in config.yml
     *
     * @param msg the message
     * @author ARVIN3108 ID
     */
    public void severe(String msg) {
        Bukkit.getLogger().severe(convertStringMessage("%p% &c" + msg));
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