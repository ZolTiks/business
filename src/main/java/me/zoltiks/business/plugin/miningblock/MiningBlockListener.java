package me.zoltiks.business.plugin.miningblock;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.socrum.advanced.util.UtilA;
import me.socrum.minecraft.spigot.plugin.utilm.BlockM;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.socrum.minecraft.spigot.plugin.utilm.advanced.ListenerAdvanced;
import me.zoltiks.business.plugin.actor.Actor;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Map;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class MiningBlockListener implements ListenerAdvanced {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        MiningBlockManager miningBlockManager = ini.get(MiningBlockManager.class);
        Map<String, MiningBlock> stringMiningBlockMap = miningBlockManager.getStringMiningBlockMap();
        for (Map.Entry<String, MiningBlock> stringMiningBlockEntry : stringMiningBlockMap.entrySet()) {
            String miningBlockId = stringMiningBlockEntry.getKey();
            MiningBlock miningBlock = stringMiningBlockEntry.getValue();

            BlockM blockM = miningBlock.getBlockM();
            if (!blockM.isSimilar(block)) continue;

            boolean blockCanBeBroken = true;
            List<String> canOnlyBeBrokeInWorldGuardRegionList = miningBlock.getCanOnlyBeBrokeInWorldGuardRegionList();
            if (!canOnlyBeBrokeInWorldGuardRegionList.isEmpty()) {
                // этот тип блока можно ломать только в определенных регионах
                blockCanBeBroken = false;

                RegionContainer regionContainer = WorldGuardPlugin.inst().getRegionContainer();
                RegionManager regionManager = regionContainer.get(blockLocation.getWorld());
                if (regionManager != null) {
                    ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(blockLocation);
                    for (ProtectedRegion protectedRegion : applicableRegions.getRegions()) {
                        String id = protectedRegion.getId();

                        // в этом регионе можно ломать этот тип блока?
                        if (canOnlyBeBrokeInWorldGuardRegionList.contains(id)) {
                            blockCanBeBroken = true;
                            break;
                        }
                    }
                }
            }

            // этот тип блока нельзя ломать в этом месте без специального региона
            if (!blockCanBeBroken) {
                return;
            }

            // переопределить возможную отмену этого события другим плагином
            event.setCancelled(false);

            // отключить выпадение предметов за ненадобностью
            event.setDropItems(false);
            event.setExpToDrop(0);

            // расчитать доход за сломанный блок согласно формулы
            String incomeFormula = miningBlock.getIncomeFormula();
            incomeFormula = UtilM.papi(player, incomeFormula);
            incomeFormula = Util.decodeKKString(incomeFormula.replace(",", ""), Util.formatterPrefixes);
            double income = UtilA.eval(incomeFormula);

            // зачислить доход
            instance.getEconomy().depositPlayer(player, income);

            // добавить добываемый блок в список на восставновление
            MiningBlockScheduler miningBlockScheduler = ini.get(MiningBlockScheduler.class);
            miningBlockScheduler.getBrokenMiningBlockList().add(new BrokenMiningBlock(block, miningBlock, player, income));
        }
    }

}
