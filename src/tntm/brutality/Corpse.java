package tntm.brutality;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.world.blocks.environment.StaticWall;
import tntm.asset.TntmAssets;
import tntm.utils.TntmTimer;
import tntm.world.blocks.walls.TntmBlock;

public class Corpse extends TntmBlock {
    public Corpse(String name) {
        super(name);

        enableDrawStatus = false;
        breakable = alwaysReplace = false;

        update = true;
        solid = true;
    }

    public class CorpseBuild extends TntmBlock.TntmBlockBuild {
        public float lifetime = TntmTimer.second8;

        public boolean errorNow() {
            return cache == null || positions == null || type == null;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            timer--;
            if(timer < 0) {
                removeX();
            }
        }

        public float aFloat;
        public CorpseFragment[] cache;
        public Vec2[] positions;

        public UnitType type;
        public float timer;

        public float maxDelta() {
            return type.hitSize / 2;
        }

        public Vec2 deltaPos() {
            return new Vec2(delx(), delx());
        }

        public float delx() {
            return Mathf.random(-maxDelta(), maxDelta());
        }

        public float fin() {
            return timer / lifetime;
        }

        public void removeX() {
            tile.setNet(Blocks.air);
        }

        @Override
        public void draw() {
            if(errorNow()) {
                return;
            }

            for(int i = 0; i < cache.length; i++) {
                Vec2 pos = positions[i];
                cache[i].draw(this, x() + pos.x, y() + pos.y);
            }

            Draw.alpha(fin());
            Draw.rect(TntmAssets.get(type.name), x, y, aFloat);
        }

        @Override
        public void drawTeam() {
        }

        public void set(UnitType type2) {
            this.type = type2 == null ? UnitTypes.dagger : type2;

            int bloodCircles = Brutality.bloodEnabled() ? Mathf.random(2, 4) : 0;
            int weapons = Brutality.bloodFxEnabled() ? type.weapons.size : 0;

            int len = bloodCircles + weapons;
            cache = new CorpseFragment[len];
            positions = new Vec2[len];
            aFloat = Mathf.random(0, 360);
            timer = lifetime;

            int circlesLeft = bloodCircles;
            int fxLeft = weapons;

            for(int i = 0; i < len; i++) {
                if(circlesLeft > 0 && Brutality.bloodEnabled()) {
                    circlesLeft--;

                    cache[i] = new CorpseFragment.BloodFrag(Mathf.random(4, 16));
                    positions[i] = deltaPos();
                    continue;
                }

                if(fxLeft > 0 && Brutality.bloodFxEnabled()) {
                    fxLeft--;

                    cache[i] = new CorpseFragment.BloodFxFrag();
                    positions[i] = deltaPos();
                }
            }
        }
    }
}