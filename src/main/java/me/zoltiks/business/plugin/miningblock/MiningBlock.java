package me.zoltiks.business.plugin.miningblock;

import me.socrum.minecraft.spigot.plugin.utilm.BlockM;

import java.util.List;

public class MiningBlock {

    private String id;
    private BlockM blockM;
    private BlockM replaceBlockM;
    private int repsawnDelay;
    private String incomeFormula;
    private List<String> canOnlyBeBrokeInWorldGuardRegionList;

    public MiningBlock(String id, BlockM blockM, BlockM replaceBlockM, int repsawnDelay, String incomeFormula, List<String> canOnlyBeBrokeInWorldGuardRegionList) {
        this.id = id;
        this.blockM = blockM;
        this.replaceBlockM = replaceBlockM;
        this.repsawnDelay = repsawnDelay;
        this.incomeFormula = incomeFormula;
        this.canOnlyBeBrokeInWorldGuardRegionList = canOnlyBeBrokeInWorldGuardRegionList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BlockM getBlockM() {
        return blockM;
    }

    public void setBlockM(BlockM blockM) {
        this.blockM = blockM;
    }

    public BlockM getReplaceBlockM() {
        return replaceBlockM;
    }

    public void setReplaceBlockM(BlockM replaceBlockM) {
        this.replaceBlockM = replaceBlockM;
    }

    public int getRepsawnDelay() {
        return repsawnDelay;
    }

    public void setRepsawnDelay(int repsawnDelay) {
        this.repsawnDelay = repsawnDelay;
    }

    public String getIncomeFormula() {
        return incomeFormula;
    }

    public void setIncomeFormula(String incomeFormula) {
        this.incomeFormula = incomeFormula;
    }

    public List<String> getCanOnlyBeBrokeInWorldGuardRegionList() {
        return canOnlyBeBrokeInWorldGuardRegionList;
    }

    public void setCanOnlyBeBrokeInWorldGuardRegionList(List<String> canOnlyBeBrokeInWorldGuardRegionList) {
        this.canOnlyBeBrokeInWorldGuardRegionList = canOnlyBeBrokeInWorldGuardRegionList;
    }

}
