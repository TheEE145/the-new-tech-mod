package tntm.world.blocks.logic;

import arc.graphics.Color;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import tntm.graphics.drawer.DrawLiquid;
import tntm.graphics.drawer.DrawRotor;
import tntm.graphics.drawer.Drawer;
import tntm.graphics.drawer.HeatDrawer;
import tntm.utils.ticker.BasicTicker;
import tntm.utils.ticker.Ticker;
import tntm.world.blocks.walls.TntmBlock;

public class ProcessorSpeedUpBlock extends TntmBlock {
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

    public class ProcessorSpeedUpBlockBuild extends TntmBlockBuild {
        public BasicTicker basicTicker = new BasicTicker();
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
                    if(e instanceof HeatDrawer e2) {
                        e2.heat = heat() * basicTicker.delta();
                        e2.color = color;
                    }

                    if(e instanceof DrawLiquid e2 && hasLiquids && liquids != null) {
                        e2.stack = LiquidStack.with(liquids.current(), liquids.currentAmount())[0];
                        e2.cap = liquidCapacity;
                    }

                    if(e instanceof DrawRotor e2) {
                        e2.rotation = heat() * 50;
                    }
                }
            }
        }
    }
}