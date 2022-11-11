package tntm.world.blocks.production;

import mindustry.world.blocks.production.Drill;
import tntm.TheTech;

public class TntmDrill extends Drill {
    public TntmDrill(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);

        hasItems = true;
        itemCapacity = 15;
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }
}