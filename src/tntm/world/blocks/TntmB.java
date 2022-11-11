package tntm.world.blocks;

import mindustry.world.Block;
import tntm.TheTech;

public class TntmB extends Block {
    public TntmB(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }
}