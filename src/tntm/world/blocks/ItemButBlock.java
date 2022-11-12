package tntm.world.blocks;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

public class ItemButBlock extends TntmB {
    public ItemButBlock(String name) {
        super(name);

        buildVisibility = BuildVisibility.sandboxOnly;
        category = Category.effect;

        health = 1;
        researchCostMultiplier = 5f;

        solid = false;
        update = true;
        hasShadow = false;
        rebuildable = false;
        drawDisabled = false;
        squareSprite = false;
    }

    @Override
    public boolean canBeBuilt(){
        return false;
    }

    public class ItemButBlockBuild extends Building {
        @Override
        public void draw() {
            Draw.rect(region, x, y);
        }

        @Override
        public void drawLight() {
        }
    }
}