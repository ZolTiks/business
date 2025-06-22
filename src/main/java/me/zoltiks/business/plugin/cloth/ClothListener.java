package me.zoltiks.business.plugin.cloth;

import me.socrum.minecraft.spigot.plugin.utilm.advanced.ListenerAdvanced;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.business.ActorBusiness;
import me.zoltiks.business.plugin.business.event.BusinessIncomeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import static me.zoltiks.business.Main.instance;

public class ClothListener implements ListenerAdvanced {

    /**
     * Зачисляет игроку дополнительный доход в виде % от финального
     * дохода бизнеса в момент начисления им зарплаты за счет суммы
     * всех процентов дополнительного дохода всей одетой одежды на игроке.
     */
    @EventHandler
    private void onBusinessIncomeEvent(BusinessIncomeEvent event) {
        ActorBusiness actorBusiness = event.getActorBusiness();
        Actor actor = actorBusiness.getActor();
        Player player = actor.getPlayer();

        double totalAdditionIncomePercentForTotalBusinessIncome = ClothUtil.getTotalAdditionIncomePercentForTotalBusinessIncome(player);
        double additionIncomeFromTotalBusinessIncome = actorBusiness.getTotalIncome() / 100 * totalAdditionIncomePercentForTotalBusinessIncome;
        if (additionIncomeFromTotalBusinessIncome == 0) return;

        instance.getEconomy().depositPlayer(player, additionIncomeFromTotalBusinessIncome);
    }

}
