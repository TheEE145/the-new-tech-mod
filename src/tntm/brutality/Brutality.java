package tntm.brutality;

import arc.Events;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.AmmoType;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import tntm.TheTech;
import tntm.content.TntmBlocks;
import tntm.content.TntmFx;
import tntm.graphics.TntmDraw;
import tntm.utils.TntmMath;
import tntm.world.blocks.turrets.PayloadTurret;
import tntm.world.bullets.BlockBulletType;

import javax.xml.crypto.Data;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Brutality {
    public static boolean bloodEnabled() {
        return settings.getBool("blood");
    }

    public static boolean corpsesEnabled() {
        return settings.getBool("corpses");
    }

    public static boolean bloodFxEnabled() {
        return settings.getBool("bloodFx");
    }

    public static void load() {
        ui.settings.addCategory("@settings.brutality", t -> {
            t.checkPref("blood", false);
            t.checkPref("corpses", false);
            t.checkPref("bloodFx", false);
        });

        if(bloodFxEnabled()) {
            content.blocks().each(b -> {
                if(b instanceof ItemTurret t) {
                    t.ammoTypes.values().forEach(Brutality::handle);
                }

                if(b instanceof LiquidTurret t) {
                    t.ammoTypes.values().forEach(Brutality::handle);
                }

                if(b instanceof PayloadTurret t) {
                    t.ammoTypes.values().forEach(Brutality::handle);
                }
            });

            content.units().each(u -> {
                u.weapons.each(w -> {
                    handle(w.bullet);
                });

                u.deathExplosionEffect = TntmFx.death;
            });
        }

        if(corpsesEnabled()) {
            content.units().each(u -> {
                u.deathExplosionEffect = new MultiEffect() {{
                    effects = new Effect[] {
                            u.deathExplosionEffect,
                            new CorpseSpawnEffect(u)
                    };
                }};
            });
        }
    }

    public static class CorpseSpawnEffect extends Effect {
        public CorpseSpawnEffect(UnitType type) {
            super(2, b -> {
                Tile tile = TntmMath.getTileByDraw(b.x, b.y);

                if(tile.build != null) {
                    return;
                }

                tile.setNet(TntmBlocks.corpse);
                if(tile.build instanceof Corpse.CorpseBuild bu) {
                    bu.set(type);
                }
            });
        }
    }

    public static void handle(BulletType type) {
        type.hitEffect = new MultiEffect() {{
            effects = new Effect[] {
                type.hitEffect, TntmFx.damage
            };
        }};
    }
}