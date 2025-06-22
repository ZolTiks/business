package me.zoltiks.business.plugin.offlineincome;

import me.socrum.advanced.sum.Sum;
import me.socrum.advanced.util.UtilA;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.socrum.minecraft.spigot.plugin.utilm.advanced.ListenerAdvanced;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.actor.event.ActorLoadedEvent;
import me.zoltiks.business.plugin.business.ActorBusiness;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.zoltiks.business.Main.instance;

public class OfflineIncomeListener implements ListenerAdvanced {

    @EventHandler
    private void onActorLoadedEvent(ActorLoadedEvent event) {
        Actor actor = event.getActor();
        Player player = actor.getPlayer();

        long offlineTime = 0;
        long lastExitTimestamp = actor.getLastExitTimestamp();

        // -1 систамное значение говорящее о том, что игрок ранее не выходил с сервера
        if (lastExitTimestamp != -1) offlineTime = UtilA.timestamp() - lastExitTimestamp;

        double totalBusinessIncome = 0;
        List<ActorBusiness> actorBusinessList = actor.getActorBusinessList();
        for (ActorBusiness actorBusiness : actorBusinessList) totalBusinessIncome = Sum.add(totalBusinessIncome, actorBusiness.getTotalIncome());

        List<OfflineIncome> offlineIncomeList = new ArrayList<>();
        for (PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions()) {
            String permission = permissionAttachmentInfo.getPermission();
            long seconds = 0;
            double percent = 0;
            boolean success = true;
            try {
                // это "business.offline_income.seconds.N1.percent.N2"?
                String[] split = permission.split("\\.");
                seconds = Long.parseLong(split[3]);
                percent = Double.parseDouble(split[5]);
            } catch (Exception ignored) {
                success = false;
            }
            if (success) offlineIncomeList.add(new OfflineIncome(seconds, percent));
        }

        for (OfflineIncome offlineIncome : offlineIncomeList) {
            double percent = offlineIncome.getPercent();
            long maxOfflineSeconds = offlineIncome.getSeconds();
            long finalOfflineSeconds = offlineTime > maxOfflineSeconds ? maxOfflineSeconds : offlineTime;

            double incomePerOfflineSecond = totalBusinessIncome / 100 * percent;
            double totalOfflineIncome = Sum.multiply(incomePerOfflineSecond, finalOfflineSeconds);

            instance.getEconomy().depositPlayer(player, totalOfflineIncome);
            UtilM.sendMessageFromConfigurationPath(instance, player, "message.offline_income_received", new HashMap<String, String>() {{
                put("%offline_income%", UtilA.number(totalOfflineIncome));
                put("%seconds_in_offline%", UtilA.number(finalOfflineSeconds));
            }});
        }
    }

}
