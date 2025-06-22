package me.zoltiks.business.plugin;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.socrum.advanced.ini.Initer;
import me.socrum.advanced.sum.Sum;
import me.socrum.advanced.util.UtilA;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.booster.Booster;
import me.zoltiks.business.plugin.business.ActorBusiness;
import me.zoltiks.business.plugin.business.Business;
import me.zoltiks.business.plugin.business.BusinessLevel;
import me.zoltiks.business.plugin.business.BusinessManager;
import me.zoltiks.business.plugin.cloth.ClothUtil;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class PAPI extends PlaceholderExpansion implements Configurable, Initer {

    @Override
    public void init() {
        PlaceholderAPI.registerExpansion(this);
    }

    @Override
    public void destroy() {
        PlaceholderAPI.unregisterExpansion(this);
    }

    public String getIdentifier() {
        return "business";
    }

    public String getAuthor() {
        return "zoltiks";
    }

    public String getVersion() {
        return "0.0.1";
    }

    public Map<String, Object> getDefaults() {
        return null;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String string) {
        if (offlinePlayer == null) {
            return "";
        }
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return "";
        }
        Actor actor = Util.requiredNullableActor(player);
        if (actor == null) {
            return "";
        }
        double businessIncomeMultiplier = actor.getBusinessIncomeMultiplier();
        int businessTotalOwned = actor.getActorBusinessList().size();
        double businessTotalIncome = 0.0;
        for (ActorBusiness actorBusiness : actor.getActorBusinessList()) {
            businessTotalIncome += actorBusiness.getTotalIncome();
        }

        switch (string) {
            case "business_income_multiplier": {
                return Util.number(businessIncomeMultiplier, 2);
            }
            case "business_income_multiplier_percent": {
                return Util.format(businessIncomeMultiplier * 100.0) + "%";
            }
            case "business_total_owned": {
                return String.valueOf(businessTotalOwned);
            }
            case "business_total_income": {
                return Util.format(businessTotalIncome);
            }
            case "balance": {
                return Util.format(instance.getEconomy().getBalance(actor.getPlayer()));
            }
        }

        // бустеры
        // booster_duration_N
        if (string.startsWith("booster_duration_")) {
            try {
                int boosterIndex = Integer.parseInt(string.replace("booster_duration_", ""));
                Booster booster = actor.getBoosterList().get(boosterIndex - 1);
                return UtilA.shortTimeString(booster.getDurationInSeconds());
            } catch (Exception e) {
                return "0 сек.";
            }
        }

        // booster_multiplier_N
        if (string.startsWith("booster_multiplier_")) {
            try {
                int boosterIndex = Integer.parseInt(string.replace("booster_multiplier_", ""));
                Booster booster = actor.getBoosterList().get(boosterIndex - 1);
                return UtilA.number(booster.getBusinessIncomeMultiplier());
            } catch (Exception e) {
                return "1";
            }
        }

        // booster_total_multiplier
        if (string.equalsIgnoreCase("booster_total_multiplier")) {
            try {
                // применить множители активных бустеров игрока
                double totalBoosterMultiplier = 1;
                List<Booster> boosterList = actor.getBoosterList();
                for (Booster booster : boosterList) {
                    double multiplier = booster.getBusinessIncomeMultiplier();
                    if (multiplier > 1) multiplier = Sum.subtract(multiplier, 1);
                    totalBoosterMultiplier = Sum.add(totalBoosterMultiplier, multiplier);
                }
                return UtilA.number(totalBoosterMultiplier);
            } catch (Exception e) {
                return "1";
            }
        }

        // example: business_player_business_level_%id%
        if (string.startsWith("player_business_level_")) {
            int businessId = Integer.parseInt(string.replace("player_business_level_", ""));
            for (ActorBusiness actorBusiness : actor.getActorBusinessList()) {
                if (actorBusiness == null) continue;
                if (businessId != actorBusiness.getBusiness().getId()) continue;
                return String.valueOf(actorBusiness.getLevel());
            }
            return "0";
        }

        // example: business_business_buy_price_%id%
        if (string.startsWith("business_buy_price_")) {
            int businessId = Integer.parseInt(string.replace("business_buy_price_", ""));
            Map<Integer, Business> integerBusinessMap = ini.get(BusinessManager.class).getIntegerBusinessMap();
            Business business = integerBusinessMap.get(businessId);
            if (business == null) return "0";
            List<BusinessLevel> businessLevelList = business.getBusinessLevelList();
            if (businessLevelList == null || businessLevelList.isEmpty()) return "0";
            BusinessLevel businessLevel = businessLevelList.get(0);
            return Util.format(businessLevel.getPrice());
        }

        // example: business_business_total_income_%id%
        if (string.startsWith("player_business_total_income_")) {
            int businessId = Integer.parseInt(string.replace("player_business_total_income_", ""));
            for (ActorBusiness actorBusiness : actor.getActorBusinessList()) {
                if (actorBusiness == null) continue;
                if (businessId != actorBusiness.getBusiness().getId()) continue;
                return Util.format(actorBusiness.getTotalIncome());
            }
            return "0";
        }

        // example: business_player_business_upgrade_price_%id%
        if (string.startsWith("player_business_upgrade_price_")) {
            int businessId = Integer.parseInt(string.replace("player_business_upgrade_price_", ""));
            for (ActorBusiness actorBusiness : actor.getActorBusinessList()) {
                if (actorBusiness == null) continue;
                if (businessId != actorBusiness.getBusiness().getId()) continue;

                Business business = actorBusiness.getBusiness();
                List<BusinessLevel> businessLevelList = business.getBusinessLevelList();
                BusinessLevel businessLevel = null;
                try {
                    businessLevel = businessLevelList.get(actorBusiness.getLevel());
                } catch (Exception ignored) {

                }
                if (businessLevel != null) return Util.format(businessLevel.getPrice());
                return "0";
            }
            return "0";
        }

        // example: business_business_display_name_%id%
        if (string.startsWith("business_display_name_")) {
            int businessId = Integer.parseInt(string.replace("business_display_name_", ""));
            Business business = ini.get(BusinessManager.class).getIntegerBusinessMap().get(businessId);
            return business == null ? "" : business.getItemStackVisualization().getItemMeta().getDisplayName();
        }

        // example: business_business_max_level_%id%
        if (string.startsWith("business_max_level_")) {
            int businessId = Integer.parseInt(string.replace("business_max_level_", ""));
            Business business = ini.get(BusinessManager.class).getIntegerBusinessMap().get(businessId);
            return business == null ? "0" : String.valueOf(business.getBusinessLevelList().size());
        }

        // --- одежда

        // %business_total_cloth_business_income_multiplier%
        if (string.equalsIgnoreCase("total_cloth_business_income_multiplier")) {
            return Util.format(ClothUtil.getTotalClothBusinessIncomeMultiplier(player));
        }

        // %business_total_addition_income_percent_for_total_business_income%
        if (string.equalsIgnoreCase("total_addition_income_percent_for_total_business_income")) {
            return Util.format(ClothUtil.getTotalAdditionIncomePercentForTotalBusinessIncome(player));
        }

        return "";
    }

    public boolean register() {
        return super.register();
    }
}

