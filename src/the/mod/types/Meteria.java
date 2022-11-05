package the.mod.types;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.BlockStatus;
import the.mod.TheTech;
import the.mod.content.*;
import the.mod.utils.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static the.mod.TheTech.toBlock;

public class Meteria {
    public static class MeteriaNode extends RadiusBlock {
        public float maxMeteria;

        public MeteriaNode(String name) {
            super(name);

            colorByTeam = false;
            radColor = ThePal.meteria;
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("meteria", (MeteriaNodeBuild build) -> new Bar(
                    () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / build.local$maxMeteria) * 100) + "%",
                    () -> ThePal.meteria,
                    () -> build.meteria / build.local$maxMeteria
            ));

            addBar("boost", (MeteriaNodeBuild build) -> new Bar(
                    () -> bundle.get("meteria.boost") + ": " + (int) Math.floor(build.local$maxMeteria/maxMeteria * 100) + "%",
                    () -> ThePal.meteria,
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
                    Draw.color(ThePal.meteria);
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
                            Drawx.beam(x, y, vec.x, vec.y, ThePal.meteria, 0.25f);
                            Draw.color(ThePal.meteria);
                        } else {
                            Draw.color(ThePal.meteria);
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

                    return build instanceof MeteriaReceiverBuild;
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
                    if(block instanceof MeteriaNodeBooster) {
                        result += ((MeteriaNodeBooster) block).meteriaBoost;
                    }
                }

                return result;
            }

            @Override
            public void draw() {
                super.draw();
                drawLinks();

                updateTact();
            }

            public void updateTact() {
                blocksInRange();

                float meteriaMax = maxMeteriaCalculate();
                if(local$maxMeteria > meteriaMax) {
                    Effects.meteriaExplode.at(x, y);
                }

                if(meteria > meteriaMax) {
                    meteria = meteriaMax;
                }

                local$maxMeteria = meteriaMax;
                for(Building b : buildsInRange) {
                    if(b instanceof MeteriaSource.MeteriaSourceBuild) {
                        meteria = local$maxMeteria;
                    }

                    if(b instanceof MeteriaCrafter.MeteriaCrafterBuild) {
                        ((MeteriaCrafter.MeteriaCrafterBuild) b).meteria = sub(((MeteriaCrafter.MeteriaCrafterBuild) b).meteria);
                    }

                    if(b instanceof MeteriaReceiverBuild b2) {
                        ((MeteriaReceiverBuild) b).meteria(add(b2.meteria(), b2.meteriaCapacity()));
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

    public static class MeteriaNodeBooster extends Types.ModBlock {
        public float meteriaBoost;

        public MeteriaNodeBooster(String name) {
            super(name);
        }
    }

    public static class MeteriaSource extends Types.ModBlock {
        public MeteriaSource(String name) {
            super(name);
        }

        public class MeteriaSourceBuild extends Types.ModBlock.ModBlockBuild {
        }
    }

    public static class MeteriaCrafter extends Types.ModCrafter {
        public float meteriaGet;
        public float maxMeteria;

        public MeteriaCrafter(String name) {
            super(name);
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("meteria", (MeteriaCrafterBuild build) -> new Bar(
                    () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / maxMeteria) * 100) + "%",
                    () -> ThePal.meteria,
                    () -> build.meteria / maxMeteria
            ));
        }

        public class MeteriaCrafterBuild extends ModCrafterBuild {
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

    public static class MeteriaPlant extends Types.ModCrafter {
        public float maxMeteria;
        public float meteriaConsume;

        public MeteriaPlant(String name) {
            super(name);
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("meteria", (MeteriaPlantBuild build) -> new Bar(
                    () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / maxMeteria) * 100) + "%",
                    () -> ThePal.meteria,
                    () -> build.meteria / maxMeteria
            ));
        }

        public class MeteriaPlantBuild extends ModCrafterBuild implements MeteriaReceiverBuild {
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

    public interface MeteriaReceiverBuild {
        float meteria();
        float meteriaCapacity();

        void meteria(float meteria);
    }

    public static class MeteriaDrill extends Types.ModDrill {
        public float maxMeteria;
        public float meteriaConsume;

        public MeteriaDrill(String name) {
            super(name);
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("meteria", (MeteriaDrillBuild build) -> new Bar(
                    () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / maxMeteria) * 100) + "%",
                    () -> ThePal.meteria,
                    () -> build.meteria / maxMeteria
            ));
        }

        public class MeteriaDrillBuild extends ModDrillBuild implements MeteriaReceiverBuild {
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
}