package me.zoltiks.business.plugin.command;

import me.socrum.minecraft.spigot.plugin.utilm.advanced.CommandExecutorAdvanced;
import me.zoltiks.business.Main;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.business.ActorBusiness;
import me.zoltiks.business.plugin.business.Business;
import me.zoltiks.business.plugin.business.BusinessManager;
import me.zoltiks.business.plugin.gui.GuiManager;
import me.zoltiks.business.plugin.gui.realization.BusinessUpConfirm;
import me.zoltiks.business.plugin.gui.realization.MyBusinessList;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.ini;

public class Command implements CommandExecutorAdvanced {

    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {

        String multiplierString;
        String message;
        String playerName;
        Player player;
        FileConfiguration fileConfiguration = Main.instance.getConfig();
        if (strings.length == 1 && strings[0].equals("reload")) {
            if (!commandSender.hasPermission(fileConfiguration.getString("permission.reload"))) {
                String message2 = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("message.reload_denied"));
                if (!message2.isEmpty()) {
                    commandSender.sendMessage(message2);
                }
                return true;
            }
            Main.instance.refresh();
            String message3 = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("message.reload"));
            if (!message3.isEmpty()) {
                commandSender.sendMessage(message3);
            }
            return true;
        }
        if (strings.length == 1 && strings[0].equalsIgnoreCase("help")) {
            List stringList = Main.instance.getConfig().getStringList("message.help");
            for (Object string : stringList) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', (String) string));
            }
            return true;
        }
        if (strings.length == 0 && commandSender instanceof Player) {
            player = (Player) commandSender;
            GuiManager guiManager = ini.get(GuiManager.class);
            guiManager.open(player, new MyBusinessList(player, ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("gui.title")), Main.instance.getConfig().getInt("gui.size")));
        }
        if (strings.length == 2 && strings[1].equalsIgnoreCase("reset")) {
            if (!commandSender.isOp()) {
                return true;
            }
            playerName = strings[0];
            Player otherPlayer = Bukkit.getPlayerExact(playerName);
            if (otherPlayer == null) {
                String message4 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.player_offline"));
                if (!message4.isEmpty()) {
                    commandSender.sendMessage(message4);
                }
                return true;
            }
            Actor otherActor = Util.requiredNullableActor(otherPlayer);
            otherActor.setActorBusinessList(new ArrayList<>());
            String message5 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.reset"));
            if (!message5.isEmpty()) {
                commandSender.sendMessage(message5);
            }
            return true;
        }
        if (strings.length == 2 && strings[1].equalsIgnoreCase("up") && commandSender instanceof Player) {
            player = (Player) commandSender;
            Actor actor = Util.requiredNullableActor(player);
            if (actor == null) {
                return true;
            }
            String businessIdString = strings[0];
            int businessId = 0;
            try {
                businessId = Integer.parseInt(businessIdString);
            } catch (NumberFormatException e) {
                String message6 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.business_not_exist"));
                if (!message6.isEmpty()) {
                    player.sendMessage(message6);
                }
                return true;
            }
            BusinessManager businessManager = ini.get(BusinessManager.class);
            Map<Integer, Business> integerBusinessMap = businessManager.getIntegerBusinessMap();
            Business business = integerBusinessMap.get(businessId);
            if (business == null) {
                String message7 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.business_not_exist"));
                if (!message7.isEmpty()) {
                    player.sendMessage(message7);
                }
                return true;
            }
            List<ActorBusiness> actorBusinessList = actor.getActorBusinessList();
            ActorBusiness actorBusiness = null;
            for (ActorBusiness actorBusiness1 : actorBusinessList) {
                if (!actorBusiness1.getBusiness().equals(business)) continue;
                actorBusiness = actorBusiness1;
                break;
            }
            String title = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("confirm_gui.title"));
            int size = Main.instance.getConfig().getInt("confirm_gui.size");
            GuiManager guiManager = ini.get(GuiManager.class);
            guiManager.open(player, new BusinessUpConfirm(player, title, size, business, actorBusiness));
            return true;
        }
        if (strings.length == 3 && strings[1].equalsIgnoreCase("multiplier")) {
            if (!commandSender.hasPermission(fileConfiguration.getString("permission.multiplier_set"))) {
                message = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("message.multiplier_set_denied"));
                if (!message.isEmpty()) {
                    commandSender.sendMessage(message);
                }
                return true;
            }
            playerName = strings[0];
            multiplierString = strings[2];
            Player otherPlayer = Bukkit.getPlayerExact(playerName);
            if (otherPlayer == null) {
                String message8 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.player_offline"));
                if (!message8.isEmpty()) {
                    commandSender.sendMessage(message8);
                }
                return true;
            }
            double multiplier = 0.0;
            try {
                multiplier = Double.parseDouble(multiplierString);
            } catch (NumberFormatException e) {
                String message9 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.multiplier_not_number"));
                if (!message9.isEmpty()) {
                    commandSender.sendMessage(message9);
                }
                return true;
            }
            Actor otherActor = Util.requiredNullableActor(otherPlayer);
            if (otherActor == null) {
                return true;
            }
            otherActor.setBusinessIncomeMultiplier(multiplier);
            String message10 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.multiplier_set"));
            if (!message10.isEmpty()) {
                commandSender.sendMessage(message10);
            }
            return true;
        }
        if (strings.length == 4 && strings[1].equalsIgnoreCase("multiplier") && strings[2].equalsIgnoreCase("add")) {
            if (!commandSender.hasPermission(fileConfiguration.getString("permission.multiplier_set"))) {
                message = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("message.multiplier_set_denied"));
                if (!message.isEmpty()) {
                    commandSender.sendMessage(message);
                }
                return true;
            }
            playerName = strings[0];
            multiplierString = strings[3];
            Player otherPlayer = Bukkit.getPlayerExact(playerName);
            if (otherPlayer == null) {
                String message11 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.player_offline"));
                if (!message11.isEmpty()) {
                    commandSender.sendMessage(message11);
                }
                return true;
            }
            double multiplier = 0.0;
            try {
                multiplier = Double.parseDouble(multiplierString);
            } catch (NumberFormatException e) {
                String message12 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.multiplier_not_number"));
                if (!message12.isEmpty()) {
                    commandSender.sendMessage(message12);
                }
                return true;
            }
            Actor otherActor = Util.requiredNullableActor(otherPlayer);
            if (otherActor == null) {
                return true;
            }
            otherActor.setBusinessIncomeMultiplier(otherActor.getBusinessIncomeMultiplier() + multiplier);
            String message13 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.multiplier_set"));
            if (!message13.isEmpty()) {
                commandSender.sendMessage(message13);
            }
            return true;
        }
        if (strings.length == 4 && strings[1].equalsIgnoreCase("multiplier") && strings[2].equalsIgnoreCase("del")) {
            if (!commandSender.hasPermission(fileConfiguration.getString("permission.multiplier_set"))) {
                message = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("message.multiplier_set_denied"));
                if (!message.isEmpty()) {
                    commandSender.sendMessage(message);
                }
                return true;
            }
            playerName = strings[0];
            multiplierString = strings[3];
            Player otherPlayer = Bukkit.getPlayerExact(playerName);
            if (otherPlayer == null) {
                String message14 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.player_offline"));
                if (!message14.isEmpty()) {
                    commandSender.sendMessage(message14);
                }
                return true;
            }
            double multiplier = 0.0;
            try {
                multiplier = Double.parseDouble(multiplierString);
            } catch (NumberFormatException e) {
                String message15 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.multiplier_not_number"));
                if (!message15.isEmpty()) {
                    commandSender.sendMessage(message15);
                }
                return true;
            }
            Actor otherActor = Util.requiredNullableActor(otherPlayer);
            if (otherActor == null) {
                return true;
            }
            otherActor.setBusinessIncomeMultiplier(otherActor.getBusinessIncomeMultiplier() - multiplier);
            String message16 = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("message.multiplier_set"));
            if (!message16.isEmpty()) {
                commandSender.sendMessage(message16);
            }
            return true;
        }

        return true;
    }

}

