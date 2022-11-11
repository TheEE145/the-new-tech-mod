package tntm.world.blocks.lasers;

import arc.func.Cons3;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import tntm.TheTech;

import tntm.asset.TntmAssets;
import tntm.world.blocks.enviroment.TntmFloor;
import tntm.world.blocks.enviroment.TntmStaticWall;
import tntm.world.blocks.lasers.meta.*;
import tntm.world.blocks.walls.TntmBlock;

import static tntm.utils.TntmMath.*;

public class LaserBlock extends TntmBlock {
    public Cons3<Tile, Block, Building> onTarget;
    public Effect endEffect, startEffect;
    public boolean mineable = false;
    public TextureRegion turretRegion;
    public float mineSpeed = 60f;
    public float damage = 10;

    public float startY = 4 * size;
    public boolean drawTargetTile;
    public Color laserColor;
    public float laserStroke;
    public float laserAlpha;

    public float laserRadius;
    public int lasers, tier = 1;

    public LaserBlock(String name) {
        super(name);
        canBurn = false;
        configurable = true;

        config(Integer.class, (LaserBlockBuild build, Integer value) -> {});
    }

    @Override
    public void load() {
        super.load();

        turretRegion = TntmAssets.get(name + "-turret");
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

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.damage, damage);
        stats.add(Stat.tiles, laserRadius/8);

        if(mineable) {
            stats.add(Stat.mineSpeed, mineSpeed);
            stats.add(Stat.mineTier, tier);
        }
    }

    public class LaserBlockBuild extends TntmBlockBuild {
        public LaserLink link;
        public float angle = 0;
        public int len;
        public float progress;

        //for extends
        public boolean canWork() {
            return true;
        }

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
            Tile tile = link.target();
            len++;

            m.ex = tile.worldx();
            m.ey = tile.worldy();

            if(canMirror() && len < lasers) {
                float r = targetRotation();
                str(new LaserModule(m.ex, m.ey, thx(angle + r, laserRadius) + m.ex, thy(angle + r, laserRadius) + m.ey) {{
                    color = m.color;
                    a = m.a;
                    th = m.th;
                }}, angle + r);
            }
        }

        public void targetRenderer() {
            Tile target = link.target();

            Block block = target.block();
            Building building = target.build;

            if(building != null) {
                building.damage(damage);
            }

            if(block != null) {
                if(block instanceof TntmStaticWall || block instanceof TntmFloor) {
                    if(block.itemDrop != null && items.total() < itemCapacity) {
                        progress--;
                        if(progress <= 0) {
                            items.add(block.itemDrop, 1);
                            progress = mineSpeed;
                        }
                    }
                }
            }

            if(onTarget != null) {
                onTarget.get(target, block, building);
            }
        }

        @Override
        public void draw() {
            super.draw();

            Draw.draw(Layer.turret, () -> {
                if(canWork()) {
                    link.draw();
                }

                if(turretRegion != null) {
                    Draw.color(Color.white);
                    Draw.alpha(1f);
                    Draw.rect(turretRegion, x, y, angle - 90);
                };
            });

            if(!link.lasers.isEmpty() && canWork()) {
                Tile target = link.target();

                if(endEffect != null) {
                    endEffect.at(target.worldx(), target.worldy());
                }

                if(startEffect != null) {
                    startEffect.at(thx(angle, startY) + x, thy(angle, startY) + y);
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
        public void update() {
            super.update();
            if(canWork()) {
                len = 0;
                link = new LaserLink(x, y);

                str(new LaserModule(link.startX, link.startY, thx(angle, laserRadius) + x, thy(angle, laserRadius) + y) {{
                    color = laserColor;
                    a = laserAlpha;
                    th = laserStroke;
                }}, angle);
            }

            if(!link.lasers.isEmpty() && canWork()) {
                targetRenderer();
            }

            dump();
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