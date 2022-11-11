package tntm.world.blocks.distribution;

import mindustry.world.blocks.distribution.Conveyor;
import tntm.TheTech;

public class TntmConveyor extends Conveyor {
    public TntmConveyor(String name) {
        super(name);
        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }
}