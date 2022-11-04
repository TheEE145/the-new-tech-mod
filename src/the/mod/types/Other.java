package the.mod.types;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.game.EventType;
import the.mod.TheTech;
import the.mod.utils.*;

import java.util.concurrent.atomic.AtomicReference;

public class Other {
    public static class SunGenerator extends Types.ModBlock {
        public static void loadEvent() {
            Events.on(EventType.TapEvent.class, e -> {
                if(e.tile.build instanceof SunGeneratorBuild) {
                    TheTech.show("change color", d -> {
                        d.cont.pane(t -> {
                            t.slider(0, 1, 0.01f, (value) -> {
                                Drawx.Math.sunColor = new Color(value, Drawx.Math.sunColor.g, Drawx.Math.sunColor.b);
                            }).size(300f, 45f).row();

                            t.slider(0, 1, 0.01f, (value) -> {
                                Drawx.Math.sunColor = new Color(Drawx.Math.sunColor.r, value, Drawx.Math.sunColor.b);
                            }).size(300f, 45f).row();

                            t.slider(0, 1, 0.01f, (value) -> {
                                Drawx.Math.sunColor = new Color(Drawx.Math.sunColor.r, Drawx.Math.sunColor.g, value);
                            }).size(300f, 45f).row();

                            t.button("apply", d::hide).size(100f, 50f);
                        });
                    });
                }
            });
        }

        public SunGenerator(String name) {
            super(name);
        }

        public class SunGeneratorBuild extends Types.ModBlock.ModBlockBuild {
            float rotation = 0;

            @Override
            public void draw() {
                super.draw();

                if(Drawx.sun != null) {
                    Draw.color(Drawx.Math.sunColor);
                    Draw.alpha(0.55f);
                    Draw.rect(Drawx.sun, x, y, size*6, size*6, rotation);
                }

                rotation++;
                if(rotation >= 360) {
                    rotation = 0;
                }
            }

            @Override
            public void write(Writes write) {
                super.write(write);
                Color c = Drawx.Math.sunColor;
                write.f(c.r);
                write.f(c.g);
                write.f(c.b);
            }

            @Override
            public void read(Reads read, byte revision) {
                super.read(read, revision);
                Drawx.Math.sunColor = new Color(read.f(), read.f(), read.f());
            }
        }
    }
}