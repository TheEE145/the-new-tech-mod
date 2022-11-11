package tntm.world.blocks.craft;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import tntm.TheTech;
import tntm.asset.TntmAssets;
import tntm.content.TntmStats;
import tntm.ui.TntmUI;
import tntm.world.blocks.consumes.ConsumeLiquidDynamic;
import tntm.world.blocks.craft.meta.CraftPlan;
import tntm.world.blocks.meteria.meta.MeteriaReceiverBuild;

import java.util.Objects;

import static mindustry.type.ItemStack.with;

public class MultiCrafter extends TntmCrafter {
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
        TheTech.setup(this);

        if(liquidCapacity == 0) {
            stats.remove(Stat.liquidCapacity);
        }

        stats.add(TntmStats.requirements, table -> {
            table.left().row();

            for(CraftPlan plan : plans) {
                table.pane(t -> {
                    t.setBackground(Styles.grayPanel);

                    TntmUI.addx(plan.items, t);
                    TntmUI.addx(plan.liquids, t);
                    TntmUI.arrowx(t);

                    TntmUI.addx(plan.amount, plan.out != null ? plan.out.uiIcon : TntmAssets.get("alphaaaa"), t);
                }).growX().padTop(6).row();
            }
        });
    }

    @Override
    public void init() {
        liquidCapacity = 0;
        itemCapacity = 10;
        hasItems = true;

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

    public class MultiCrafterBuild extends TntmCrafterBuild implements MeteriaReceiverBuild {
        public float cTimer = craftTime;
        public float meteria = 0;

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

            loc = cTimer;
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

            cTimer = plan() == null ? craftTime : plan().craftTime == -1 ? craftTime : plan().craftTime;

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
            if(meteria > meteriaCapacity()) {
                meteria = meteriaCapacity();
            }
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

        @Override
        public float meteria() {
            return meteria;
        }

        @Override
        public float meteriaCapacity() {
            return plan() == null ? 0 : plan().meteria * 2;
        }

        @Override
        public void meteria(float meteria) {
            this.meteria = meteria;
        }

        @Override
        public boolean isMeteria() {
            return plan() != null && plan().meteria > 0;
        }
    }
}