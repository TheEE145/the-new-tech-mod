package tntm.world.blocks.distribution;

import mindustry.world.blocks.distribution.Router;
import tntm.TheTech;

public class TntmRouter extends Router {
    public TntmRouter(String name) {
        super(name);
        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }
}