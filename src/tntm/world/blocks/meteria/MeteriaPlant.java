package tntm.world.blocks.meteria;

import mindustry.ui.Bar;
import mindustry.world.meta.BlockStatus;
import tntm.content.TntmStats;
import tntm.graphics.TntmPal;
import tntm.world.blocks.craft.TntmCrafter;
import tntm.world.blocks.meteria.meta.MeteriaReceiverBuild;

import static arc.Core.bundle;

public class MeteriaPlant extends TntmCrafter {
    public float maxMeteria;
    public float meteriaConsume;

    public MeteriaPlant(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(TntmStats.meteriaCapacity, maxMeteria);
        stats.add(TntmStats.meteriaConsume, meteriaConsume);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("meteria", (MeteriaPlantBuild build) -> new Bar(
                () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / maxMeteria) * 100) + "%",
                () -> TntmPal.meteria,
                () -> build.meteria / maxMeteria
        ));
    }

    public class MeteriaPlantBuild extends TntmCrafterBuild implements MeteriaReceiverBuild {
        public float meteria;

        @Override
        public void craft() {
            if(meteria >= meteriaConsume) {
                super.craft();
                meteria -= meteriaConsume;
            }
        }

        @Override
        public BlockStatus status() {
            return meteria >= meteriaConsume ? super.status() : BlockStatus.noInput;
        }

        public MeteriaPlantBuild() {
            meteria = 0;
        }

        @Override
        public float meteria() {
            return meteria;
        }

        @Override
        public float meteriaCapacity() {
            return maxMeteria;
        }

        @Override
        public void meteria(float meteria) {
            this.meteria = meteria;
        }
    }
}