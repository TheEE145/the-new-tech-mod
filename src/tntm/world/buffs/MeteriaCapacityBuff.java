package tntm.world.buffs;

import mindustry.ctype.UnlockableContent;
import tntm.content.TntmStats;
import tntm.world.blocks.meteria.MeteriaNode;

public class MeteriaCapacityBuff extends TntmBuff {
    public float boost;

    public MeteriaCapacityBuff(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(TntmStats.meteriaBoost, 100 * boost + "%");
    }

    @Override
    public void handler(UnlockableContent c) {
        super.handler(c);

        if(c instanceof MeteriaNode node) {
            node.maxMeteria *= boost;
        }
    }
}