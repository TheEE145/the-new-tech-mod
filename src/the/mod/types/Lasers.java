package the.mod.types;

import arc.Events;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Blocks;
import mindustry.entities.Effect;
import mindustry.entities.TargetPriority;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import the.mod.TheTech;
import the.mod.utils.Types;

public class Lasers {
    public static void load() {
        Events.on(EventType.TapEvent.class, event -> {
            Building b = event.tile.build;
            if(b == null) {
                return;
            }

            if(b instanceof LaserBlock.LaserBlockBuild) {
                //TODO change laser angle dialog
            }
        });
    }

    public static class LaserModule {
        public float sx, sy, ex, ey, th, a;
        public Color color;

        public LaserModule(float sx, float sy, float ex, float ey) {
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
        }

        public void draw() {
            Draw.color(color);
            Draw.alpha(a);
            Lines.stroke(th);
            Lines.line(sx, sy, ex, ey);
        }

        public float len() {
            return (float) Math.sqrt(TheTech.pow(ex - sx) + TheTech.pow(ey - sy));
        }

        public Tile tileOn() {
            return TheTech.getTileByDraw(ex, ey);
        }

        public LaserModule bounce(float nx, float ny) {
            LaserModule laserModule = new LaserModule(ex, ey, nx, ny);

            laserModule.a = a;
            laserModule.color = color;
            return laserModule;
        }

        @Override
        public String toString() {
            return "(" + sx + ", " + sy + ", " + ex + ", " + ey + ")";
        }
    }

    public static class LaserLink {
        public Seq<LaserModule> lasers;
        public float startX, startY;

        public LaserLink(float startX, float startY) {
            this.startX = startX;
            this.startY = startY;

            lasers = new Seq<>();
        }

        public Tile start() {
            return TheTech.getTileByDraw(startX, startY);
        }

        public LaserModule last() {
            return lasers.isEmpty() ? null : lasers.get(lasers.size - 1);
        }

        public Tile target() {
            return last() == null ? start() : last().tileOn();
        }

        public float len() {
            float len = 0;
            for(LaserModule m : lasers) {
                len += m.len();
            }

            return len;
        }

        public void draw() {
            for(LaserModule e : lasers) {
                e.draw();
            }
        }

        public LaserModule start(LaserModule m, float x, float y) {
            if(m == null) {
                return null;
            }

            m.sx = startX;
            m.sy = startY;
            m.ex = x;
            m.ey = y;

            lasers.add(m);
            return m;
        }

        public LaserModule bounce(float x, float y) {
            LaserModule laser;

            if(lasers.isEmpty()) {
                laser = new LaserModule(startX, startY, x, y);
            } else {
                laser = last().bounce(x, y);
            }

            lasers.add(laser);
            return laser;
        }

        @Override
        public String toString() {
            return lasers.toString();
        }
    }

    public static class LaserMirror extends Types.ModBlock {
        public float offset;
        public float defaultAngle;

        public LaserMirror(String name) {
            super(name);
            canBurn = false;

            rotate = true;
            priority = TargetPriority.transport;
            conveyorPlacement = true;
            underBullets = true;
        }

        public class LaserMirrorBuild extends ModBlockBuild {
            public float angle() {
                if(rotation == 0 || rotation == 2) {
                    return -offset;
                }

                if(rotation == 3 || rotation == 1) {
                    return offset;
                }

                return defaultAngle;
            }
        }
    }

    public static class LaserBlock extends Types.ModBlock {
        public static final float theta = (float) (Math.PI * 2);
        public TextureRegion turretRegion;
        public Effect endEffect;

        public boolean drawTargetTile;
        public Color laserColor;
        public float laserStroke;
        public float laserAlpha;

        public float laserRadius;
        public int lasers;

        public LaserBlock(String name) {
            super(name);

            TheTech.on(EventType.ClientLoadEvent.class, () -> {
                turretRegion = TheTech.mod(name + "-turret");
            });

            canBurn = false;
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("links", (LaserBlockBuild b) -> new Bar(
                    () -> "links: " + b.len,
                    () -> Color.orange,
                    () -> (float) b.len / (float) lasers
            ));
        }

        public class LaserBlockBuild extends ModBlockBuild {
            public LaserLink link;
            public float angle = 0;
            public int len;

            public boolean canMirror() {
                return link.target().block() != null && (link.target().block() instanceof LaserMirror);
            }

            public float targetRotation() {
                Building b = link.target().build;
                if(b == null) {
                    return 0;
                }

                if(b instanceof LaserMirror.LaserMirrorBuild) {
                    return ((LaserMirror.LaserMirrorBuild) b).angle();
                }

                return b.rotation * 90;
            }

            private void str(LaserModule m, float angle) {
                if(angle > 360) {
                    angle -= 360;
                }

                link.lasers.add(m);
                len++;

                if(canMirror() && len < lasers) {
                    float r = targetRotation();
                    str(new LaserModule(m.ex, m.ey, thx(angle + r, laserRadius) + m.ex, thy(angle + r, laserRadius) + m.ey) {{
                        color = m.color;
                        a = m.a;
                        th = m.th;
                    }}, angle + r);
                }
            }

            public float thx(float angle, float rad) {
                return (float) (Math.cos((angle / 360) * theta) * rad);
            }

            public float thy(float angle, float rad) {
                return (float) (Math.sin((angle / 360) * theta) * rad);
            }

            @Override
            public void draw() {
                super.draw();

                len = 0;
                link = new LaserLink(x, y);

                str(new LaserModule(link.startX, link.startY, thx(angle, laserRadius) + x, thy(angle, laserRadius) + y) {{
                    color = laserColor;
                    a = laserAlpha;
                    th = laserStroke;
                }}, angle);

                Draw.draw(Layer.turret, () -> {
                    link.draw();
                    if(turretRegion != null) {
                        Draw.color(Color.white);
                        Draw.alpha(1f);
                        Draw.rect(turretRegion, x, y, angle - 90);
                    };
                });

                if(!link.lasers.isEmpty()) {
                    Tile target = link.target();

                    if(endEffect != null) {
                        endEffect.at(target.worldx(), target.worldy());
                    }

                    if(drawTargetTile) {
                        Draw.draw(Layer.max, () -> {
                            Draw.color(Color.red);
                            Lines.stroke(1.5f);
                            Lines.rect(target.worldx() - 5, target.worldy() - 5, 10, 10);
                        });
                    }
                }
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
        }
    }
}