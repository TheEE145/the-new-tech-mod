package tntm.world.blocks.enviroment;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.world.meta.BuildVisibility;
import tntm.TheTech;
import tntm.asset.TntmAssets;
import tntm.content.TntmFx;
import tntm.world.blocks.TntmB;

import static mindustry.type.ItemStack.with;

public class CrystalBlock extends TntmB {
    public boolean defaultBlockAssets = true;
    public Color color;
    public int sprites = 3, amount;
    public Item item;

    public TextureRegion[] regions;

    public CrystalBlock(String name, Item item, int amount) {
        super(name);

        update = true;
        solid = true;
        rotate = false;
        drawDisabled = false;

        this.item = item;
        this.amount = amount;

        requirements(Category.effect, with(item, amount));
        destroyEffect = breakEffect = TntmFx.bread;

        buildVisibility = BuildVisibility.sandboxOnly;
        researchCost = with();

        instantDeconstruct = true;
        deconstructThreshold = 1f;
        rebuildable = false;
        hasColor = true;
        mapColor = item.color;
    }

    @Override
    public void load() {
        super.load();

        if(defaultBlockAssets) {
            regions = new TextureRegion[sprites];
            for(int i = 0; i < sprites; i++) {
                regions[i] = TntmAssets.mod("crystal-default-" + (1 + i));
            }
        } else {
            regions = new TextureRegion[sprites];
            regions[0] = TntmAssets.get(name);

            if(sprites > 1) {
                for(int i = 1; i < sprites; i++) {
                    regions[i] = TntmAssets.get(name + i);
                }
            }
        }
    }

    public class CrystalBlockBuild extends Building {
        float rot;
        int sprite;

        public CrystalBlockBuild() {
            rot = Mathf.random(359f);
            sprite = Mathf.random(regions.length - 1);
            Log.info(sprite);
        }

        @Override
        public void draw() {
            Draw.draw(Layer.blockOver - 1f, () -> {
                Draw.blend(Blending.additive);
                Draw.color(item.color, 0.2f);
                Draw.rect("circle-shadow", x, y, size*8, size*8);
                Draw.blend();

                if(defaultBlockAssets) {
                    Draw.color(color == null ? item.color : color);
                } else {
                    Draw.color(Color.white);
                }

                Draw.rect(regions[sprite], x, y, rot);
            });
        }

        @Override
        public void drawTeam() {
        }

        @Override
        public void drawLight(){
            super.drawLight();
            Drawf.light(x, y, 40f, Tmp.c1.set(item.color).mul(0.7f), 0.35f);
            Drawf.light(x, y, 20f, Tmp.c1.set(item.color).mul(0.7f), 1f);
        }

        @Override
        public void onDestroyed() {
            destroyEffect.at(x, y, item.color);
        }
    }
}