package id.universenetwork.sfa_loader.utils;

import id.universenetwork.sfa_loader.libraries.guizhanlib.slimefun.addon.AbstractAddon;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@UtilityClass
public class TextUtils {
    /**
     * Send multiple messages to {@link CommandSender} with prefix and translated color codes
     */
    public void send(CommandSender sender, String... txt) {
        for (String s : txt) sender.sendMessage(translateColor(s));
    }

    /**
     * Send multiple messages to {@link Player} with prefix and translated color codes
     */
    public void send(Player player, String... txt) {
        for (String s : txt) player.sendMessage(translateColor(s));
    }

    /**
     * Send multiple center-aligned messages to {@link CommandSender} with a translated prefix and color codes
     */
    public void sendCentered(CommandSender sender, String... txt) {
        for (String s : txt) {
            if (s == null || s.isEmpty()) {
                sender.sendMessage("");
                continue;
            }
            s = translateColor(s);
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;
            for (char c : s.toCharArray())
                if (c == '§') previousCode = true;
                else if (previousCode) {
                    previousCode = false;
                    isBold = c == 'l' || c == 'L';
                } else {
                    Font FI = Font.getFontInfo(c);
                    messagePxSize += isBold ? FI.getBoldLength() : FI.getLength();
                    messagePxSize++;
                }
            int CENTER_PX = 154;
            int halvedMessageSize = messagePxSize / 2;
            int toCompensate = CENTER_PX - halvedMessageSize;
            int spaceLength = Font.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while (compensated < toCompensate) {
                sb.append(" ");
                compensated += spaceLength;
            }
            sender.sendMessage(sb + s);
        }
    }

    /**
     * Send multiple center-aligned message to {@link Player} with prefix and translated color codes
     */
    public void sendCentered(Player player, String... txt) {
        for (String s : txt) {
            if (s == null || s.isEmpty()) {
                player.sendMessage("");
                continue;
            }
            s = translateColor(s);
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;
            for (char c : s.toCharArray())
                if (c == '§') previousCode = true;
                else if (previousCode) {
                    previousCode = false;
                    isBold = c == 'l' || c == 'L';
                } else {
                    Font FI = Font.getFontInfo(c);
                    messagePxSize += isBold ? FI.getBoldLength() : FI.getLength();
                    messagePxSize++;
                }
            int CENTER_PX = 154;
            int halvedMessageSize = messagePxSize / 2;
            int toCompensate = CENTER_PX - halvedMessageSize;
            int spaceLength = Font.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while (compensated < toCompensate) {
                sb.append(" ");
                compensated += spaceLength;
            }
            player.sendMessage(sb + s);
        }
    }

    /**
     * Translate color codes to actual color using Bukkit API and replace %p% with prefix
     */
    @SuppressWarnings("deprecation")
    public String translateColor(String txt) {
        txt = StringUtils.replace(txt, "%p%",
                AbstractAddon.getAddonConfig().getString("plugin-settings.prefix"));
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

    enum Font {
        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);
        final char character;
        final int length;

        Font(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public static Font getFontInfo(char c) {
            for (Font FI : Font.values()) if (FI.getCharacter() == c) return FI;
            return Font.DEFAULT;
        }

        public char getCharacter() {
            return character;
        }

        public int getLength() {
            return length;
        }

        public int getBoldLength() {
            if (this == Font.SPACE) return getLength();
            return length + 1;
        }
    }
}