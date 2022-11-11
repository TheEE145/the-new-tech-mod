package tntm.world.blocks.lasers;

import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;

public class LaserMultiMirror extends LaserMirror {
    public LaserMultiMirror(String name) {
        super(name);
        configurable = true;

        config(Integer.class, (laserMultiMirrorBuild build, Integer value) -> {});
    }

    public class laserMultiMirrorBuild extends LaserMirrorBuild {
        public float angle;

        @Override
        public float angle() {
            return rotation == 0 || rotation == 2 ? -angle : angle;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(angle);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            angle = read.f();
        }

        @Override
        public void buildConfiguration(Table table) {
            table.table(t -> {
                Slider s = t.slider(0, 360, 10, (value) -> {
                    angle = value;
                }).get();

                s.update(() -> {
                    s.setValue(angle);
                });
            });
        }
    }
}