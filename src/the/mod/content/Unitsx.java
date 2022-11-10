package the.mod.content;

import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Rect;
import arc.struct.Seq;
import mindustry.ai.UnitCommand;
import mindustry.content.*;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.RegenAbility;
import mindustry.entities.abilities.RepairFieldAbility;
import mindustry.entities.bullet.*;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;
import the.mod.TheTech;
import the.mod.utils.Bullets;
import the.mod.utils.Timer;
import the.mod.utils.Types;

public class Unitsx {
    public static final Seq<Types.ModUnitType> all = new Seq<>();

    public static class FieldAbility extends Ability {
        public Cons2<Unit, Unit> handler = (a, b) -> {};
        public StatusEffect status;
        public boolean friendly;
        public Color color;
        public float range;

        @Override
        public void update(Unit unit) {
            super.update(unit);

            Groups.unit.each(e -> {
                if(TheTech.len(unit.x, e.x, unit.y, e.y) < range && e != unit) {
                    handler.get(e, unit);

                    if(friendly == (unit.team == e.team)) {
                        e.apply(status, Timer.second8);
                    }
                }
            });
        }

        @Override
        public void draw(Unit unit) {
            super.draw(unit);

            Draw.draw(100, () -> {
                Draw.color(Pal.darkOutline);
                Lines.stroke(3.5f);
                Lines.arc(unit.x, unit.y, range, 1f);

                Draw.color(color);
                Lines.stroke(1.5f);
                Lines.arc(unit.x, unit.y, range, 1f);

                Draw.color();
            });
        }
    }

    public static <T extends Types.ModUnitType> T add(T type) {
        all.add(type);
        TheTech.all.add(type);
        return type;
    }

    public static Types.ModUnitType
            //core
            hellx, hell59, hellix9,

            //ground damage
            bbi, bb72, dx5, jok9, AX50,

            //ground support
            fed1, fdx, fd832, fk3, gt2,

            //air damage
            sena5, sen57, seno99, snx8,

            //air support
            yt1, yt2, yt3, yt4, yt5,

            //other
            javelin, trident, tau, alpha, delta, omega;

    public static void load() {
        //core
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
                            Bullets.setup(this, Color.acid);
                            damage = speed = 7;

                            frontColor = Color.white;
                            backColor = trailColor = hitColor = Color.acid;

                            status = StatusEffects.disarmed;
                        }};
                    }}
            );
        }});

        //ground damage
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
                    Bullets.setup(this, Color.red);

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

        bb72 = add(new Types.ModUnitType("bb72") {{
            tank = true;

            faceTarget = false;
            rotateMoveFirst = false;
            hitSize = 18f;
            treadPullOffset = 5;
            speed = 0.7f;
            rotateSpeed = 2.6f;
            health = 500;
            armor = 8f;
            itemCapacity = 0;
            treadRects = new Rect[] { new Rect(17 - 96f/2f, 10 - 96f/2f, 19, 76) };
            researchCostMultiplier = 0f;

            abilities.add(new FieldAbility() {{
                color = Color.blue;
                status = StatusEffects.wet;
                range = 80;
                friendly = false;
            }});

            weapons.add(new Types.ModWeapon("b7") {{
                reload = 30f;
                rotate = true;
                mirror = false;
                x = y = 0;

                bullet = new LaserBulletType() {{
                    damage = 24;
                    status = StatusEffects.shocked;
                }};
            }});
        }});

        dx5 = add(new Types.ModUnitType("dx5") {{
            ground = true;
            health = 750;
            armor = 12f;
            speed = 1;

            range = Timer.second8 * 4; //time * speed = distance
            weapons.add(new Types.ModWeapon("air") {{
                reload = Timer.second8 / 4;
                mirror = false;
                x = y = 0;

                bullet = new BasicBulletType(4f, 75) {{
                    Bullets.setup(this, Pal.bulletYellowBack);
                    hitColor = backColor = Pal.bulletYellowBack;
                    lifetime = Timer.second8 / 4;

                    splashDamage = 25;
                    splashDamageRadius = 40;

                    fragBullets = 10;
                    fragBullet = new BasicBulletType(7f, 15) {{
                        Bullets.setup(this, Pal.bulletYellowBack);
                        hitColor = backColor = Pal.bulletYellowBack;
                    }};
                }};
            }});
        }});

        //other
        javelin = add(new Types.ModUnitType("javelin") {{
            flying = true;

            health = 450;
            speed = 4;

            weapons.add(
                    new Types.ModWeapon("air") {{
                        x = y = 0;

                        reload = 15f;
                        mirror = false;
                        rotate = false;

                        bullet = new BasicBulletType(5f, 12) {{
                            sprite = backSprite = TheTech.modId + "-javelin-form";
                            backColor = frontColor = hitColor = Color.blue;
                            width = height = splashDamageRadius = 16;
                            splashDamage = 8;

                            Bullets.setup(this, Color.blue);
                        }};
                    }}
            );

            color(Color.blue);
        }});

        trident = add(new Types.ModUnitType("trident") {{
            defaultCommand = UnitCommand.repairCommand;
            flying = true;

            health = 300;
            speed = 3;

            faceTarget = isEnemy = false;
            payloadCapacity = 256;
            color(Pal.heal);

            abilities.add(new RegenAbility() {{
                amount = 50;
                display = true;
            }});

            weapons.add(
                    new Types.ModWeapon("r4") {{
                        x = -4;
                        reload = 15f;
                        rotate = true;

                        bullet = new LaserBoltBulletType(7f, 5) {{
                            Bullets.setup(this, Pal.heal);

                            canHeal = true;
                            healPercent = 3f;
                            collidesTeam = true;

                            backColor = hitColor = Pal.heal;
                            frontColor = Color.white;
                        }};
                    }}
            );

            researchRequirements(ItemStack.with(
                    Itemsx.virusM, 20,
                    Itemsx.silica, 40,
                    Itemsx.coalSand, 10
            ));
        }});

        delta = add(new Types.ModUnitType("delta") {{
            ground = true;
            health = 600;
            speed = 1.4f;

            weapons.add(
                new Types.ModWeapon("delta-weapon") {{
                    x = -4;
                    reload = 30f;

                    bullet = new LightningBulletType() {{
                        damage = 25;
                        lightning = 4;
                        backMove = false;
                        collidesAir = false;
                        lightningLength = 24;

                        status = StatusEffects.shocked;
                    }};
                }}
            );
        }});
    }
}