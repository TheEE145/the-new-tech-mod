package the.mod.utils;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.TankUnitType;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.*;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Stat;
import the.mod.TheTech;
import the.mod.types.Lasers;

import static arc.Core.*;
import static the.mod.TheTech.*;
import static mindustry.Vars.*;

public class Types {
    public static class ModItemTurret extends ItemTurret {
        public ModItemTurret(String name) {
            super(name);

            localizedName = prefix(localizedName);
            consumeLiquid(the.mod.content.Liquids.emethen, 0.25f).boost();
        }

        public class ModItemTurretBuild extends ItemTurretBuild {
        }
    }

    public static class ModCore extends CoreBlock {
        public float shieldReload = 60f;
        public float shieldHp = 1;

        public ModCore(String name) {
            super(name);
            localizedName = prefix(localizedName);
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("shield", (ModCoreBuild build) -> new Bar(
                        () -> build.shield.isBroken() ? bundle.get("shield.progress") : bundle.get("shield.health"),
                        () -> build.shield.isBroken() ? Color.orange : Color.cyan,
                        () -> build.shield.isBroken() ? build.shield.progress() : build.shield.health()
            ));
        }

        public class ModCoreBuild extends CoreBuild {
            public Shield shield;

            public ModCoreBuild() {
                shield = new Shield() {{
                    maxHealth = shieldHp;
                    reloadTime = shieldReload;
                }};
            }

            @Override
            public void draw() {
                super.draw();
                if(shield.region == null) {
                    shield.region = mod("shield-" + size);
                }

                shield.renderer(x, y);
            }

            @Override
            public void damage(Team source, float damage) {
                super.damage(source, shield.damage(damage));
            }
        }
    }

    public static class ModCrafter extends GenericCrafter {
        public ModCrafter(String name) {
            super(name);

            localizedName = prefix(localizedName);
        }

        @Override
        public void load() {
            super.load();

            if(atlas.has(name + "-preview")) {
                uiIcon = get(name + "-preview");
            }
        }

        @Override
        public void setStats() {
            super.setStats();

            stats.remove(Stat.input);
            stats.remove(Stat.output);
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("progress", (ModCrafterBuild b) -> {
                return new Bar(
                        () -> "progress",
                        () -> Pal.bar,
                        () -> b.progress
                );
            });
        }

        public class ModCrafterBuild extends GenericCrafterBuild {
        }
    }

    public static class ModBlock extends Wall {
        public float dynamicEffectChange = 0f;
        public Effect dynamicEffect = Fx.none;
        public boolean canBurn = true;
        public Drawer.Plan[] drawer;

        public ModBlock(String name) {
            super(name);

            localizedName = prefix(localizedName);
            squareSprite = false;

            flashHit = false;
            update = true;
        }

        @Override
        public void load() {
            super.load();

            if(atlas.has(name + "-preview")) {
                uiIcon = get(name + "-preview");
            }
        }

        public class ModBlockBuild extends WallBuild {
            public Drawer.MultiDrawer localDrawer = new Drawer.MultiDrawer();
            private boolean loaded = false;

            public ModBlockBuild() {
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

    public static class ModItem extends Item {
        public ModItem(String name, Color color) {
            super(name, color);

            localizedName = prefix(localizedName);
        }
    }

    public static class ModDrill extends Drill {
        public ModDrill(String name) {
            super(name);

            localizedName = prefix(localizedName);

            hasItems = true;
            itemCapacity = 15;
        }

        public class ModDrillBuild extends DrillBuild {
        }
    }

    public static class LiquidUnloader extends ModBlock {
        public TextureRegion centerRegion;
        public Seq<Tile> nearbyBlocks;

        public LiquidUnloader(String name) {
            super(name);
            update = solid = hasLiquids = configurable = saveConfig = noUpdateDisabled = true;

            health = 70;
            liquidCapacity = 0;

            config(Liquid.class, (LiquidUnloaderBuild tile, Liquid liquid) -> tile.config = liquid);
            configClear((LiquidUnloaderBuild build) -> build.config = Liquids.water);
        }

        @Override
        public void load() {
            super.load();
            centerRegion = get(name + "-center");
        }

        @Override
        public void setBars(){
            super.setBars();
            removeBar("liquid");
            removeBar("liquid-");
        }

        public class LiquidUnloaderBuild extends ModBlockBuild {
            public Liquid config = Liquids.water;

            @Override
            public void draw() {
                super.draw();

                if (config == null) {
                    config = Liquids.water;
                }

                Draw.draw(Layer.block, () -> {
                    Draw.color(config.color);
                    Draw.rect(centerRegion, x, y);
                    Draw.flush();
                });

                nearbyBlocks();
                float total = 0;
                for (Tile tile : requireNotConduit()) {
                    total += tile.build.liquids.get(config);
                }

                total = total / nearbyBlocks.size;
                for (Tile t : nearbyBlocks) {
                    t.build.liquids.remove(config, t.build.liquids.get(config));
                    t.build.liquids.add(config, total);
                }

                dumpLiquid(config);
            }

            public boolean needLiquid(Block block) {
                for (Consume e : block.consumers) {
                    if (e instanceof ConsumeLiquid) {
                        if (((ConsumeLiquid) e).liquid == config) {
                            return true;
                        }
                    }
                }

                return false;
            }

            public boolean canBeUnloaded(Tile tile, int rotation) {
                if (tile == null) {
                    return false;
                }

                Block block = tile.block();
                if (block == null) {
                    return false;
                }

                if (block.hasLiquids) {
                    if (block instanceof Conduit) {
                        Building build = tile.build;

                        if (build == null) {
                            return false;
                        }

                        return rotation != build.rotation;
                    }

                    return !(block instanceof LiquidUnloader) && needLiquid(block);
                }

                return false;
            }

            public Seq<Tile> requireNotConduit() {
                Seq<Tile> result = new Seq<>();
                for (Tile b : nearbyBlocks) {
                    if (!(b.block() instanceof Conduit)) {
                        result.add(b);
                    }
                }

                return result;
            }

            public void nearbyBlocks() {
                nearbyBlocks = new Seq<>();
                Tile tile;

                tile = world.tile(tileX(), tileY() + 1);
                if (canBeUnloaded(tile, 3)) {
                    nearbyBlocks.add(tile);
                }

                tile = world.tile(tileX() + 1, tileY());
                if (canBeUnloaded(tile, 2)) {
                    nearbyBlocks.add(tile);
                }

                tile = world.tile(tileX(), tileY() - 1);
                if (canBeUnloaded(tile, 1)) {
                    nearbyBlocks.add(tile);
                }

                tile = world.tile(tileX() - 1, tileY());
                if (canBeUnloaded(tile, 0)) {
                    nearbyBlocks.add(tile);
                }
            }

            @Override
            public Liquid config() {
                return config;
            }

            public void configureERR(Object value) {
                super.configure(value);
            }

            @Override
            public byte version() {
                return 2;
            }

            @Override
            public void write(Writes write) {
                super.write(write);
                write.i(config.id);
            }

            @Override
            public void read(Reads read, byte revision) {
                super.read(read, revision);
                config = content.liquid(read.i());
            }

            @Override
            public void buildConfiguration(Table table) {
                ItemSelection.buildTable(LiquidUnloader.this, table, content.liquids(), () -> config, this::configureERR);
            }
        }
    }

    public static class ModFloor extends Floor {
        public ModFloor(String name) {
            super(name);
            localizedName = prefix(localizedName);
        }

        public ModFloor(String name, int variants) {
            super(name, variants);
            localizedName = prefix(localizedName);
        }
    }

    public static class ModStaticWall extends StaticWall {
        public Item itemDrop;

        public ModStaticWall(String name) {
            super(name);
            localizedName = prefix(localizedName);
        }
    }

    public static class ModLiquid extends Liquid {
        public ModLiquid(String name, Color color) {
            super(name, color);
            localizedName = prefix(localizedName);
        }
    }

    public static class ModOreBlock extends OreBlock {
        public ModOreBlock(String name, Item ore) {
            super(name, ore);
            localizedName = prefix(localizedName);
        }
    }

    public static class ModConveyor extends Conveyor {
        public ModConveyor(String name) {
            super(name);
            localizedName = prefix(localizedName);
        }

        public class ModConveyorBuild extends ConveyorBuild {
        }
    }

    public static class ModEnemy extends Router {
        public ModEnemy(String name) {
            super(name);
            localizedName = prefix(localizedName);
        }

        public class ModEnemyBuild extends RouterBuild {
        }
    }

    public static class ModStatusEffect extends StatusEffect {
        public Cons<Unit> onTimeEnd;

        public void damage(float damage) {
            this.damage = damage/60;
        }

        public ModStatusEffect(String name) {
            super(name);
            localizedName = prefix(localizedName);
        }

        @Override
        public void update(Unit unit, float time) {
            super.update(unit, time);

            if((time <= 2f) && (onTimeEnd != null)) {
                onTimeEnd.get(unit);
            }
        }
    }

    public static class ModBridge extends ItemBridge {
        public ModBridge(String name) {
            super(name);

            localizedName = prefix(localizedName);
        }
    }

    public static class ModUnitType extends UnitType {
        public boolean helicopter, ground, legs, tank;
        public boolean helicopterEnginesEnabled = false;

        public TextureRegion topRegion, rotorRegion;
        public float rotorSpeed = 15;
        public Rotor[] rotors;

        public ModUnitType(String name) {
            super(name);

            localizedName = prefix(localizedName);
            outlineColor = Pal.darkOutline;

            tacker.add(this);
        }

        public void color(Color color) {
            trailColor = engineColor = color;
            trailLength = 24;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void load() {
            super.load();

            if(helicopter) {
                topRegion = TheTech.get(name + "-top");
                rotorRegion = TheTech.get(name + "-rotor");

                if(!helicopterEnginesEnabled) {
                    engines = Seq.with();
                    engineSize = 0;
                }
            }

            if(ground) {
                constructor = MechUnit::create;
            }

            if(legs) {
                constructor = LegsUnit::create;
            }

            if(tank) {
                constructor = TankUnit::create;
            }

            if(!ground && !legs && !tank) {
                constructor = EntityMapping.map(3);
            }
        }

        public float layer(Unit unit) {
            if(unit == null) {
                return Layer.flyingUnit;
            }

            return unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
        }

        ItemStack[] requirements = ItemStack.empty;
        public void researchRequirements(ItemStack[] items) {
            requirements = items;
        }

        @Override
        public ItemStack[] researchRequirements() {
            return requirements;
        }

        @Override
        public void draw(Unit unit) {
            super.draw(unit);

            if(helicopter && rotors != null) {
                float rot = unit.rotation;
                Draw.draw(layer(unit), () -> {
                    for(Rotor r : rotors) {
                        float xx = Lasers.thx(rot, r.x);
                        float xy = Lasers.thy(rot, r.y);

                        float yx = Lasers.thx(rot, r.y);
                        float yy = Lasers.thy(rot, r.y);

                        Draw.rect(
                                rotorRegion,

                                unit.x + xx + yx,
                                unit.y + xy + yy,

                                rotorRegion.width/(4f + r.small),
                                rotorRegion.height/(4f + r.small),

                                r.rotation
                        );

                        Draw.rect(topRegion, unit.x + xx + yx, unit.y + xy + yy);
                    }
                });
            }
        }

        @Override
        public void createIcons(MultiPacker packer) {
            try {
                super.createIcons(packer);
            } catch(ArcRuntimeException ignored) {
            }
        }

        public void tact() {
            if(helicopter && rotors != null) {
                for(Rotor r : rotors) {
                    r.tack(rotorSpeed);
                }
            }
        }

        @Override
        public void update(Unit unit) {
            super.update(unit);
        }

        public static class Rotor extends Tacker.RotationTicker {
            public Rotor() {
                super(0);
            }

            public float small = 0f;
            public float x, y = x = 0;
        }
    }

    public static class ModWeapon extends Weapon {
        public ModWeapon(String name) {
            super(modId + "-" + name);
        }
    }

    public static class ModB extends Block {
        public ModB(String name) {
            super(name);

            localizedName = prefix(localizedName);
        }
    }

    public static class Buff extends UnlockableContent {
        private ItemStack[] requirements = ItemStack.empty;

        public static final Seq<Buff> buffs = new Seq<>();
        public Cons<UnlockableContent> handler;
        public boolean inited = false;

        public void requirements(Object... args) {
            requirements = ItemStack.with(args);
        }

        @Override
        public ItemStack[] researchRequirements() {
            return requirements;
        }

        public void handler(UnlockableContent c) {
            if(handler != null) {
                handler.get(c);
            }
        }

        public Buff(String name) {
            super(name);

            buffs.add(this);
        }

        public void activate() {
            if(inited) {
                return;
            }

            all.each(this::handler);
            inited = true;
        }

        @Override
        public void onUnlock() {
            activate();
        }

        @Override
        public ContentType getContentType() {
            return ContentType.error;
        }
    }

    public static class ModFactory extends UnitFactory {
        public ModFactory(String name) {
            super(name);

            localizedName = prefix(localizedName);
        }

        public class ModFactoryBuild extends UnitFactoryBuild {
            @Override
            public void draw() {
                Draw.rect(region, x, y);
                Draw.rect(outRegion, x, y, rotdeg());

                if(currentPlan != -1){
                    UnitPlan plan = plans.get(currentPlan);
                    Draw.draw(Layer.blockOver, () -> Drawf.construct(this, plan.unit, 0, progress / plan.time, speedScl, time));
                }

                Draw.z(Layer.blockOver);

                payRotation = rotdeg();
                drawPayload();

                Draw.z(Layer.blockOver + 0.1f);

                Draw.rect(topRegion, x, y);
            }

            @Override
            public boolean acceptPayload(Building source, Payload payload) {
                return super.acceptPayload(source, payload);
            }
        }
    }
}