package tntm.world.blocks.production;

import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import mindustry.world.modules.ItemModule;
import tntm.TheTech;
import tntm.world.blocks.meteria.meta.MeteriaGiverBuild;

import static mindustry.Vars.content;
import static mindustry.type.ItemStack.with;

public class AllSource extends PowerNode {
    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }

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

    public class AllSourceBuild extends PowerNodeBuild implements MeteriaGiverBuild {
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