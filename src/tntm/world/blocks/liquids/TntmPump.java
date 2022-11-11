package tntm.world.blocks.liquids;

import mindustry.world.blocks.production.Pump;
import tntm.TheTech;

public class TntmPump extends Pump {
    public TntmPump(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }
}