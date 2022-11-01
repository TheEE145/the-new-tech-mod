package the.mod.content;

import arc.graphics.*;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;
import the.mod.TheTech;
import the.mod.types.*;
import the.mod.utils.Types.*;

import static the.mod.types.Meteria.*;
import static mindustry.type.ItemStack.with;

public class Blocks {
    public static final Seq<Block> all = new Seq<>();

    //defence
    public static ModBlock silicaWall, largeSilicaWall, virusMWall, virusMWallLarge;
    public static Minigun ares;

    //energy
    public static MeteriaNode meteriaNode, largeMeteriaNode, sandboxMeteriaNode;
    public static MeteriaNodeBooster meteriaBooster;
    public static MeteriaSource meteriaSource;
    public static MeteriaCrafter coalMeteriaGenerator;

    //drills
    public static MeteriaDrill meteriaDrill, largeDrill, nuclearDrill;
    public static ModDrill silicaDrill, updatedDrill;

    //unloaders, why not
    public static LiquidUnloader liquidUnloader;

    //crafters
    public static ModCrafter silicaPress;
    public static MeteriaPlant meteriaPress;

    //cores
    public static ModCore terra;

    public static <T extends Block> T add(T type) {
        all.add(type);
        TheTech.all.add(type);
        return type;
    }

    public static void load() {
        //miniguns
        ares = add(new Minigun("ares") {{
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
        }});

        //meteria nodes
        meteriaNode = add(new MeteriaNode("meteria-node") {{
            maxMeteria = 10000;
            range = 25;

            size = 2;

            requirements(Category.power, with(
                    Itemsx.silica, 25
            ));
        }});

        largeMeteriaNode = add(new MeteriaNode("large-meteria-node") {{
            maxMeteria = 50000;
            range = 50;

            size = 3;

            requirements(Category.power, with(
                    Itemsx.silica, 125
            ));
        }});

        sandboxMeteriaNode = add(new MeteriaNode("sandbox-meteria-node") {{
            maxMeteria = 900000;
            range = 1000;
            size = 3;

            category = Category.power;
            buildVisibility = BuildVisibility.sandboxOnly;
        }});

        //meteria
        meteriaBooster = add(new MeteriaNodeBooster("meteria-booster") {{
            meteriaBoost = 2000;
            size = 2;

            requirements(Category.power, with(
                    Itemsx.silica, 75
            ));
        }});

        meteriaSource = add(new MeteriaSource("meteria-source") {{
            category = Category.power;
            buildVisibility = BuildVisibility.sandboxOnly;
        }});

        coalMeteriaGenerator = add(new MeteriaCrafter("fuel-meteria-generator") {{
            meteriaGet = 100;
            maxMeteria = 1000;

            craftEffect = Effects.craftMeteria;

            hasItems = true;
            itemCapacity = 10;

            consumeItems(with(
                    Items.coal, 1
            ));

            requirements(Category.power, with(
                    Itemsx.silica, 25
            ));
        }});

        //cores
        terra = add(new ModCore("core-terra") {{
            health = 25000;
            unitType = UnitTypes.alpha;
            size = 4;

            itemCapacity = 10000;
        }});

        //drills
        silicaDrill = add(new ModDrill("silica-drill") {{
            requirements(Category.production, with(Itemsx.silica, 12));

            tier = 2;
            drillTime = 600f;
            size = 2;

            consumeLiquid(Liquids.water, 0.05f).boost();
            researchCost = with();
        }});

        updatedDrill = add(new ModDrill("updated-drill") {{
            requirements(Category.production, with(Itemsx.silica, 24));

            tier = 3;
            drillTime = 400f;
            size = 3;

            consumeLiquid(Liquids.water, 0.06f).boost();
        }});

        meteriaDrill = add(new MeteriaDrill("meteria-drill") {{
            requirements(Category.production, with(Itemsx.silica, 72));

            meteriaConsume = 2;
            maxMeteria = 500;
            size = 4;

            drillTime = 280f;
            tier = 4;

            consumeLiquid(Liquids.water, 0.08f).boost();
        }});

        //unloaders
        liquidUnloader = add(new LiquidUnloader("liquid-unloader") {{
            requirements(Category.liquid, with(
                    Items.titanium, 25,
                    Items.metaglass, 10
            ));

            size = 1;
        }});

        //crafters
        silicaPress = add(new ModCrafter("silica-press") {{
            health = 420;
            size = 2;

            hasItems = true;
            itemCapacity = 15;
            outputItem = with(Itemsx.silicaSand, 2)[0];

            consumeItems(with(
                    Itemsx.silica, 1
            ));

            requirements(Category.crafting, with(
                    Itemsx.silica, 50
            ));
        }});

        meteriaPress = add(new MeteriaPlant("meteria-press") {{
            health = 560;
            size = 3;

            maxMeteria = 500;
            meteriaConsume = 100;

            hasItems = true;
            itemCapacity = 15;
            outputItem = with(Itemsx.virusMSand, 2)[0];

            consumeItems(with(
                    Itemsx.virusM, 1
            ));

            requirements(Category.crafting, with(
                    Itemsx.silica, 75,
                    Itemsx.silicaSand, 25
            ));
        }});

        //walls
        silicaWall = add(new ModBlock("silica-wall") {{
           health = 2235;
           size = 1;

           canBurn = false;
           requirements(Category.defense, with(
                   Itemsx.silica, 6
           ));
        }});

        largeSilicaWall = add(new ModBlock("silica-wall-large") {{
            health = 3549;
            size = 2;

            canBurn = false;
            requirements(Category.defense, with(
                    Itemsx.silica, 24
            ));
        }});

        virusMWall = add(new ModBlock("m-virus-wall") {{
            health = 2860;
            size = 1;

            dynamicEffectChange = 0.016f;
            dynamicEffect = Effects.virusMEffect;

            requirements(Category.defense, with(
                    Itemsx.virusM, 6
            ));
        }});

        virusMWallLarge = add(new ModBlock("m-virus-wall-large") {{
            health = 4032;
            size = 2;

            dynamicEffectChange = 0.016f;
            dynamicEffect = Effects.virusMEffect;

            requirements(Category.defense, with(
                    Itemsx.virusM, 24
            ));
        }});
    }
}