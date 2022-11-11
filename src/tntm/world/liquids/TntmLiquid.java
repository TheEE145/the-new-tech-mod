package tntm.world.liquids;

import arc.graphics.Color;
import mindustry.type.Liquid;
import tntm.TheTech;

public class TntmLiquid extends Liquid {
    public TntmLiquid(String name, Color color) {
        super(name, color);
        localizedName = TheTech.prefix(localizedName);
    }
}