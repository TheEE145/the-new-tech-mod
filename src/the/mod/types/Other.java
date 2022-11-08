package the.mod.types;

import arc.Core;
import arc.Events;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Blocks;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.modules.ItemModule;
import the.mod.TheTech;
import the.mod.content.Effects;
import the.mod.utils.*;

import static mindustry.Vars.content;
import static mindustry.Vars.world;
import static mindustry.type.ItemStack.with;

public class Other {
    public static class SunGenerator extends Types.ModBlock {
        public static void loadEvent() {
            Events.on(EventType.TapEvent.class, e -> {
                if(e.tile.build instanceof SunGeneratorBuild) {
                    TheTech.show("change color", d -> {
                        d.cont.pane(t -> {
                            t.slider(0, 1, 0.01f, (value) -> {
                                Drawx.Math.sunColor = new Color(value, Drawx.Math.sunColor.g, Drawx.Math.sunColor.b);
                            }).size(300f, 45f).row();

                            t.slider(0, 1, 0.01f, (value) -> {
                                Drawx.Math.sunColor = new Color(Drawx.Math.sunColor.r, value, Drawx.Math.sunColor.b);
                            }).size(300f, 45f).row();

                            t.slider(0, 1, 0.01f, (value) -> {
                                Drawx.Math.sunColor = new Color(Drawx.Math.sunColor.r, Drawx.Math.sunColor.g, value);
                            }).size(300f, 45f).row();

                            t.button("apply", d::hide).size(100f, 50f);
                        });
                    });
                }
            });
        }

        public SunGenerator(String name) {
            super(name);
        }

        public class SunGeneratorBuild extends Types.ModBlock.ModBlockBuild {
            float rotation = 0;

            @Override
            public void draw() {
                super.draw();

                if(Drawx.sun != null) {
                    Draw.color(Drawx.Math.sunColor);
                    Draw.alpha(0.55f);
                    Draw.rect(Drawx.sun, x, y, size*6, size*6, rotation);
                }

                rotation++;
                if(rotation >= 360) {
                    rotation = 0;
                }
            }

            @Override
            public void write(Writes write) {
                super.write(write);
                Color c = Drawx.Math.sunColor;
                write.f(c.r);
                write.f(c.g);
                write.f(c.b);
            }

            @Override
            public void read(Reads read, byte revision) {
                super.read(read, revision);
                Drawx.Math.sunColor = new Color(read.f(), read.f(), read.f());
            }
        }
    }

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

    public static class CrystalCrasher extends Types.ModBlock {
        public int length;

        public CrystalCrasher(String name) {
            super(name);

            hasItems = configurable = true;
            config(Boolean.class, (CrystalCrasherBuild build, Boolean ignored) -> {});
        }

        @SuppressWarnings("all")
        public class CrystalCrasherBuild extends ModBlockBuild {
            public boolean started = false, ended = false;
            public float progress;

            public void handler(int x, int y) {
                Tile tile = world.tile(x, y);
                if(tile.block() instanceof CrystalBlock) {
                    tile.block().destroyEffect.at(tile.build.x, tile.build.y);

                    CrystalBlock b = (CrystalBlock) tile.block();
                    items.add(b.item, b.amount);
                    tile.setNet(Blocks.air);
                }
            }

            @Override
            public void buildConfiguration(Table table) {
                table.table(t -> {
                    TextButton b = t.button(Core.bundle.get("start"), () -> started = true).size(160f, 50f).get();
                    b.update(() -> {
                        b.visible(() -> !started);
                    });
                });
            }

            public float sd() {
                return (progress / 100) * (length * 8);
            }

            @Override
            public void draw() {
                super.draw();

                Draw.draw(Layer.blockBuilding - 0.1f, () -> {
                    if(started && !ended) {
                        Draw.color(Color.sky);
                        Lines.stroke(1.5f);

                        Draw.alpha(0.30f);
                        Fill.rect(x, y, sd()*2, sd()*2);

                        Draw.alpha(progress / 100);
                        Lines.rect(x - sd(), y - sd(), sd()*2, sd()*2);
                    }
                });

                if(started && !ended) {
                    progress++;

                    if(progress == 100) {
                        ended = true;

                        for(int x = tileX() - length; x < tileX() + length; x++) {
                            for(int y = tileY() - length; y < tileY() + length; y++) {
                                handler(x, y);
                            }
                        }
                    }
                }

                dump();
            }
        }
    }

    public static class CrystalBlock extends Types.ModB {
        public boolean defaultBlockAssets = true;
        public Color color;
        public int sprites = 3, amount;
        public Item item;

        public TextureRegion[] regions;

        public CrystalBlock(String name, Item item, int amount) {
            super(name);

            update = true;
            solid = true;
            rotate = false;
            drawDisabled = false;

            this.item = item;
            this.amount = amount;

            requirements(Category.effect, with(item, amount));
            destroyEffect = breakEffect = Effects.bread;

            buildVisibility = BuildVisibility.sandboxOnly;
            researchCost = with();

            instantDeconstruct = true;
            deconstructThreshold = 1f;
            rebuildable = false;
            hasColor = true;
            mapColor = item.color;
        }

        @Override
        public void load() {
            super.load();

            if(defaultBlockAssets) {
                regions = new TextureRegion[sprites];
                for(int i = 0; i < sprites; i++) {
                    regions[i] = TheTech.mod("crystal-default-" + (1 + i));
                }
            } else {
                regions = new TextureRegion[sprites];
                regions[0] = TheTech.get(name);

                if(sprites > 1) {
                    for(int i = 1; i < sprites; i++) {
                        regions[i] = TheTech.get(name + i);
                    }
                }
            }
        }

        public class CrystalBlockBuild extends Building {
            float rot;
            int sprite;

            public CrystalBlockBuild() {
                rot = Mathf.random(359f);
                sprite = Mathf.random(regions.length - 1);
                Log.info(sprite);
            }

            @Override
            public void draw() {
                Draw.draw(Layer.blockOver - 1f, () -> {
                    Draw.blend(Blending.additive);
                    Draw.color(item.color, 0.2f);
                    Draw.rect("circle-shadow", x, y, size*8, size*8);
                    Draw.blend();

                    if(defaultBlockAssets) {
                        Draw.color(color == null ? item.color : color);
                    } else {
                        Draw.color(Color.white);
                    }

                    Draw.rect(regions[sprite], x, y, rot);
                });
            }

            @Override
            public void drawTeam() {
            }

            @Override
            public void drawLight(){
                super.drawLight();
                Drawf.light(x, y, 40f, Tmp.c1.set(item.color).mul(0.7f), 0.35f);
                Drawf.light(x, y, 20f, Tmp.c1.set(item.color).mul(0.7f), 1f);
            }

            @Override
            public void onDestroyed() {
                destroyEffect.at(x, y, item.color);
            }
        }
    }

    public static class AllSource extends PowerNode {
        public AllSource(String name) {
            super(name);

            hasItems = hasLiquids = true;

            itemCapacity = Integer.MAX_VALUE;
            liquidCapacity = Integer.MAX_VALUE;

            update = true;
            maxNodes = Integer.MAX_VALUE;
            outputsPower = true;
            consumesPower = false;
            envEnabled = Env.any;

            buildVisibility = BuildVisibility.sandboxOnly;
            requirements(Category.effect, with());

            localizedName = TheTech.prefix(localizedName);
        }

        @Override
        public void setBars() {
            super.setBars();

            removeBar("items");
            removeBar("liquid");
            removeBar("liquid-");
        }

        public class AllSourceBuild extends PowerNodeBuild implements Meteria.MeteriaGiverBuild {
            public ItemModule flowItems = new ItemModule();

            @Override
            public void updateTile() {
                super.updateTile();

                if(enabled) {
                    for(Item item : content.items()) {
                        items.set(item, Integer.MAX_VALUE);
                    }

                    liquids.clear();
                    for(Liquid liquid : content.liquids()) {
                        liquids.add(liquid, Integer.MAX_VALUE/2f);
                    }

                    dump();
                    for(Liquid liquid : content.liquids()) {
                        dumpLiquid(liquid);
                    }
                }
            }

            @Override
            public float getPowerProduction() {
                return enabled ? Integer.MAX_VALUE : 0f;
            }

            @Override
            public float value() {
                return enabled ? Integer.MAX_VALUE : 0f;
            }

            @Override
            public ItemModule flowItems(){
                return flowItems;
            }

            @Override
            public void handleItem(Building source, Item item){
                flowItems.handleFlow(item, 1);
            }

            @Override
            public boolean acceptItem(Building source, Item item){
                return enabled;
            }

            @Override
            public boolean acceptLiquid(Building source, Liquid liquid){
                return enabled;
            }

            @Override
            public void handleLiquid(Building source, Liquid liquid, float amount){
                liquids.handleFlow(liquid, amount);
            }
        }
    }

    public static class ProcessorSpeedUpBlock extends Types.ModBlock {
        public ConsumeLiquidBase liquidConsumer;
        public Color color = Pal.turretHeat;
        public int links = size * 4;
        public float consumeLiquidAmount;
        public float boost = 2;

        public ProcessorSpeedUpBlock(String name) {
            super(name);

            update = true;
            solid = true;
            rotate = false;
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("links", (ProcessorSpeedUpBlockBuild b) -> new Bar(
                    () -> "\uF7E4: " + b.connections + " / " + links,
                    () -> Color.orange,
                    () -> (float) b.connections / links
            ));
        }

        @Override
        public void setStats() {
            super.setStats();

            stats.add(Stat.output, "@ \uF7E4", links);
            stats.add(Stat.speedIncrease, boost * 100, StatUnit.percent);
        }

        @Override
        public void init() {
            if(hasLiquids) {
                liquidConsumer = findConsumer(c -> c instanceof ConsumeLiquidBase);

                if(liquidConsumer == null){
                    liquidConsumer = consume(new ConsumeLiquidFilter(liquid ->
                            liquid.temperature <= 0.5f && liquid.flammability < 0.1f, consumeLiquidAmount));
                }
            }

            super.init();
        }

        public class ProcessorSpeedUpBlockBuild extends ModBlockBuild {
            public Tacker.BasicTicker basicTicker = new Tacker.BasicTicker();
            public int connections = 0;

            public boolean canBoost() {
                return enabled && canConsume() && efficiency() > 0.8f;
            }

            public float boost() {
                return canBoost() ? boost : 0;
            }

            public float heat() {
                return canBoost() ? (float) connections / links : 0;
            }

            @Override
            public void updateTile() {
                if(boost() == 0 && !basicTicker.paused()) {
                    basicTicker.pause();
                }

                if(boost() > 0 && basicTicker.paused()) {
                    basicTicker.resume();
                }

                basicTicker.tick();
                connections = 0;

                if(canBoost()) {
                    float boost = boost();
                    for(Building b : proximity) {
                        if(b instanceof LogicBlock.LogicBuild && connections < links) {
                            for(int i = 0; i < boost; i++) {
                                b.updateTile();
                            }

                            connections++;
                        }
                    }
                }

                if(localDrawer != null && drawer != null) {
                    for(Drawer e : localDrawer.drawers) {
                        if(e instanceof Drawer.HeatDrawer e2) {
                            e2.heat = heat() * basicTicker.delta();
                            e2.color = color;
                        }

                        if(e instanceof Drawer.DrawLiquid e2 && hasLiquids && liquids != null) {
                            e2.stack = LiquidStack.with(liquids.current(), liquids.currentAmount())[0];
                            e2.cap = liquidCapacity;
                        }

                        if(e instanceof Drawer.DrawRotor e2) {
                            e2.rotation = heat() * 50;
                        }
                    }
                }
            }
        }
    }

    public static class UnbreakableWall extends Types.ModBlock {
        public UnbreakableWall(String name) {
            super(name);

            chanceDeflect = Integer.MAX_VALUE;
            flashHit = true;

            insulated = true;
            absorbLasers = true;
            flashColor = Color.white;
            health = Integer.MAX_VALUE;
            solid = true;

            update = true;
            canBurn = false;
            schematicPriority = Integer.MAX_VALUE;
        }

        public class UnbreakableWallBuild extends ModBlockBuild {
            @Override
            public void damage(Bullet bullet, Team source, float damage) {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void damage(float amount, boolean withEffect) {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void damage(float damage) {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void damage(Team source, float damage) {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void damageContinuous(float amount) {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void damageContinuousPierce(float amount) {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void damagePierce(float amount) {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void damagePierce(float amount, boolean withEffect) {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public boolean dead() {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
                return false;
            }

            @Override
            public void dead(boolean dead) {
                super.dead(false);
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void kill() {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void killed() {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public float health() {
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
                return Integer.MAX_VALUE;
            }

            @Override
            public void updateTile() {
                super.updateTile();
                health = Integer.MAX_VALUE;
            }

            @Override
            public void afterDestroyed() {
                super.afterDestroyed();
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }

            @Override
            public void onDestroyed() {
                super.onDestroyed();
                world.tile(tileX(), tileY()).setNet(block, team, rotation);
            }
        }
    }
}