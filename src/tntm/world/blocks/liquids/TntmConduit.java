package tntm.world.blocks.liquids;

import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.Conduit;

public class TntmConduit extends Conduit {
    public boolean showLiquids = true;

    public TntmConduit(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        if(!showLiquids) {
            removeBar("liquid-");
            removeBar("liquid");
        }
    }

    public class TntmConduitBuild extends ConduitBuild {
        @Override
        public void drawLiquidLight(Liquid liquid, float amount) {
            if(showLiquids) {
                super.drawLiquidLight(liquid, amount);
            }
        }
    }
}