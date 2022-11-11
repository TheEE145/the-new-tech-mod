package tntm.world.blocks.walls;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.world.blocks.defense.Wall;
import tntm.TheTech;
import tntm.asset.TntmAssets;
import tntm.graphics.drawer.Drawer;
import tntm.graphics.drawer.MultiDrawer;
import tntm.graphics.drawer.PlanDrawer;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class TntmBlock extends Wall {
    public float dynamicEffectChange = 0f;
    public Effect dynamicEffect = Fx.none;
    public boolean canBurn = true;
    public PlanDrawer[] drawer;

    public TntmBlock(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
        squareSprite = false;

        flashHit = false;
        update = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }

    @Override
    public void load() {
        super.load();

        if(atlas.has(name + "-preview")) {
            uiIcon = TntmAssets.get(name + "-preview");
        }
    }

    public class TntmBlockBuild extends WallBuild {
        public MultiDrawer localDrawer = new MultiDrawer();
        private boolean loaded = false;

        public TntmBlockBuild() {
            if(drawer != null) {
                localDrawer.drawers = new Drawer[drawer.length];
                for(int i = 0; i < drawer.length; i++) {
                    localDrawer.drawers[i] = drawer[i].get();
                }
            }
        }

        public void extinguish(float x, float y) {
            Fires.extinguish(world.tileWorld(this.x + x, this.y + y), 9000);
        }

        @Override
        public void draw() {
            if(drawer == null) {
                super.draw();
            } else {
                if(!loaded) {
                    localDrawer.load(TheTech.toBlock(this));
                    loaded = true;
                }

                localDrawer.draw(x, y);
            }
        }

        public float diner() {
            return Mathf.range(size * 2.356f);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if(!canBurn) {
                extinguish(2, 2);
                for(Point2 p : Geometry.d8) {
                    extinguish(p.x * tilesize, p.y * tilesize);
                }
            }
        }

        @Override
        public void update() {
            super.update();

            if(Mathf.chanceDelta(dynamicEffectChange)) {
                dynamicEffect.at(x + diner(), y + diner());
            }
        }
    }
}