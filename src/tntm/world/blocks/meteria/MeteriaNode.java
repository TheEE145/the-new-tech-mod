package tntm.world.blocks.meteria;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import tntm.TheTech;
import tntm.content.TntmFx;
import tntm.content.TntmStats;
import tntm.graphics.TntmDraw;
import tntm.world.blocks.RadiusBlock;
import tntm.graphics.TntmPal;
import tntm.world.blocks.meteria.meta.MeteriaGiverBuild;
import tntm.world.blocks.meteria.meta.MeteriaReceiverBuild;

import static arc.Core.bundle;
import static mindustry.Vars.world;
import static tntm.TheTech.toBlock;

public class MeteriaNode extends RadiusBlock {
    public float maxMeteria;

    public MeteriaNode(String name) {
        super(name);

        colorByTeam = false;
        radColor = TntmPal.meteria;
        update = true;
        noUpdateDisabled = false;
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(TntmStats.meteriaCapacity, maxMeteria);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("meteria", (MeteriaNodeBuild build) -> new Bar(
                () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / build.local$maxMeteria) * 100) + "%",
                () -> TntmPal.meteria,
                () -> build.meteria / build.local$maxMeteria
        ));

        addBar("boost", (MeteriaNodeBuild build) -> new Bar(
                () -> bundle.get("meteria.boost") + ": " + (int) Math.floor(build.local$maxMeteria/maxMeteria * 100) + "%",
                () -> TntmPal.meteria,
                () -> (int) Math.floor(build.local$maxMeteria - maxMeteria) == 0 ? 0 : 1
        ));

        addBar("links", (MeteriaNodeBuild build) -> new Bar(
                () -> build.blocksInRange.size == 0 ?
                        bundle.get("meteria.nolinks") : bundle.get("meteria.links") + ": " + build.blocksInRange.size,

                () -> Color.orange,
                () -> build.blocksInRange.size > 0 ? 1 : 0
        ));
    }

    public class MeteriaNodeBuild extends RadiusBlockBuild {
        public Seq<Building> buildsInRange;
        public Seq<Block> blocksInRange;
        public Seq<Vec2> blocksInRangeP;
        public float local$maxMeteria = maxMeteria;
        public float meteria = 0;

        public static final float ratio = 1.634f;

        public MeteriaNodeBuild() {
            super();

            buildsInRange = new Seq<>();
            blocksInRange = new Seq<>();
            blocksInRangeP = new Seq<>();
        }

        @Deprecated
        public void drawField() {
            Draw.draw(Layer.max, () -> {
                Draw.color(TntmPal.meteria);
                Draw.alpha(0.25f);
                Lines.stroke(2.5f);
                Lines.rect(x - range(), y - range(), range()*2, range()*2);
                Draw.flush();
            });
        }

        @Deprecated
        public float range() {
            return -1;
        }

        public void drawLinks() {
            Draw.draw(Layer.max, () -> {
                Vec2 vec;
                float sie;
                for(int i = 0; i < blocksInRange.size; i++) {
                    vec = blocksInRangeP.get(i);
                    sie = blocksInRange.get(i).size * 8;

                    Building b = buildsInRange.get(i);
                    if(b instanceof MeteriaNodeBuild) {
                        TntmDraw.beam(x, y, vec.x, vec.y, TntmPal.meteria, 0.25f);
                        Draw.color(TntmPal.meteria);
                    } else {
                        Draw.color(TntmPal.meteria);
                        Lines.stroke(1.5f);
                        Draw.alpha(0.25f);
                        Lines.rect(vec.x - sie/2 - ratio, vec.y - sie/2 - ratio, sie + ratio * 2, sie + ratio * 2);
                    }
                }

                Draw.flush();
            });
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(meteria);
            write.f(local$maxMeteria);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            meteria = read.f();
            local$maxMeteria = read.f();
        }

        public void addLink(Tile tile) {
            blocksInRange.add(tile.block());
            blocksInRangeP.add(new Vec2(tile.build.x, tile.build.y));
            buildsInRange.add(tile.build);
        }

        public void blocksInRange() {
            buildsInRange = new Seq<>();
            blocksInRange = new Seq<>();
            blocksInRangeP = new Seq<>();

            Seq<Building> cache = new Seq<>();
            for(Building e : buildings((build) -> {
                Block block = toBlock(build);

                if(block instanceof MeteriaNodeBooster) {
                    return true;
                }

                if(block instanceof MeteriaNode) {
                    return true;
                }

                if(block instanceof MeteriaSource) {
                    return true;
                }

                if(block instanceof MeteriaCrafter) {
                    return true;
                }

                return (build instanceof MeteriaReceiverBuild b && b.isMeteria()) || build instanceof MeteriaGiverBuild;
            })) {
                cache.add(e);
            }

            //remove all duplicates to display normal
            Seq<Building> result = new Seq<>();
            for(Building e : cache) {
                boolean found = false;
                for(Building e2 : result) {
                    if(TheTech.isPart(e, e2)) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    result.add(e);
                }
            }

            for(Building e : result) {
                if(TheTech.isPart(e, this)) {
                    continue;
                }

                addLink(world.tile(e.tileX(), e.tileY()));
            }
        }

        public float maxMeteriaCalculate() {
            float result = maxMeteria;

            for(Block block : blocksInRange) {
                if(block instanceof MeteriaNodeBooster b) {
                    result += b.meteriaBoost;
                }
            }

            return result;
        }

        @Override
        public void draw() {
            super.draw();
            drawLinks();
        }

        @Override
        public void update() {
            super.update();
            updateTact();
        }

        public void updateTact() {
            blocksInRange();

            float meteriaMax = maxMeteriaCalculate();
            if(local$maxMeteria > meteriaMax) {
                TntmFx.meteriaExplode.at(x, y);
            }

            if(meteria > meteriaMax) {
                meteria = meteriaMax;
            }

            local$maxMeteria = meteriaMax;
            for(Building b : buildsInRange) {
                if(TheTech.toBlock(b) instanceof MeteriaSource) {
                    meteria = local$maxMeteria;
                }

                if(b instanceof MeteriaCrafter.MeteriaCrafterBuild c) {
                    c.meteria = sub(c.meteria);
                }

                if(b instanceof MeteriaReceiverBuild b2) {
                    ((MeteriaReceiverBuild) b).meteria(add(b2.meteria(), b2.meteriaCapacity()));
                }

                if(b instanceof MeteriaGiverBuild b2) {
                    meteria += b2.value();

                    if(meteria < 0) {
                        meteria = 0;
                    }

                    if(meteria > local$maxMeteria) {
                        meteria = local$maxMeteria;
                    }
                }

                if(b instanceof MeteriaNodeBuild) {
                    float bothMeteria = ((MeteriaNodeBuild) b).meteria + this.meteria;
                    bothMeteria = bothMeteria / 2;

                    this.meteria = bothMeteria;
                    ((MeteriaNodeBuild) b).meteria = bothMeteria;
                }
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
        }

        public float add(float bMeteria, float cap) {
            if(bMeteria > meteria) {
                bMeteria += meteria;
                meteria = 0;

                if(bMeteria > cap) {
                    meteria = bMeteria - cap;
                    bMeteria = cap;
                }
            } else {
                float left = cap - bMeteria;

                if(meteria < left) {
                    bMeteria += meteria;
                    meteria = 0;
                } else {
                    meteria -= left;
                    bMeteria += left;
                }
            }

            return bMeteria;
        }

        public float sub(float bMeteria) {
            float left = meteriaLeft();
            if(bMeteria > left) {
                bMeteria -= left;
                meteria += left;
            } else {
                meteria += bMeteria;
                bMeteria = 0;
            }

            return bMeteria;
        }

        public float meteriaLeft() {
            return local$maxMeteria - meteria;
        }
    }
}