package me.zoltiks.business.plugin.miningblock;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.socrum.advanced.ini.Initer;
import me.socrum.advanced.util.UtilA;
import me.socrum.minecraft.spigot.plugin.utilm.BlockM;
import me.socrum.minecraft.spigot.plugin.utilm.Updatable;
import me.socrum.minecraft.spigot.plugin.utilm.UtilM;
import me.zoltiks.business.plugin.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.zoltiks.business.Main.instance;

public class BrokenMiningBlock implements Initer, Updatable {

    private Block block;
    private BlockM regenerativeOriginalBrokenBlock;
    private MiningBlock miningBlock;
    private Player whoBroke;
    private double income;
    private int remainingTimeToRespawn;

    // голограмма
    private Hologram hologram;
    private int remainingTimeToHologramDespawn;

    public BrokenMiningBlock(Block block, MiningBlock miningBlock, Player whoBroke, double income) {
        this.block = block;
        this.regenerativeOriginalBrokenBlock = new BlockM(this.block.getType(), true, this.getBlock().getData());
        this.miningBlock = miningBlock;
        this.whoBroke = whoBroke;
        this.income = income;
        this.remainingTimeToRespawn = this.miningBlock.getRepsawnDelay();
        if (this.remainingTimeToRespawn < 1) this.remainingTimeToRespawn = 1;

        // заменить сломанный блок на заменяемый, в любом случае он может быть воздухом
        new BukkitRunnable() {
            @Override
            public void run() {
                BlockM replaceBlockM = miningBlock.getReplaceBlockM();
                replaceBlockM.place(block);
            }
        }.runTask(instance);

        // голограмма
        this.remainingTimeToHologramDespawn = 2;

        FileConfiguration fileConfiguration = instance.getConfig();
        List<String> miningBlockHologramStringList = fileConfiguration.getStringList("mining_block_hologram_string_list");

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%income%", Util.format(income));
        miningBlockHologramStringList = UtilA.placeholders(miningBlockHologramStringList, placeholders);
        miningBlockHologramStringList = UtilM.papi(whoBroke, miningBlockHologramStringList);

        // расчет положения призыва голограммы по центру над блоком
        Location blockLocation = this.block.getLocation();
        double miningBlockHologramHeight = fileConfiguration.getDouble("mining_block_hologram_height");
        Location hologramLocation = new Location(blockLocation.getWorld(), blockLocation.getX() + 0.5, blockLocation.getY() + miningBlockHologramHeight, blockLocation.getZ() + 0.5);

        this.hologram = DHAPI.createHologram(UUID.randomUUID().toString(), hologramLocation, miningBlockHologramStringList);
        this.hologram.setDownOrigin(true);
    }

    @Override
    public void update() {
        if (--this.remainingTimeToHologramDespawn <= 0) despawnHologram();
        if (--this.remainingTimeToRespawn <= 0) destroy();
    }

    @Override
    public void destroy() {
//        BlockM blockM = this.miningBlock.getBlockM();
//        blockM.place(this.block);
        // восстанавливать именно сломанный блок, на новых версиях работать не будет полагаю
        this.regenerativeOriginalBrokenBlock.place(this.block);
        despawnHologram();
    }

    public void despawnHologram() {
        DHAPI.removeHologram(this.hologram.getName());
    }

    public boolean isComplete() {
        return this.remainingTimeToHologramDespawn <= 0 && this.remainingTimeToRespawn <= 0;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public MiningBlock getMiningBlock() {
        return miningBlock;
    }

    public void setMiningBlock(MiningBlock miningBlock) {
        this.miningBlock = miningBlock;
    }

    public Player getWhoBroke() {
        return whoBroke;
    }

    public void setWhoBroke(Player whoBroke) {
        this.whoBroke = whoBroke;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public int getRemainingTimeToRespawn() {
        return remainingTimeToRespawn;
    }

    public void setRemainingTimeToRespawn(int remainingTimeToRespawn) {
        this.remainingTimeToRespawn = remainingTimeToRespawn;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    public int getRemainingTimeToHologramDespawn() {
        return remainingTimeToHologramDespawn;
    }

    public void setRemainingTimeToHologramDespawn(int remainingTimeToHologramDespawn) {
        this.remainingTimeToHologramDespawn = remainingTimeToHologramDespawn;
    }

}
