package tntm.world.blocks.distribution;

import mindustry.world.blocks.distribution.ItemBridge;
import tntm.TheTech;

public class TntmBridge extends ItemBridge {
    public TntmBridge(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }
}