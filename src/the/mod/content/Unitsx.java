package the.mod.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import the.mod.TheTech;
import the.mod.utils.Types;

public class Unitsx {
    public static final Seq<Types.ModUnitType> all = new Seq<>();

    public static <T extends Types.ModUnitType> T add(T type) {
        all.add(type);
        TheTech.all.add(type);
        return type;
    }

    public static Types.ModUnitType
            //core
            hellx, hell59, hellix9,

            //ground damage
            bbi, bb72, dx5, jok9, AX50;

    public static void load() {
        hellx = add(new Types.ModUnitType("hellx") {{
            helicopter = true;

            health = 250;
            flying = true;

            buildSpeed = 5;
            itemCapacity = 40;
            mineFloor = true;
            mineSpeed = 3f;
            mineTier = 1;

            rotors = new Rotor[] {
                    new Rotor() {{
                        small = 2f;
                        y = -1;
                    }}
            };

            speed = 2;
            weapons.add(
                    new Types.ModWeapon("rocket1x") {{
                        x = 12;
                        y = 4;

                        mirror = true;
                        reload = 15f;

                        bullet = new MissileBulletType() {{
                            damage = speed = 7;

                            frontColor = Color.white;
                            backColor = trailColor = hitColor = Color.acid;

                            status = StatusEffects.disarmed;
                            despawnEffect = hitEffect = Effects.bulletCollision;
                        }};
                    }}
            );
        }});

        bbi = add(new Types.ModUnitType("bbi") {{
            ground = true;

            speed = 0.7f;
            hitSize = 7f;
            health = 200;

            weapons.add(new Types.ModWeapon("a1"){{
                reload = 17f;
                x = 4f;
                y = 2f;
                top = false;
                ejectEffect = Fx.casing1;
                bullet = new BasicBulletType(2.5f, 9){{
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;

                    frontColor = Color.white;
                    backColor = Color.red;
                    status = StatusEffects.burning;

                    despawnEffect = hitEffect = Effects.bulletCollision;
                }};
            }});
        }});
    }
}