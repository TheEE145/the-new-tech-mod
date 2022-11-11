package tntm.world.blocks.meteria;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockStatus;
import tntm.content.TntmStats;
import tntm.graphics.TntmPal;
import tntm.world.blocks.craft.TntmCrafter;

import static arc.Core.bundle;

public class MeteriaCrafter extends TntmCrafter {
    public float meteriaGet;
    public float maxMeteria;

    public MeteriaCrafter(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(TntmStats.meteriaProduce, meteriaGet);
        stats.add(TntmStats.meteriaCapacity, maxMeteria);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("meteria", (MeteriaCrafterBuild build) -> new Bar(
                () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / maxMeteria) * 100) + "%",
                () -> TntmPal.meteria,
                () -> build.meteria / maxMeteria
        ));
    }

    public class MeteriaCrafterBuild extends TntmCrafterBuild {
        @Override
        public BlockStatus status() {
            return meteria >= maxMeteria ? BlockStatus.noOutput : super.status();
        }

        public float meteria = 0;

        @Override
        public void craft() {
            if(meteria < maxMeteria) {
                super.craft();

                meteria += meteriaGet;
                if(meteria > maxMeteria) {
                    meteria = maxMeteria;
                }
            }
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
    }
}