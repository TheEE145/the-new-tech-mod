package tntm.world.blocks.enviroment;

import mindustry.type.Item;
import mindustry.world.blocks.environment.StaticWall;
import tntm.TheTech;

public class TntmStaticWall extends StaticWall {
    public Item itemDrop;

    public TntmStaticWall(String name) {
        super(name);
        localizedName = TheTech.prefix(localizedName);
    }
}