package the.mod.content;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;
import the.mod.types.*;

import static the.mod.types.Meteria.*;
import static mindustry.type.ItemStack.with;

public class Blocks {
    public static Minigun ares;

    public static MeteriaNode meteriaNode, largeMeteriaNode, sandboxMeteriaNode;
    public static MeteriaNodeBooster meteriaBooster;
    public static MeteriaSource meteriaSource;
    public static MeteriaCrafter coalMeteriaGenerator;

    public static void load() {
        //miniguns
        ares = new Minigun("ares") {{
            requirements(Category.turret, with(Items.copper, 1));

            health = 240;

            range = 310f;
            size = 4;

            shootY = 15f;
            rotateSpeed = 5f;
            consumeAmmoOnce = true;
            shootSound = Sounds.shootBig;

            drawer = new DrawTurret("reinforced-") {{
                parts.add(
                        new RegionPart("-side") {{
                            mirror = true;
                            under = true;
                            moveX = 1.75f;
                            moveY = -0.5f;

                            rotateSpeed = 5f;
                            moveRot = -45f;
                        }},

                        new RegionPart("-back") {{
                            under = true;
                            moveY = 0.5f;
                        }},

                        new RegionPart("-mid") {{
                           under = true;
                           moveY = -1.5f;
                        }}
                );
            }};

            ammo(Items.copper, new BasicBulletType() {{
                damage = 251;
                speed = 8.5f;

                width = health = 16;
                shrinkY = 0.3f;

                collidesTiles = true;
                frontColor = Color.white;
                backColor = trailColor = hitColor = Color.sky;
                trailChance = 0.44f;

                lifetime = 34f;
                rotationOffset = 90f;
                trailRotation = true;
                trailEffect = Fx.disperseTrail;

                hitEffect = despawnEffect = Fx.hitBulletColor;
            }});
        }};

        //meteria nodes
        meteriaNode = new MeteriaNode("meteria-node") {{
            maxMeteria = 10000;
            range = 25;

            size = 2;

            requirements(Category.power, with(
                    Items.copper, 100
            ));
        }};

        largeMeteriaNode = new MeteriaNode("large-meteria-node") {{
            maxMeteria = 50000;
            range = 50;

            size = 3;

            requirements(Category.power, with(
                    Items.copper, 100
            ));
        }};

        sandboxMeteriaNode = new MeteriaNode("sandbox-meteria-node") {{
            maxMeteria = 900000;
            range = 1000;
            size = 3;

            category = Category.power;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        //meteria
        meteriaBooster = new MeteriaNodeBooster("meteria-booster") {{
            meteriaBoost = 2000;
            size = 2;

            requirements(Category.power, with(
                    Items.copper, 100
            ));
        }};

        meteriaSource = new MeteriaSource("meteria-source") {{
            category = Category.power;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        coalMeteriaGenerator = new MeteriaCrafter("fuel-meteria-generator") {{
            meteriaGet = 100;
            maxMeteria = 1000;

            craftEffect = Effects.craftMeteria;

            hasItems = true;
            itemCapacity = 10;

            consumeItems(with(
                    Items.coal, 1
            ));

            requirements(Category.power, with(
                    Items.copper, 100
            ));
        }};
    }
}