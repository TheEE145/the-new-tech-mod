package tntm.graphics.drawer;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import mindustry.graphics.Pal;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import tntm.TheTech;
import tntm.asset.TntmAssets;
import tntm.utils.ticker.RotationTicker;

public class Drawer {
    TextureRegion region;
    String prefix;

    public Drawer(String prefix) {
        this.prefix = prefix;
    }

    public <T extends Block> void load(T block) {
        region = TntmAssets.get(block.name + prefix);
    }

    public void update() {
    }

    public TextureRegion region() {
        return region;
    }

    public void draw(float x, float y) {
        Draw.rect(region, x, y);
    }
}