package me.zoltiks.business.plugin.miningblock;

import me.socrum.advanced.ini.Initer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.zoltiks.business.Main.instance;

public class MiningBlockScheduler implements Initer {

    private final List<BrokenMiningBlock> brokenMiningBlockList;
    private final BukkitRunnable bukkitRunnable;

    public MiningBlockScheduler() {
        this.brokenMiningBlockList = new ArrayList<>();
        this.bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < brokenMiningBlockList.size(); i++) {
                    BrokenMiningBlock brokenMiningBlock = brokenMiningBlockList.get(i);
                    brokenMiningBlock.update();

                    // если временный объект завершил работу, удалить из списка
                    if (!brokenMiningBlock.isComplete()) continue;
                    brokenMiningBlockList.remove(i);
                    i--;
                }
            }
        };
    }

    @Override
    public void init() {
        this.bukkitRunnable.runTaskTimer(instance, 0, 20);
    }

    @Override
    public void destroy() {
        this.bukkitRunnable.cancel();

        // необходимо дополнительно произвести корректное завершение работы временных объектов
        this.brokenMiningBlockList.forEach(BrokenMiningBlock::destroy);
        this.brokenMiningBlockList.clear();
    }

    public List<BrokenMiningBlock> getBrokenMiningBlockList() {
        return brokenMiningBlockList;
    }

    public BukkitRunnable getBukkitRunnable() {
        return bukkitRunnable;
    }

}
