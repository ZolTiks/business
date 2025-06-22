package me.zoltiks.business.plugin.util;

import me.zoltiks.business.Main;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.actor.ActorManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.zoltiks.business.Main.ini;

public class Util {
    public static Map<Double, String> formatterPrefixes = new LinkedHashMap<Double, String>();

    public static Actor requiredNullableActor(Player player) {
        ActorManager actorManager = ini.get(ActorManager.class);
        Map<Player, Actor> playerActorHashMap = actorManager.getPlayerActorMap();
        return playerActorHashMap.get(player);
    }

    public static ItemStack parseConfigItemStack(String path) {
        FileConfiguration config = Main.instance.getConfig();
        String cancelItemMaterial = config.getString(path + ".material");
        byte data = 0;
        if (cancelItemMaterial.contains(":")) {
            data = Byte.parseByte(cancelItemMaterial.split(":")[1]);
            cancelItemMaterial = cancelItemMaterial.split(":")[0];
        }
        ItemStack is = new ItemStack(Material.valueOf(cancelItemMaterial), 1, (byte) 0, Byte.valueOf(data));
        if (config.getInt(path + ".durability") > 0) {
            is.setDurability((short) config.getInt(path + ".durability"));
        }

        ItemMeta im = is.getItemMeta();
        im.setDisplayName(config.getString(path + ".name").replace("&", "\u00a7"));
        ArrayList<String> lore = new ArrayList<String>();
        for (String str : config.getStringList(path + ".lore")) {
            lore.add(str.replace("&", "\u00a7"));
        }
        im.setLore(lore);
        im.setUnbreakable(true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS);
        is.setItemMeta(im);
        return is;
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1;
            int ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected character: '" + (char)ch + "' in expression: " + str);
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.' || ch == 'E' || ch == 'e') {
                    while ((ch >= '0' && ch <= '9') || ch == '.' || ch == 'E' || ch == 'e' || ch == '+' || ch == '-') {
                        nextChar();
                    }
                    String numStr = str.substring(startPos, this.pos);
                    try {
                        x = Double.parseDouble(numStr);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Invalid number format: " + numStr);
                    }
                } else {
                    throw new RuntimeException("Unexpected character: '" + (char)ch + "'");
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    public static String number(Number number, int round) {
        if (number == null) {
            return null;
        }
        String string1 = "";
        String string2 = "";
        if (number instanceof Integer) {
            string1 = String.valueOf(number);
        } else {
            char c;
            String[] strings = String.format("%." + round + "f", number).replace(",", ".").split("\\.");
            String fractional = strings.length > 1 ? strings[1] : "0";
            fractional = fractional.substring(0, Math.min(fractional.length(), round));
            String result = "";
            for (int j = fractional.length(); j > 0 && (c = fractional.charAt(j - 1)) == '0'; --j) {
                result = fractional.substring(0, j - 1);
            }
            if (result.isEmpty() && !fractional.endsWith("0")) {
                result = fractional;
            }
            fractional = result;
            string1 = strings[0];
            string2 = (fractional.isEmpty() ? "" : ".") + fractional;
        }
        try {
            char sign = string1.charAt(0);
            string1 = string1.replace("-", "").replace("+", string1);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < string1.length(); ++i) {
                if (i != 0 && i % 3 == 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(string1.charAt(string1.length() - i - 1));
            }
            String result = (sign == '-' || sign == '+' ? Character.valueOf(sign) : "") + new StringBuilder(stringBuilder.toString()).reverse().toString() + string2;
            return result.replace(".", Main.instance.getConfig().getString("separator"));
        } catch (Exception e) {
            return string1.replace(".", Main.instance.getConfig().getString("separator"));
        }
    }

    public static String format(double value) {
        for (Map.Entry<Double, String> doubleStringEntry : formatterPrefixes.entrySet()) {
            double number = doubleStringEntry.getKey();
            String suffix = doubleStringEntry.getValue();
            if (!(value >= number)) continue;
            return Util.number(value / number, 2) + suffix;
        }
        return Util.number(value, 2);
    }

    public static void sendMessageFromConfigurationPath(CommandSender commandSender, String path, boolean isList) {
        Util.sendMessageFromConfigurationPath(commandSender, path, isList, new HashMap<String, String>());
    }

    public static void sendMessageFromConfigurationPath(CommandSender commandSender, String path, boolean isList, Map<String, String> placeholders) {
        if (isList) {
            List stringList = Main.instance.getConfig().getStringList(path);
            for (Object message : stringList) {
                for (Map.Entry<String, String> stringStringEntry : placeholders.entrySet()) {
                    message = ((String) message).replace(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', (String) message));
            }
        } else {
            String message = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString(path));
            for (Map.Entry<String, String> stringStringEntry : placeholders.entrySet()) {
                message = message.replace(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            if (!message.isEmpty()) {
                commandSender.sendMessage(message);
            }
        }
    }

    public static int guiXYSlot(int x, int y) {
        return x - 1 + (y - 1) * 9;
    }

    static {
        FileConfiguration fileConfiguration = Main.instance.getConfig();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("formatter");
        Set keySet = configurationSection.getKeys(false);
        for (Object key : keySet) {
            double keyValue = Double.parseDouble((String) key);
            String string = configurationSection.getString((String) key);
            formatterPrefixes.put(keyValue, string);
        }
    }

    public static String decodeKKString(String input, Map<Double, String> values) {
        if (input == null || values == null) {
            return input;
        }

        // Обрабатываем только числа с суффиксами (K, M, B и т.д.)
        for (Map.Entry<Double, String> entry : values.entrySet()) {
            String suffix = entry.getValue();
            Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)" + Pattern.quote(suffix));
            Matcher matcher = pattern.matcher(input);

            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String numberStr = matcher.group(1);
                double number = Double.parseDouble(numberStr);
                double result = number * entry.getKey();

                // Сохраняем как целое число если нет дробной части
                String replacement = (result % 1 == 0) ?
                        String.valueOf((long) result) :
                        String.valueOf(result);

                matcher.appendReplacement(sb, replacement);
            }
            matcher.appendTail(sb);
            input = sb.toString();
        }

        return input;
    }


}

