package me.zoltiks.business.plugin.miningblock;

import me.socrum.advanced.ini.Initer;
import me.socrum.minecraft.spigot.plugin.utilm.BlockM;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

import static me.zoltiks.business.Main.ini;
import static me.zoltiks.business.Main.instance;

public class MiningBlockManager implements Initer {

    private final Map<String, MiningBlock> stringMiningBlockMap;

    public MiningBlockManager() {
        this.stringMiningBlockMap = new HashMap<>();

        FileConfiguration fileConfiguration = instance.getConfig();
        ConfigurationSection miningBlockListConfigurationSection = fileConfiguration.getConfigurationSection("mining_block_list");
        Set<String> miningBlockIdSet = miningBlockListConfigurationSection.getKeys(false);
        miningBlockIdSet.forEach(miningBlockId -> {
            ConfigurationSection miningBlockConfigurationSection = miningBlockListConfigurationSection.getConfigurationSection(miningBlockId);

            // расчитать на какой блок должен быть заменен добываемый в случае его добычи
            BlockM replaceBlockM = new BlockM(Material.AIR, false, (byte) 0);
            ConfigurationSection replaceBlockConfigurationSection = miningBlockConfigurationSection.getConfigurationSection("replace_block");
            if (replaceBlockConfigurationSection != null) replaceBlockM = UtilM.getBlockMFromConfigurationPath(instance, miningBlockConfigurationSection.getConfigurationSection("replace_block").getCurrentPath());

            // список регионов WorldGuard в которых этот блок может быть сломан
            List<String> canOnlyBeBrokeInWorldGuardRegionList = miningBlockConfigurationSection.getStringList("can_only_be_broke_in_worldguard_region_list");
            if (canOnlyBeBrokeInWorldGuardRegionList == null) canOnlyBeBrokeInWorldGuardRegionList = new ArrayList<>();

            MiningBlock miningBlock = new MiningBlock(
                    miningBlockId,
                    UtilM.getBlockMFromConfigurationPath(instance, miningBlockConfigurationSection.getConfigurationSection("block").getCurrentPath()),
                    replaceBlockM,
                    miningBlockConfigurationSection.getInt("respawn_delay"),
                    miningBlockConfigurationSection.getString("income_formula"),
                    canOnlyBeBrokeInWorldGuardRegionList
            );
            this.stringMiningBlockMap.put(miningBlockId, miningBlock);
        });

        UtilM.registerClassAsListener(instance, ini.put(new MiningBlockListener()));
        ini.put(new MiningBlockScheduler());
    }

    public Map<String, MiningBlock> getStringMiningBlockMap() {
        return stringMiningBlockMap;
    }

}
