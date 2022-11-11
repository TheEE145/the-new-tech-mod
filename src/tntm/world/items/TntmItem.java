package tntm.world.items;

import arc.graphics.Color;
import mindustry.type.Item;
import tntm.TheTech;

public class TntmItem extends Item {
    public TntmItem(String name, Color color) {
        super(name, color);

        localizedName = TheTech.prefix(localizedName);
    }
}