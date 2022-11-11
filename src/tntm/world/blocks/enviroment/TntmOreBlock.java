package tntm.world.blocks.enviroment;

import mindustry.type.Item;
import mindustry.world.blocks.environment.OreBlock;
import tntm.TheTech;

public class TntmOreBlock extends OreBlock {
    public TntmOreBlock(String name, Item ore) {
        super(name, ore);
        localizedName = TheTech.prefix(localizedName);
    }
}