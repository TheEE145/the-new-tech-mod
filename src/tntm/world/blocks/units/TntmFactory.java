package tntm.world.blocks.units;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.units.UnitFactory;
import tntm.TheTech;

public class TntmFactory extends UnitFactory {
    public TntmFactory(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }

    public class TNTMFactoryBuild extends UnitFactoryBuild {
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