package tntm.content;

import arc.graphics.*;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.*;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;
import tntm.TheTech;
import tntm.brutality.Corpse;
import tntm.graphics.drawer.PlanDrawer;
import tntm.utils.TntmBullets;
import tntm.graphics.TntmPal;
import tntm.utils.TntmTimer;

import tntm.world.blocks.ItemButBlock;
import tntm.world.blocks.RadiusBlock;
import tntm.world.blocks.craft.MultiCrafter;
import tntm.world.blocks.craft.meta.CraftPlan;
import tntm.world.blocks.distribution.TntmBridge;
import tntm.world.blocks.distribution.TntmConveyor;
import tntm.world.blocks.distribution.TntmRouter;
import tntm.world.blocks.enviroment.CrystalBlock;
import tntm.world.blocks.enviroment.TntmFloor;
import tntm.world.blocks.enviroment.TntmOreBlock;
import tntm.world.blocks.enviroment.TntmStaticWall;
import tntm.world.blocks.lasers.LaserBlock;
import tntm.world.blocks.lasers.LaserMirror;
import tntm.world.blocks.lasers.LaserMultiMirror;
import tntm.world.blocks.liquids.LiquidUnloader;
import tntm.world.blocks.liquids.TntmConduit;
import tntm.world.blocks.liquids.TntmPump;
import tntm.world.blocks.logic.ProcessorSpeedUpBlock;
import tntm.world.blocks.meteria.*;
import tntm.world.blocks.production.AllSource;
import tntm.world.blocks.production.CrystalCrasher;
import tntm.world.blocks.production.MeteriaDrill;
import tntm.world.blocks.production.TntmDrill;
import tntm.world.blocks.storage.TntmCore;
import tntm.world.blocks.turrets.Minigun;
import tntm.world.blocks.turrets.PayloadTurret;
import tntm.world.blocks.turrets.TntmItemTurret;
import tntm.world.blocks.units.TntmFactory;
import tntm.world.blocks.walls.*;
import tntm.world.bullets.BlockBulletType;

import static mindustry.type.ItemStack.with;

public class TntmBlocks {
    public static final Seq<Block> all = new Seq<>();
    public static Corpse corpse;

    //defence
    public static TntmBlock silicaWall, largeSilicaWall, virusMWall, virusMWallLarge;
    public static JoinWall magmaWall;
    public static UnbreakableWall unbreakableWall;
    public static DPSBlock dpsBlock;
    public static TntmItemTurret silicaTurret, scretch, amot;
    public static PayloadTurret route, rapita;
    public static Minigun ares;

    //ammo
    public static ItemButBlock basicBomb, clasterBomb, basicNuke, clasterNuke;

    //environment
    public static TntmFloor virusMFloor, mantium, orangeIce, emethen, crystals;
    public static TntmStaticWall virusMStaticWall, orangeIceWall, mantiumWall;
    public static TntmOreBlock silicaOre;
    public static CrystalBlock virusMCrystal, silicaCrystal;

    //energy
    public static MeteriaNode meteriaNode, largeMeteriaNode, sandboxMeteriaNode;
    public static MeteriaNodeBooster meteriaBooster;
    public static MeteriaSource meteriaSource;
    public static MeteriaCrafter coalMeteriaGenerator, emethenMeteriaGenerator;

    //distribution
    public static TntmConveyor silicaConveyor;
    public static TntmBridge silicaBridge;
    public static TntmRouter silicaRouter;

    //drills
    public static MeteriaDrill meteriaDrill, largeDrill, nuclearDrill;
    public static TntmDrill silicaDrill, updatedDrill;

    //liquids
    public static TntmPump basicPump;
    public static LiquidUnloader liquidUnloader;
    public static TntmConduit eminiumConduit;

    //crafters
    public static MeteriaPlant magmaFabric;
    public static MultiCrafter crusher;

    //cores
    public static TntmCore terra;

    //lasers
    public static LaserMirror mirror, mirror135, mirror0;
    public static LaserBlock laser, longLaser;
    public static LaserMultiMirror multiMirror;

    //logic
    public static ProcessorSpeedUpBlock cooler;

    //other
    public static RadiusBlock sonicPulsar;
    public static CrystalCrasher crasher;
    public static AllSource allSource;

    //units
    public static TntmFactory baseFactory, updatedFactory;

    public static <T extends Block> T add(T type) {
        all.add(type);
        TheTech.all.add(type);
        return type;
    }

    public static void load() {
        corpse = new Corpse("YRL39_X5");

        //ammo
        basicBomb = add(new ItemButBlock("basicBomb") {{
            size = 2;
        }});

        clasterBomb = add(new ItemButBlock("clasterBomb") {{
            size = 2;
        }});

        basicNuke = add(new ItemButBlock("basicNuke") {{
            size = 3;
        }});

        clasterNuke = add(new ItemButBlock("clasterNuke") {{
            size = 3;
        }});

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

                despawnEffect = hitEffect = TntmFx.bulletCollision;
            }});
        }});

        silicaTurret = add(new TntmItemTurret("silica-turret") {{
            requirements(Category.turret, with(TntmItems.silica, 24));
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
                    TntmItems.silica, new BasicBulletType() {{
                        TntmBullets.setup(this);
                        damage = 12;
                        speed = 8.5f;

                        width = height = 8;
                        shrinkY = 0.3f;

                        collidesTiles = false;
                        frontColor = Color.white;
                        backColor = trailColor = hitColor = Color.white;

                        lifetime = 34f;
                        rotationOffset = 90f;
                    }},

                    TntmItems.silicaSand, new BasicBulletType() {{
                        TntmBullets.setup(this);
                        damage = 9;
                        speed = 8.5f;

                        width = height = 12;
                        shrinkY = 0.3f;

                        collidesTiles = false;
                        frontColor = Color.white;
                        backColor = trailColor = hitColor = Color.white;

                        lifetime = 34f;
                        rotationOffset = 90f;

                        shootCone = 40f;
                        fragBullets = 7;

                        fragBullet = new BasicBulletType() {{
                            TntmBullets.setup(this);
                            damage = 5;
                            speed = 6.5f;

                            width = height = 6;
                            shrinkY = 0.3f;

                            collidesTiles = true;
                            frontColor = Color.white;
                            backColor = trailColor = hitColor = Color.white;

                            lifetime = 16f;
                            rotationOffset = 90f;
                        }};
                    }},

                    TntmItems.virusM, new BasicBulletType() {{
                        TntmBullets.setup(this, TntmPal.virusM);
                        damage = 6;
                        speed = 8.5f;

                        width = height = 7;
                        shrinkY = 0.3f;

                        collidesTiles = false;
                        frontColor = TntmPal.virusM;
                        backColor = trailColor = hitColor = Color.blue;

                        lifetime = 34f;
                        rotationOffset = 90f;
                        status = TntmStatusEffects.virus1stage;
                    }}
            );

            itemCapacity = 10;
            researchCost = with();
        }});

        scretch = add(new TntmItemTurret("scretch") {{
            requirements(Category.turret, with(
                    TntmItems.silica, 24,
                    TntmItems.virusM, 12
            ));

            size = 1;
            health = 300;
            range = 155;
            reload = 4;
            recoil = 0f;
            coolantMultiplier = 1.5f;
            shootCone = 50f;
            targetAir = false;
            ammoUseEffect = Fx.none;
            shootSound = Sounds.flame;
            coolant = consumeCoolant(0.1f);

            shootY = 8f;
            rotateSpeed = 5f;
            consumeAmmoOnce = true;
            shootSound = Sounds.shoot;

            drawer = new DrawTurret("redcon-") {{
                parts.add(
                        new RegionPart("-bolt") {{
                            moveY = 2.7f;
                            under = true;
                        }},

                        new RegionPart("-mid") {{
                            moveY = -0.10f;
                            under = true;
                        }},

                        new RegionPart("-blades") {{
                            moveY = 2f;
                            under = true;
                        }}
                );
            }};

            ammo(
                    Items.coal, new BulletType(5f, 17f) {{
                        ammoMultiplier = 3f;
                        hitSize = 7f;
                        lifetime = 30f;
                        pierce = true;
                        collidesAir = false;
                        statusDuration = 60f * 4;
                        shootEffect = Fx.shootSmallFlame;
                        hitEffect = despawnEffect = Fx.hitFlameSmall;
                        trailEffect = Fx.shootSmallFlame;
                        trailColor = Pal.bulletYellow;
                        trailChance = 1;
                        status = StatusEffects.burning;
                        keepVelocity = false;
                        hittable = false;
                    }},

                    TntmItems.coalSand, new BulletType(5f, 35f){{
                        ammoMultiplier = 6f;
                        hitSize = 7f;
                        lifetime = 30f;
                        pierce = true;
                        collidesAir = false;
                        statusDuration = 60f * 4;
                        shootEffect = Fx.shootSmallFlame;
                        hitEffect = despawnEffect = Fx.hitFlameSmall;
                        trailEffect = Fx.shootSmallFlame;
                        trailColor = Pal.bulletYellow;
                        trailChance = 1;
                        status = StatusEffects.burning;
                        keepVelocity = false;
                        hittable = false;
                    }}
            );
        }});

        amot = add(new TntmItemTurret("amot") {{
            size = 2;
            health = 200;
            reload = 60;

            range = 300;
            shoot = new ShootSpread() {{
                shots = 5;
            }};

            drawer = new DrawTurret("redcon-") {{
                parts.add(
                        new RegionPart("-bolt") {{
                            moveY = -1f;
                            under = true;
                        }},

                        new RegionPart("-back") {{
                            moveY = -0.10f;
                            under = true;
                        }}
                );
            }};

            ammo(
                    TntmItems.silicaSand, new BasicBulletType(4, 20) {{
                        TntmBullets.setup(this);

                        lifetime *= 2;
                        hitColor = frontColor = backColor = Color.white;
                    }}
            );

            requirements(Category.turret, with(
                    TntmItems.silica, 40,
                    TntmItems.virusM, 20
            ));
        }});

        route = add(new PayloadTurret("route") {{
            shootEffect = Fx.shootTitan;
            smokeEffect = Fx.shootSmokeTitan;

            dynamicBullet = true;
            health = 1356;
            range = 648;
            reload = 120;

            ammo(
                    basicBomb, new BlockBulletType(basicBomb) {{
                        lifetime = TntmTimer.second8;
                        speed = 2;

                        splashDamage = 900;
                        splashDamageRadius = 40;
                    }},

                    clasterBomb, new BlockBulletType(clasterBomb) {{
                        lifetime = TntmTimer.second8;
                        speed = 2;

                        hitEffect = despawnEffect = Fx.none;

                        splashDamageRadius = 40;
                        fragBullets = 15;

                        fragOffset = 40;
                        fragBullet = new BlockBulletType(clasterBomb) {{
                            lifetime = TntmTimer.second8 / 2;
                            speed = 3;

                            splashDamage = 700;
                            splashDamageRadius = 20;

                            startScale /= 2;
                            maxScale /= 2;
                        }};
                    }}
            );

            size = 3;
            requirements(Category.turret, with());
        }});

        rapita = add(new PayloadTurret("rapita") {{
            shootEffect = Fx.shootTitan;
            smokeEffect = Fx.shootSmokeTitan;

            dynamicBullet = true;
            health = 2656;
            range = 648 * 2;
            reload = 240;

            ammo(
                    basicNuke, new BlockBulletType(basicNuke) {{
                        lifetime = TntmTimer.second8 * 2;
                        speed = 2;

                        splashDamage = 1800;
                        splashDamageRadius = 80;

                        hitEffect = despawnEffect = TntmFx.largeExplosion;
                    }},

                    clasterNuke, new BlockBulletType(clasterNuke) {{
                        lifetime = TntmTimer.second8 * 2;
                        speed = 2;

                        hitEffect = despawnEffect = Fx.none;

                        splashDamageRadius = 80;
                        fragBullets = 15;

                        fragOffset = 80;
                        fragBullet = new BlockBulletType(clasterNuke) {{
                            lifetime = TntmTimer.second8;
                            speed = 3;

                            splashDamage = 1400;
                            splashDamageRadius = 40;

                            startScale /= 2;
                            maxScale /= 2;

                            hitEffect = despawnEffect = TntmFx.largeExplosion;
                        }};
                    }}
            );

            size = 5;
            requirements(Category.turret, with());
        }});


                //meteria nodes
        meteriaNode = add(new MeteriaNode("meteria-node") {{
            maxMeteria = 10000;
            radius = 200;

            size = 2;

            requirements(Category.power, with(
                    TntmItems.silica, 25
            ));
        }});

        largeMeteriaNode = add(new MeteriaNode("large-meteria-node") {{
            maxMeteria = 50000;
            radius = 400;

            size = 3;

            requirements(Category.power, with(
                    TntmItems.silica, 125
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
                    TntmItems.silica, 75
            ));
        }});

        meteriaSource = add(new MeteriaSource("meteria-source") {{
            category = Category.power;
            buildVisibility = BuildVisibility.sandboxOnly;
        }});

        coalMeteriaGenerator = add(new MeteriaCrafter("fuel-meteria-generator") {{
            meteriaGet = 100;
            maxMeteria = 1000;

            craftEffect = TntmFx.craftMeteria;

            hasItems = true;
            itemCapacity = 10;

            consumeItems(with(
                    Items.coal, 1
            ));

            requirements(Category.power, with(
                    TntmItems.silica, 25
            ));
        }});

        emethenMeteriaGenerator = add(new MeteriaCrafter("emethen-meteria-generator") {{
            meteriaGet = 500;
            maxMeteria = 2000;
            size = 3;

            craftEffect = TntmFx.craftMeteria;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidRegion(),
                    new DrawDefault(),
                    new DrawRegion("-top")
            );

            hasLiquids = true;
            liquidCapacity = 20;

            consumeLiquid(TntmLiquids.emethen, 0.26f);

            requirements(Category.power, with(
                    TntmItems.silica, 40,
                    TntmItems.silicaSand, 50,
                    TntmItems.virusM, 20
            ));
        }});

        //cores
        terra = add(new TntmCore("core-terra") {{
            requirements(Category.effect, BuildVisibility.editorOnly, with(TntmItems.silica, 1000));
            alwaysUnlocked = true;

            isFirstTier = true;
            unitType = TntmUnits.hellx;
            health = 3500;
            itemCapacity = 4000;
            size = 4;

            unitCapModifier = 8;
            shieldReload = 600f;
            shieldHp = 2000f;
        }});

        //drills
        silicaDrill = add(new TntmDrill("silica-drill") {{
            requirements(Category.production, with(TntmItems.silica, 12));

            tier = 2;
            drillTime = 600f;
            size = 2;

            consumeLiquid(TntmLiquids.emethen, 0.05f).boost();
            researchCost = with();
        }});

        updatedDrill = add(new TntmDrill("updated-drill") {{
            requirements(Category.production, with(TntmItems.silica, 24));

            tier = 3;
            drillTime = 400f;
            size = 3;

            consumeLiquid(TntmLiquids.emethen, 0.06f).boost();
        }});

        meteriaDrill = add(new MeteriaDrill("meteria-drill") {{
            requirements(Category.production, with(TntmItems.silica, 72));

            meteriaConsume = 2;
            maxMeteria = 500;
            size = 4;

            drillTime = 280f;
            tier = 4;

            consumeLiquid(TntmLiquids.emethen, 0.08f).boost();
        }});

        //liquids
        liquidUnloader = add(new LiquidUnloader("liquid-unloader") {{
            requirements(Category.liquid, with(
                    Items.titanium, 25,
                    Items.metaglass, 10
            ));

            size = 1;
            techNode = new TechTree.TechNode(Blocks.conduit.techNode, this, requirements);
        }});

        basicPump = add(new TntmPump("basic-pump") {{
            requirements(Category.liquid, with(TntmItems.silica, 15, TntmItems.virusM, 10));
            pumpAmount = 6f / 60f;
        }});

        eminiumConduit = add(new TntmConduit("eminium-conduit") {{
            requirements(Category.liquid, with());
            showLiquids = false;
            health = 45;
        }});

        //crafters
        crusher = add(new MultiCrafter("crusher") {{
            health = 300;
            size = 2;

            plans = Seq.with(
                    new CraftPlan(TntmItems.silicaSand, 2) {{
                        items = with(TntmItems.silica, 1);
                    }},

                    new CraftPlan(TntmItems.virusMSand, 2) {{
                        items = with(TntmItems.virusM, 1);
                        craftTime = 120;
                    }},

                    new CraftPlan(TntmItems.coalSand, 2) {{
                        items = with(Items.coal, 1);
                        craftTime = 30;
                    }}
            );

            requirements(Category.crafting, with(
                    TntmItems.silica, 75,
                    TntmItems.virusM, 25
            ));
        }});

        magmaFabric = add(new MeteriaPlant("magma-fabric") {{
            health = 400;
            size = 3;

            meteriaConsume = 100;
            maxMeteria = 500;
            craftTime = 90;

            requirements(Category.crafting, with(
                    TntmItems.silica, 125,
                    TntmItems.virusM, 75
            ));

            consumeLiquid(TntmLiquids.emethen, 0.2f);
            consumeItems(ItemStack.with(
                    TntmItems.silica, 3,
                    TntmItems.virusM, 6,
                    TntmItems.coalSand, 4
            ));

            outputItem = ItemStack.with(TntmItems.magmaAlloy, 3)[0];
        }});

        //walls
        silicaWall = add(new TntmBlock("silica-wall") {{
           health = 2235;
           size = 1;

           canBurn = false;
           requirements(Category.defense, with(
                   TntmItems.silica, 6
           ));
        }});

        largeSilicaWall = add(new TntmBlock("silica-wall-large") {{
            health = 3549;
            size = 2;

            canBurn = false;
            requirements(Category.defense, with(
                    TntmItems.silica, 24
            ));
        }});

        virusMWall = add(new TntmBlock("m-virus-wall") {{
            health = 2860;
            size = 1;

            dynamicEffectChange = 0.016f;
            dynamicEffect = TntmFx.virusMEffect;

            requirements(Category.defense, with(
                    TntmItems.virusM, 6
            ));
        }});

        virusMWallLarge = add(new TntmBlock("m-virus-wall-large") {{
            health = 4032;
            size = 2;

            dynamicEffectChange = 0.016f;
            dynamicEffect = TntmFx.virusMEffect;

            requirements(Category.defense, with(
                    TntmItems.virusM, 24
            ));
        }});

        magmaWall = add(new JoinWall("magma-wall") {{
            healthMove = true;
            health = 2000;
            size = 1;
            canBurn = false;

            requirements(Category.defense, with(
                    TntmItems.magmaAlloy, 6
            ));
        }});

        unbreakableWall = add(new UnbreakableWall("unbrekable-wall") {{
            buildVisibility = BuildVisibility.sandboxOnly;
            requirements(Category.defense, BuildVisibility.sandboxOnly, with());
        }});

        dpsBlock = add(new DPSBlock("dps-block") {{
            health = 1999999998;

            requirements(Category.defense, BuildVisibility.sandboxOnly, with());
        }});

        //redcon
        virusMStaticWall = add(new TntmStaticWall("m-virus-wall-static") {{
            variants = 3;
            mapColor = Color.gray;

            itemDrop = TntmItems.virusM;
        }});

        mantiumWall = add(new TntmStaticWall("mantium-wall") {{
            variants = 2;
            mapColor = Color.gray;
        }});

        mantium = add(new TntmFloor("mantium", 3) {{
            wall = mantiumWall;
        }});

        orangeIceWall = add(new TntmStaticWall("orange-ice-wall") {{
            variants = 2;
        }});

        orangeIce = add(new TntmFloor("orange-ice", 3) {{
            wall = orangeIceWall;
        }});

        virusMFloor = add(new TntmFloor("m-virus", 3) {{
            itemDrop = TntmItems.virusM;
            wall = virusMStaticWall;
        }});

        emethen = add(new TntmFloor("emethen-floor") {{
            speedMultiplier = 0.3f;
            variants = 0;
            statusDuration = 6f;
            supportsOverlay = true;
            drownTime = 210f;
            albedo = 0.9f;
            isLiquid = true;
            liquidDrop = TntmLiquids.emethen;
            liquidMultiplier = 1.5f;

            cacheLayer = CacheLayer.slag;
        }});

        crystals = add(new TntmFloor("crystals", 3) {{
            wall = virusMStaticWall;
        }});

        silicaOre = add(new TntmOreBlock("ore-silica-crystal", TntmItems.silica) {{
            variants = 3;
        }});

        virusMCrystal = add(new CrystalBlock("virus-m-crystal", TntmItems.virusM, 25) {{
            health = 1;
        }});

        silicaCrystal = add(new CrystalBlock("silica-crystal-2", TntmItems.silica, 25) {{
            health = 1;
        }});

        //distribution
        silicaConveyor = add(new TntmConveyor("silica-conveyor") {{
            size = 1;

            health = 45;
            speed = 0.03f;
            displayedSpeed = 4.2f;
            buildCostMultiplier = 2f;

            requirements(Category.distribution, with(
                    TntmItems.silica, 1
            ));

            researchCost = with();
        }});

        silicaRouter = add(new TntmRouter("silica-router") {{
            requirements(Category.distribution, with(TntmItems.silica, 3));
            buildCostMultiplier = 4f;
        }});

        silicaBridge = add(new TntmBridge("silica-bridge-conveyor") {{
            requirements(Category.distribution, with(TntmItems.silica, 6, TntmItems.virusM, 6));
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
                    TntmItems.silica, 2
            ));
        }});

        mirror = add(new LaserMirror("mirror") {{
            health = 140;
            offset = 90;

            requirements(Category.effect, with(
                    TntmItems.silica, 2
            ));
        }});

        mirror135 = add(new LaserMirror("mirror-135") {{
            health = 140;
            offset = 135;

            requirements(Category.effect, with(
                    TntmItems.silica, 2
            ));
        }});

        multiMirror = add(new LaserMultiMirror("mirror-all") {{
            health = 150;

            requirements(Category.effect, with(
                    TntmItems.silica, 4
            ));
        }});

        laser = add(new LaserBlock("laser") {{
            health = 200;

            hasItems = true;
            itemCapacity = 15;
            mineable = true;

            lasers = 7;
            laserAlpha = 1f;
            laserColor = Color.red;
            laserStroke = 1f;
            laserRadius = 80f;
            drawTargetTile = false;

            endEffect = TntmFx.laserEndEffect;
            startEffect = TntmFx.laserStartEffect;

            requirements(Category.effect, with(
                    TntmItems.silica, 5
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

            endEffect = TntmFx.longLaserEndEffect;
            startEffect = TntmFx.laserStartEffect;

            requirements(Category.effect, with(
                    TntmItems.silica, 10
            ));
        }});

        //logic
        cooler = add(new ProcessorSpeedUpBlock("processor-booster") {{
            consumeLiquidAmount = 17f / 60f;
            hasLiquids = true;
            health = 200;
            links = 6;
            size = 3;
            boost = 3;

            drawer = new PlanDrawer[] {
                    new PlanDrawer("-bottom", ""),
                    new PlanDrawer("-liquid", "liquid"),
                    new PlanDrawer("base"),
                    new PlanDrawer("-heat", "heat")
            };

            requirements(Category.logic, with(
                    TntmItems.silica, 50,
                    TntmItems.silicaSand, 25,
                    TntmItems.virusM, 30,
                    TntmItems.coalSand, 6
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
                    unit.apply(TntmStatusEffects.sonicPulse, TntmTimer.second8);
                }
            };

            requirements(Category.effect, with(
                    TntmItems.silica, 25,
                    TntmItems.silicaSand, 50,
                    TntmItems.virusM, 10
            ));

            hasLiquids = true;
            liquidCapacity = 48;
        }});

        crasher = add(new CrystalCrasher("crystal-crasher") {{
            length = 8;
            size = 2;

            health = 150;
            requirements(Category.production, with(
                    TntmItems.silica, 15
            ));
        }});

        allSource = add(new AllSource("all-source") {{
            health = Integer.MAX_VALUE;
            size = 1;

            laserRange = 800;
        }});

        //units
        baseFactory = add(new TntmFactory("baseFactory") {{
            size = 2;

            plans.add(
                    new UnitPlan(TntmUnits.trident, TntmTimer.second8, TntmUnits.trident.researchRequirements()),
                    new UnitPlan(TntmUnits.javelin, TntmTimer.second8 * 2, TntmUnits.javelin.researchRequirements())
            );

            requirements(Category.units, with(
                    TntmItems.silica, 50,
                    TntmItems.silicaSand, 20,
                    TntmItems.virusM, 30,
                    TntmItems.virusMSand, 10
            ));
        }});

        updatedFactory = add(new TntmFactory("updatedFactory") {{
            size = 2;

            plans.add(
                    new UnitPlan(TntmUnits.delta, TntmTimer.second8, TntmUnits.delta.researchRequirements()),
                    new UnitPlan(TntmUnits.tau, TntmTimer.second8 * 2, TntmUnits.tau.researchRequirements())
            );

            requirements(Category.units, with(
                    TntmItems.silica, 150,
                    TntmItems.virusM, 130,
                    TntmItems.magmaAlloy, 100
            ));
        }});

    }
}