package tntm.world.buffs;

import mindustry.ctype.UnlockableContent;
import mindustry.world.meta.Stat;
import tntm.world.blocks.production.TntmDrill;

public class DrillSpeedBuff extends TntmBuff {
    public float boost;

    public DrillSpeedBuff(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.mineSpeed, 100 * boost + "%");
    }

    @Override
    public void handler(UnlockableContent c) {
        super.handler(c);

        if(c instanceof TntmDrill drill) {
            drill.drillTime /= boost;
        }
    }
}