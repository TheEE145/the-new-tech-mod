package the.mod.content;

import arc.graphics.*;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;
import the.mod.TheTech;
import the.mod.types.*;
import the.mod.utils.*;
import the.mod.utils.Types.*;
import the.mod.types.Lasers.*;

import static the.mod.types.Meteria.*;
import static mindustry.type.ItemStack.with;

public class Blocksx {
    public static final Seq<Block> all = new Seq<>();

    //defence
    public static ModBlock silicaWall, largeSilicaWall, virusMWall, virusMWallLarge;
    public static ModItemTurret silicaTurret;
    public static Minigun ares;

    //environment
    public static ModFloor virusMFloor, mantium, orangeIce, emethen, crystals;
    public static ModStaticWall virusMStaticWall, orangeIceWall, mantiumWall;
    public static ModOreBlock silicaOre;

    //energy
    public static MeteriaNode meteriaNode, largeMeteriaNode, sandboxMeteriaNode;
    public static MeteriaNodeBooster meteriaBooster;
    public static MeteriaSource meteriaSource;
    public static MeteriaCrafter coalMeteriaGenerator, emethenMeteriaGenerator;

    //distribution
    public static ModConveyor silicaConveyor;
    public static ModBridge silicaBridge;
    public static ModEnemy silicaRouter;

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

    //lasers
    public static Lasers.LaserMirror mirror, mirror135, mirror0;
    public static Lasers.LaserBlock laser, longLaser;
    public static LaserMultiMirror multiMirror;

    //other
    public static Other.SunGenerator sunGenerator;
    public static RadiusBlock sonicPulsar;

    public static <T extends Block> T add(T type) {
        all.add(type);
        TheTech.all.add(type);
        return type;
    }

    public static void load() {
        //turrets
        ares = add(new Minigun("ares") {{
            requirements(Category.turret, with(Items.copper, 1));

            health = 240;

            range = 310f;
            size = 4;

            shootY = 15f;
            rotateSpeed = 5f;
            consumeAmmoOnce = true;
            shootSound = Sounds.shootBig;

            drawer = new DrawTurret("redcon-") {{
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

        silicaTurret = add(new ModItemTurret("silica-turret") {{
            requirements(Category.turret, with(Itemsx.silica, 24));
            size = 1;
            health = 240;
            range = 310f;

            reload = 60;

            shootY = 4f;
            rotateSpeed = 5f;
            consumeAmmoOnce = true;
            shootSound = Sounds.shoot;

            drawer = new DrawTurret("redcon-") {{
               parts.add(
                       new RegionPart("-front") {{
                           moveY = -2f;
                           under = true;
                       }},

                       new RegionPart("-mid") {{
                           moveY = -0.10f;
                           under = true;
                       }},

                       new RegionPart("-blade") {{
                           moveY = 0.25f;
                           under = true;
                       }}
               );
            }};

            ammo(
                    Itemsx.silica, new BasicBulletType() {{
                        damage = 12;
                        speed = 8.5f;

                        width = height = 8;
                        shrinkY = 0.3f;

                        collidesTiles = false;
                        frontColor = Color.white;
                        backColor = trailColor = hitColor = Color.white;
                        trailChance = 0.44f;

                        lifetime = 34f;
                        rotationOffset = 90f;
                    }},

                    Itemsx.silicaSand, new BasicBulletType() {{
                        damage = 9;
                        speed = 8.5f;

                        width = height = 12;
                        shrinkY = 0.3f;

                        collidesTiles = false;
                        frontColor = Color.white;
                        backColor = trailColor = hitColor = Color.white;
                        trailChance = 0.44f;

                        lifetime = 34f;
                        rotationOffset = 90f;

                        shootCone = 40f;
                        fragBullets = 7;

                        fragBullet = new BasicBulletType() {{
                            damage = 5;
                            speed = 6.5f;

                            width = height = 6;
                            shrinkY = 0.3f;

                            collidesTiles = true;
                            frontColor = Color.white;
                            backColor = trailColor = hitColor = Color.white;
                            trailChance = 0.44f;

                            lifetime = 16f;
                            rotationOffset = 90f;
                        }};
                    }},

                    Itemsx.virusM, new BasicBulletType() {{
                        damage = 6;
                        speed = 8.5f;

                        width = height = 7;
                        shrinkY = 0.3f;

                        collidesTiles = false;
                        frontColor = ThePal.virusM;
                        backColor = trailColor = hitColor = Color.blue;
                        trailChance = 0.44f;

                        lifetime = 34f;
                        rotationOffset = 90f;
                        status = Statuses.virus1stage;
                    }}
            );

            itemCapacity = 10;
            researchCost = with();
        }});

                //meteria nodes
        meteriaNode = add(new MeteriaNode("meteria-node") {{
            maxMeteria = 10000;
            radius = 200;

            size = 2;

            requirements(Category.power, with(
                    Itemsx.silica, 25
            ));
        }});

        largeMeteriaNode = add(new MeteriaNode("large-meteria-node") {{
            maxMeteria = 50000;
            radius = 400;

            size = 3;

            requirements(Category.power, with(
                    Itemsx.silica, 125
            ));
        }});

        sandboxMeteriaNode = add(new MeteriaNode("sandbox-meteria-node") {{
            maxMeteria = 900000;
            radius = 8000;
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

        emethenMeteriaGenerator = add(new MeteriaCrafter("emethen-meteria-generator") {{
            meteriaGet = 500;
            maxMeteria = 2000;
            size = 3;

            craftEffect = Effects.craftMeteria;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidRegion(),
                    new DrawDefault(),
                    new DrawRegion("-top")
            );

            hasLiquids = true;
            liquidCapacity = 20;

            consumeLiquid(Liquids.emethen, 0.26f);

            requirements(Category.power, with(
                    Itemsx.silica, 40,
                    Itemsx.silicaSand, 50,
                    Itemsx.virusM, 20
            ));
        }});

        //cores
        terra = add(new ModCore("core-terra") {{
            requirements(Category.effect, BuildVisibility.editorOnly, with(Itemsx.silica, 1000));
            alwaysUnlocked = true;

            isFirstTier = true;
            unitType = UnitTypes.alpha;
            health = 3500;
            itemCapacity = 4000;
            size = 4;

            unitCapModifier = 8;
            shieldReload = 600f;
            shieldHp = 2000f;
        }});

        //drills
        silicaDrill = add(new ModDrill("silica-drill") {{
            requirements(Category.production, with(Itemsx.silica, 12));

            tier = 2;
            drillTime = 600f;
            size = 2;

            consumeLiquid(Liquids.emethen, 0.05f).boost();
            researchCost = with();
        }});

        updatedDrill = add(new ModDrill("updated-drill") {{
            requirements(Category.production, with(Itemsx.silica, 24));

            tier = 3;
            drillTime = 400f;
            size = 3;

            consumeLiquid(Liquids.emethen, 0.06f).boost();
        }});

        meteriaDrill = add(new MeteriaDrill("meteria-drill") {{
            requirements(Category.production, with(Itemsx.silica, 72));

            meteriaConsume = 2;
            maxMeteria = 500;
            size = 4;

            drillTime = 280f;
            tier = 4;

            consumeLiquid(Liquids.emethen, 0.08f).boost();
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

        //redcon
        virusMStaticWall = add(new ModStaticWall("m-virus-wall-static") {{
            variants = 3;
            mapColor = Color.gray;

            itemDrop = Itemsx.virusM;
        }});

        mantiumWall = add(new ModStaticWall("mantium-wall") {{
            variants = 2;
            mapColor = Color.gray;
        }});

        mantium = add(new ModFloor("mantium", 3) {{
            wall = mantiumWall;
        }});

        orangeIceWall = add(new ModStaticWall("orange-ice-wall") {{
            variants = 2;
        }});

        orangeIce = add(new ModFloor("orange-ice", 3) {{
            wall = orangeIceWall;
        }});

        virusMFloor = add(new ModFloor("m-virus", 3) {{
            itemDrop = Itemsx.virusM;
            wall = virusMStaticWall;
        }});

        emethen = add(new ModFloor("emethen-floor") {{
            speedMultiplier = 0.3f;
            variants = 0;
            statusDuration = 6f;
            supportsOverlay = true;
            drownTime = 210f;
            albedo = 0.9f;
            isLiquid = true;
            liquidDrop = Liquids.emethen;
            liquidMultiplier = 1.5f;

            cacheLayer = CacheLayer.slag;
        }});

        crystals = add(new ModFloor("crystals", 3) {{
            wall = virusMStaticWall;
        }});

        silicaOre = add(new ModOreBlock("ore-silica-crystal", Itemsx.silica) {{
            variants = 3;
        }});

        //distribution
        silicaConveyor = add(new ModConveyor("silica-conveyor") {{
            size = 1;

            health = 45;
            speed = 0.03f;
            displayedSpeed = 4.2f;
            buildCostMultiplier = 2f;

            requirements(Category.distribution, with(
                    Itemsx.silica, 1
            ));

            researchCost = with();
        }});

        silicaRouter = add(new ModEnemy("silica-router") {{
            requirements(Category.distribution, with(Itemsx.silica, 3));
            buildCostMultiplier = 4f;
        }});

        silicaBridge = add(new ModBridge("silica-bridge-conveyor") {{
            requirements(Category.distribution, with(Itemsx.silica, 6, Itemsx.virusM, 6));
            fadeIn = moveArrows = false;
            range = 5;
            arrowSpacing = 6f;
        }});

        //lasers
        mirror0 = add(new LaserMirror("mirror-0") {{
            health = 140;
            offset = 0;

            rotate = false;
            conveyorPlacement = false;

            requirements(Category.effect, with(
                    Itemsx.silica, 2
            ));
        }});

        mirror = add(new LaserMirror("mirror") {{
            health = 140;
            offset = 90;

            requirements(Category.effect, with(
                    Itemsx.silica, 2
            ));
        }});

        mirror135 = add(new LaserMirror("mirror-135") {{
            health = 140;
            offset = 135;

            requirements(Category.effect, with(
                    Itemsx.silica, 2
            ));
        }});

        multiMirror = add(new LaserMultiMirror("mirror-all") {{
            health = 150;

            requirements(Category.effect, with(
                    Itemsx.silica, 4
            ));
        }});

        laser = add(new LaserBlock("laser") {{
            health = 200;

            hasItems = true;
            itemCapacity = 5;
            mineable = true;

            lasers = 7;
            laserAlpha = 1f;
            laserColor = Color.red;
            laserStroke = 1f;
            laserRadius = 80f;
            drawTargetTile = false;

            endEffect = Effects.laserEndEffect;
            startEffect = Effects.laserStartEffect;

            requirements(Category.effect, with(
                    Itemsx.silica, 5
            ));
        }});

        longLaser = add(new LaserBlock("long-laser") {{
            health = 250;

            lasers = 14;
            laserAlpha = 0.85f;
            laserColor = Color.purple;
            laserStroke = 0.75f;
            laserRadius = 160f;
            drawTargetTile = false;

            endEffect = Effects.longLaserEndEffect;
            startEffect = Effects.laserStartEffect;

            requirements(Category.effect, with(
                    Itemsx.silica, 10
            ));
        }});

        //other
        sonicPulsar = add(new RadiusBlock("sonic-pulsar") {{
            health = 200;
            size = 2;

            radius = 56;
            colorByTeam = true;

            after = (build) -> {
                for(Unit unit : build.units()) {
                    unit.apply(Statuses.sonicPulse, Timer.second8);
                }
            };

            requirements(Category.effect, with(
                    Itemsx.silica, 25,
                    Itemsx.silicaSand, 50,
                    Itemsx.virusM, 10
            ));
        }});

        sunGenerator = add(new Other.SunGenerator("sun-generator") {{
            health = 1000;
            size = 4;

            buildVisibility = BuildVisibility.sandboxOnly;
            requirements(Category.effect, with());
        }});
    }
}