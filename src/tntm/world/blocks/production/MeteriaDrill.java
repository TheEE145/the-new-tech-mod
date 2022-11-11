package tntm.world.blocks.production;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockStatus;
import tntm.content.TntmStats;
import tntm.graphics.TntmPal;
import tntm.world.blocks.meteria.meta.MeteriaReceiverBuild;

import static arc.Core.bundle;

public class MeteriaDrill extends TntmDrill {
    public float maxMeteria;
    public float meteriaConsume;

    public MeteriaDrill(String name) {
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

        addBar("meteria", (MeteriaDrillBuild build) -> new Bar(
                () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / maxMeteria) * 100) + "%",
                () -> TntmPal.meteria,
                () -> build.meteria / maxMeteria
        ));
    }

    public class MeteriaDrillBuild extends DrillBuild implements MeteriaReceiverBuild {
        public float meteria;

        public MeteriaDrillBuild() {
            meteria = 0;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(meteria);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            meteria = read.f();
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

        @Override
        public void draw() {
            super.draw();

            if(meteria > maxMeteria) {
                meteria = maxMeteria;
            }

            if(meteria >= meteriaConsume && items.total() < itemCapacity) {
                meteria -= meteriaConsume;
            }
        }

        @Override
        public BlockStatus status() {
            return meteria < meteriaConsume ? BlockStatus.noInput : super.status();
        }

        @Override
        public void updateTile() {
            if(meteria >= meteriaConsume) {
                super.updateTile();
            }
        }
    }
}