package the.mod.types;

import arc.struct.Seq;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;
import the.mod.TheTech;
import the.mod.utils.Types;

import static mindustry.Vars.*;

public class Dracula {
    public static class DBulletType extends BasicBulletType {
        public float heal = 5f;
        public Effect healEffect;

        @Override
        public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
            super.hitTile(b, build, x, y, initialHealth, direct);
            heal(heal, healEffect);
        }

        @Override
        public void hitEntity(Bullet b, Hitboxc entity, float health) {
            super.hitEntity(b, entity, health);
            heal(heal, healEffect);
        }

        public Seq<Building> buildingSeq() {
            return buildingSeq(null);
        }

        public Seq<Building> buildingSeq(Building self) {
            Seq<Building> cache = new Seq<>();
            world.tiles.eachTile(t -> {
                if(t.block() instanceof DraculaTurret) {
                    cache.add(t.build);
                }
            });

            Seq<Building> result = new Seq<>();
            for(Building b : cache) {
                boolean found = false;
                for(Building b2 : result) {
                    if(b2 == b || TheTech.isPart(b, b2)) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    result.add(b);
                }
            }

            if(self != null) {
                Seq<Building> r = new Seq<>();
                for(Building b : result) {
                    if(b != self) {
                        r.add(b);
                    }
                }

                return r;
            }

            return result;
        }

        public void heal(float percent, Effect healEffect) {
            for(Building b : buildingSeq()) {
                if(healEffect != null) {
                    healEffect.at(b.x, b.y);
                }

                b.heal((b.health / TheTech.toBlock(b).health) * percent);
            }
        }
    }

    public interface DraculaTurret {
    }

    public static class DraculaItemTurret extends Types.ModItemTurret implements DraculaTurret {
        public DraculaItemTurret(String name) {
            super(name);
        }
    }
}