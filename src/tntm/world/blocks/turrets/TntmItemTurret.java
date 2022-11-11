package tntm.world.blocks.turrets;

import mindustry.world.blocks.defense.turrets.ItemTurret;
import tntm.TheTech;
import tntm.content.TntmLiquids;

public class TntmItemTurret extends ItemTurret {
    public TntmItemTurret(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
        consumeLiquid(TntmLiquids.emethen, 0.25f).boost();
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }
}