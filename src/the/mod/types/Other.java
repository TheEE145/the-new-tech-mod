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
import arc.scene.utils.Selection;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.SettingsMenuDialog;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.*;
import mindustry.world.modules.ItemModule;
import the.mod.TheTech;
import the.mod.content.Effects;
import the.mod.content.ModTechTree;
import the.mod.content.Redcon;
import the.mod.content.Statsx;
import the.mod.utils.*;
import the.mod.utils.Timer;

import java.util.Objects;

import static mindustry.Vars.*;
import static mindustry.type.ItemStack.with;

public class Other {
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

    public static class DPSBlock extends Types.ModBlock {
        public Drawx.FontType type = Drawx.FontType.UNDER_LINE;
        public float reloadTime = 180;

        public DPSBlock(String name) {
            super(name);
            configurable = true;

            update = true;
            buildVisibility = BuildVisibility.sandboxOnly;
            requirements(Category.defense, with());
        }

        public class DPSBlockBuild extends ModBlockBuild {
            private float dmgTimer = 0;
            private float timer = 0;
            public float damage = 0;

            @Override
            public void draw() {
                super.draw();

                if(damage < 1) {
                    return;
                }

                Draw.draw(Layer.overlayUI, () -> {
                    Drawx.text(x, y + size * 4f + 3f, team.color, ((int) damage) + " damage", type);
                });
            }

            public boolean justDamaged() {
                return dmgTimer > 0;
            }

            @Override
            public void updateTile() {
                super.updateTile();
                if(dmgTimer > 0) {
                    dmgTimer--;
                }

                if(justDamaged()) {
                    timer = reloadTime;
                } else {
                    timer--;
                }

                if(timer < 0) {
                    timer = reloadTime;
                    heal();

                    if(!justDamaged()) {
                        damage = 0;
                    }
                }

                if(!justDamaged()) {
                    damage -= 1f;
                }
            }

            @Override
            public float handleDamage(float amount) {
                damage += amount;
                dmgTimer = 10;

                return super.handleDamage(amount);
            }

            @Override
            public void buildConfiguration(Table table) {
                table.slider(0, Drawx.FontType.values().length - 1, 1, type.ordinal(), value -> {
                    type = Drawx.FontType.values()[(int) value];
                }).size(300f, 50f);
            }
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

            requirements(Category.effect, BuildVisibility.sandboxOnly, with());
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

        @Override
        public void setBars(){
            super.setBars();
            removeBar("health");
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

    public static class MultiCrafter extends Types.ModCrafter {
        public Seq<CraftPlan> plans;

        public MultiCrafter(String name) {
            super(name);

            update = true;
            solid = true;
            configurable = true;
            clearOnDoubleTap = true;

            config(Integer.class, (MultiCrafterBuild tile, Integer i) -> {
                if(!configurable) {
                    return;
                }

                if(tile.plan == i) {
                    return;
                }

                tile.plan = i < 0 || i >= plans.size ? -1 : i;
                tile.progress = 0;
            });

            config(UnlockableContent.class, (MultiCrafterBuild tile, UnlockableContent val) -> {
                if(!configurable) {
                    return;
                }

                int next = plans.indexOf(p -> p.out == val);
                if(tile.plan == next) {
                    return;
                }

                tile.plan = next;
                tile.progress = 0;
            });

            consume(new ConsumeItemDynamic((MultiCrafterBuild e) -> {
                CraftPlan plan = e.plan();
                return plan == null ? with() : plan.items;
            }));

            consume(new ConsumeLiquidDynamic((MultiCrafterBuild e) -> {
                CraftPlan plan = e.plan();
                return plan == null ? LiquidStack.with() : plan.liquids;
            }));
        }

        @Override
        public void setStats() {
            super.setStats();

            if(liquidCapacity == 0) {
                stats.remove(Stat.liquidCapacity);
            }

            stats.add(Statsx.requirements, table -> {
                table.left().row();

                for(CraftPlan plan : plans) {
                    table.pane(t -> {
                        t.setBackground(Styles.grayPanel);

                        TheTech.addx(plan.items, t);
                        TheTech.addx(plan.liquids, t);
                        TheTech.arrowx(t);

                        TheTech.addx(plan.amount, plan.out != null ? plan.out.uiIcon : TheTech.get("alphaaaa"), t);
                    }).growX().padTop(6).row();
                }
            });
        }

        @Override
        public void init() {
            liquidCapacity = 0;
            itemCapacity = 10;

            for(CraftPlan plan : plans) {
                for(LiquidStack stack : plan.liquids) {
                    liquidCapacity = Math.max(liquidCapacity, stack.amount * 120);
                }

                for(ItemStack stack : plan.items) {
                    itemCapacity = Math.max(itemCapacity, stack.amount * 2);
                }
            }

            if(liquidCapacity > 0) {
                hasLiquids = true;
            }

            if(itemCapacity > 0) {
                hasLiquids = true;
            }

            super.init();
        }

        @Override
        public void setBars() {
            super.setBars();

            removeBar("progress");
            addBar("progress", (MultiCrafterBuild b) -> {
                return new Bar(
                        () -> Core.bundle.get("bar.progress"),
                        () -> Pal.bar,
                        () -> 1f - b.loc / craftTime
                );
            });

            if(liquidCapacity == 0) {
                removeBar("liquid");
                removeBar("liquid-");
            }

            removeBar("items");
            removeBar("item");
        }

        public class MultiCrafterBuild extends ModCrafterBuild {
            public float loc = craftTime;
            public int plan = -1;

            public CraftPlan plan() {
                if(plan < 0 || plan >= plans.size) {
                    return null;
                }

                return plans.get(plan);
            }

            @Override
            public boolean acceptItem(Building source, Item item) {
                return plan != -1 && items.get(item) < getMaximumAccepted(item) &&
                        Structs.contains(plan() == null ? with() : plan().items, stack -> stack.item == item);
            }

            @Override
            public boolean acceptLiquid(Building source, Liquid liquid) {
                return plan != -1 &&
                        Structs.contains(plan() == null ? LiquidStack.with() : plan().liquids, stack -> stack.liquid == liquid);
            }

            public boolean canBeCrafted() {
                boolean canConsume = plan() != null;
                if(canConsume) {
                    for(ItemStack stackx : plan().items) {
                        if(items.get(stackx.item) < stackx.amount) {
                            canConsume = false;
                        }
                    }

                    for(LiquidStack stackx : plan().liquids) {
                        if(liquids.get(stackx.liquid) < stackx.amount) {
                            canConsume = false;
                        }
                    }
                }

                return canConsume;
            }

            @Override
            public void craft() {
                consume();

                CraftPlan plan = plan();
                if(plan != null) {
                    if(plan.out instanceof Item item) {
                        int amount = (int) (items.get(item) + plan.amount);
                        items.set(item, Math.min(amount, itemCapacity));
                    }
                }

                if(wasVisible) {
                    craftEffect.at(x, y);
                }

                loc = craftTime;
            }

            @Override
            public void write(Writes write) {
                super.write(write);
                write.i(plan);
            }

            @Override
            public void read(Reads read, byte revision) {
                super.read(read, revision);
                plan = read.i();
            }

            public boolean isFullInventory() {
                CraftPlan plan = plan();
                if(plan == null) {
                    return false;
                }

                if(plan.out instanceof Item item) {
                    return items.get(item) >= itemCapacity;
                }

                if(plan.out instanceof Liquid liquid) {
                    return liquids.get(liquid) >= liquidCapacity;
                }

                return false;
            }

            @Override
            public BlockStatus status() {
                return canBeCrafted() ? isFullInventory() ? BlockStatus.noOutput : super.status() : BlockStatus.noInput;
            }

            @Override
            public void updateTile() {
                ItemStack[] stack = with();
                LiquidStack[] lstack = LiquidStack.with();
                CraftPlan plan = plan();

                if(plan != null) {
                    if(plan.out instanceof Liquid) {
                        lstack = LiquidStack.with(plan.out, plan.amount);
                    }

                    if(plan.out instanceof Item) {
                        stack = with(plan.out, plan.amount);
                    }
                }

                outputItems = stack;
                outputLiquids = lstack;

                if(!isFullInventory()) {
                    if(canBeCrafted()) {
                        loc--;
                    }

                    if(loc < 0f) {
                        craft();
                    }
                }

                if(efficiency > 0){
                    warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                    //continuously output based on efficiency
                    if(outputLiquids != null){
                        float inc = getProgressIncrease(1f);
                        for(var output : outputLiquids){
                            handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                        }
                    }

                    if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                        updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                    }
                }else{
                    warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                }

                //TODO may look bad, revert to edelta() if so
                totalProgress += warmup * Time.delta;

                dumpOutputs();
            }

            @Override
            public void buildConfiguration(Table table) {
                Seq<UnlockableContent> contents = plans.map(p -> p.out).filter(Objects::nonNull);

                if(contents.any()) {
                    ItemSelection.buildTable(MultiCrafter.this, table, contents, () -> plan() == null ? null : plan().out, p -> {
                        plan = plans.indexOf(pl -> pl.out == p);
                    });
                } else {
                    table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
                }
            }
        }

        public static class CraftPlan {
            public UnlockableContent out;
            public float amount;

            public ItemStack[] items = with();
            public LiquidStack[] liquids = LiquidStack.with();

            public CraftPlan(UnlockableContent out, float amount) {
                this.out = out instanceof Liquid || out instanceof Item ? out : null;
                this.amount = amount;
            }
        }
    }
}