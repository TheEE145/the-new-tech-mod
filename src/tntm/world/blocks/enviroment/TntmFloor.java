package tntm.world.blocks.enviroment;

import mindustry.world.blocks.environment.Floor;
import tntm.TheTech;

public class TntmFloor extends Floor {
    public TntmFloor(String name) {
        super(name);
        localizedName = TheTech.prefix(localizedName);
    }

    public TntmFloor(String name, int variants) {
        super(name, variants);
        localizedName = TheTech.prefix(localizedName);
    }
}