package tntm.world.blocks.meteria;

import tntm.content.TntmStats;
import tntm.world.blocks.walls.TntmBlock;

public class MeteriaNodeBooster extends TntmBlock {
    public float meteriaBoost;

    public MeteriaNodeBooster(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(TntmStats.meteriaBoost, meteriaBoost);
    }
}