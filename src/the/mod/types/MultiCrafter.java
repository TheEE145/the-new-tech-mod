package the.mod.types;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.ctype.*;
import mindustry.world.blocks.*;
import mindustry.world.consumers.*;
import mindustry.world.modules.*;
import the.mod.utils.Types;
import mindustry.type.*;

public class MultiCrafter extends Types.ModCrafter {
    public Seq<MultiCraft> crafts = new Seq<>();

    public static class MultiCraft {
        public ItemStack[] itemStacks, outItem;
        public LiquidStack[] liquidStacks, outLiquid;
        public UnlockableContent icon;

        public MultiCraft(ItemStack[] itemStacks, LiquidStack[] liquidStacks, ItemStack[] outItem, LiquidStack[] outLiquid, UnlockableContent icon) {
            this.itemStacks = itemStacks;
            this.liquidStacks = liquidStacks;

            this.outItem = outItem;
            this.outLiquid = outLiquid;
            this.icon = icon;
        }
    }

    public void addCraft(ItemStack[] outItem, LiquidStack[] outLiquid, ItemStack[] items, LiquidStack[] liquids, UnlockableContent icon) {
        crafts.add(new MultiCraft(items, liquids, outItem, outLiquid, icon));
    }

    public void addCraft(ItemStack[] item, ItemStack[] items, LiquidStack[] stacks, UnlockableContent icon) {
        addCraft(item, new LiquidStack[] {}, items, stacks, icon);
    }

    public void addCraft(ItemStack[] item, ItemStack[] items, UnlockableContent icon) {
        addCraft(item, items, new LiquidStack[] {}, icon);
    }

    public void addCraft(ItemStack[] item, LiquidStack[] liquids, UnlockableContent icon) {
        addCraft(item, new ItemStack[] {}, liquids, icon);
    }

    public void addCraft(LiquidStack[] liquid, ItemStack[] items, LiquidStack[] stacks, UnlockableContent icon) {
        addCraft(new ItemStack[] {}, liquid, items, stacks, icon);
    }

    public void addCraft(LiquidStack[] liquid, ItemStack[] items, UnlockableContent icon) {
        addCraft(liquid, items, new LiquidStack[] {}, icon);
    }

    public void addCraft(LiquidStack[] liquid, LiquidStack[] liquids, UnlockableContent icon) {
        addCraft(liquid, new ItemStack[] {}, liquids, icon);
    }

    public MultiCrafter(String name) {
        super(name);

        update = configurable = saveConfig = noUpdateDisabled = true;

        config(UnlockableContent.class, (MultiCrafterBuild tile, UnlockableContent icc) -> tile.icc = icc);
        configClear((MultiCrafterBuild tile) -> tile.icc = null);
    }

    public class MultiCrafterBuild extends ModCrafterBuild {
        UnlockableContent icc;
        MultiCraft config;

        public UnlockableContent get(MultiCraft m) {
            return m == null ? null : m.icon;
        }

        public Seq<UnlockableContent> getList() {
            Seq<UnlockableContent> result = new Seq<>();
            for(MultiCraft m : crafts) {
                result.add(get(m));
            }

            return result;
        }

        public void clearConsumes() {
            Seq<Consume> result = new Seq<>();
            for(Consume cons : consumers) {
                if(!(cons instanceof LiquidModule.LiquidConsumer) && !(cons instanceof ItemModule.ItemConsumer)) {
                    result.add(cons);
                }
            }

            Consume[] consumes = new Consume[result.size];
            for(int i = 0; i < result.size; i++) {
                consumes[i] = result.get(i);
            }

            consumers = consumes;
        }

        @Override
        public void draw() {
            super.draw();
            clearConsumes();

            if(icc == null) {
                config = null;
            } else {
                for(MultiCraft i : crafts) {
                    if(i.icon == icc) {
                        config = i;
                        break;
                    }
                }
            }

            if(config == null) {
                outputItems = new ItemStack[] {};
                outputLiquids = new LiquidStack[] {};
            } else {
                consumeLiquids(config.liquidStacks);
                consumeItems(config.itemStacks);
                outputItems = config.outItem;
                outputLiquids = config.outLiquid;
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(MultiCrafter.this, table, getList(), () -> icc, this::configure);
        }
    }
}