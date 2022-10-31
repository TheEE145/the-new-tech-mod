package the.mod.types;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.production.*;
import the.mod.*;
import the.mod.content.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Meteria {
    public static class MeteriaNode extends Wall {
        public float maxMeteria;
        public int range;

        public MeteriaNode(String name) {
            super(name);

            localizedName = TheTech.prefix + " " + localizedName;
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("@meteria.name", (MeteriaNodeBuild build) -> new Bar(
                    () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / build.local$maxMeteria) * 100) + "%",
                    () -> Color.purple,
                    () -> build.meteria / build.local$maxMeteria
            ));

            addBar("@meteria.boost", (MeteriaNodeBuild build) -> new Bar(
                    () -> bundle.get("meteria.boost") + ": " + (int) Math.floor(build.local$maxMeteria - maxMeteria),
                    () -> Color.purple,
                    () -> (int) Math.floor(build.local$maxMeteria - maxMeteria) == 0 ? 0 : 1
            ));

            addBar("@meteria.links", (MeteriaNodeBuild build) -> new Bar(
                    () -> build.blocksInRange.size == 0 ?
                            bundle.get("meteria.nolinks") : bundle.get("meteria.links") + ": " + build.blocksInRange.size,

                    () -> Color.orange,
                    () -> build.blocksInRange.size > 0 ? 1 : 0
            ));
        }

        public class MeteriaNodeBuild extends WallBuild {
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

            public void drawField() {
                Draw.draw(Layer.max, () -> {
                    Draw.color(Color.purple);
                    Lines.stroke(2.5f);
                    Lines.rect(x - range(), y - range(), range()*2, range()*2);
                    Draw.flush();
                });
            }

            public float range() {
                return range * 8;
            }

            public void drawLinks() {
                Draw.draw(Layer.max, () -> {
                    Draw.color(Color.purple);
                    Lines.stroke(1.5f);

                    Vec2 vec;
                    float sie;
                    for(int i = 0; i < blocksInRange.size; i++) {
                        vec = blocksInRangeP.get(i);
                        sie = blocksInRange.get(i).size * 8;

                        Lines.rect(vec.x - sie/2 - ratio, vec.y - sie/2 - ratio, sie + ratio * 2, sie + ratio * 2);
                    }

                    Draw.flush();
                });
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

                Tile tile;
                Block block;
                for(int x = tileX() - range; x < tileX() + range; x++) {
                    for(int y = tileY() - range; y < tileY() + range; y++) {
                        if(x < 0 || y < 0) {
                            continue;
                        }

                        if(x > world.width() || y > world.height()) {
                            continue;
                        }

                        tile = world.tile(x, y);
                        if(tile == null) {
                            continue;
                        }

                        if(tile.build == this) {
                            continue;
                        }

                        block = tile.block();
                        if(block instanceof MeteriaNodeBooster) {
                            addLink(tile);
                        }

                        if(block instanceof MeteriaNode) {
                            addLink(tile);
                        }

                        if(block instanceof MeteriaSource) {
                            addLink(tile);
                        }

                        if(block instanceof MeteriaCrafter) {
                            addLink(tile);
                        }
                    }
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
                drawField();

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
                }
            }

            @Override
            public void updateTile() {
                super.updateTile();
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

    public static class MeteriaNodeBooster extends Wall {
        public float meteriaBoost;

        public MeteriaNodeBooster(String name) {
            super(name);

            localizedName = TheTech.prefix + " " + localizedName;
        }
    }

    public static class MeteriaSource extends Wall {
        public MeteriaSource(String name) {
            super(name);

            localizedName = TheTech.prefix + " " + localizedName;
        }

        public class MeteriaSourceBuild extends WallBuild {
        }
    }

    public static class MeteriaCrafter extends GenericCrafter {
        public float meteriaGet;
        public float maxMeteria;

        public MeteriaCrafter(String name) {
            super(name);

            localizedName = TheTech.prefix + " " + localizedName;
        }

        @Override
        public void setBars() {
            super.setBars();

            addBar("@meteria.name", (MeteriaCrafterBuild build) -> new Bar(
                    () -> bundle.get("meteria.name") + ": " + (int) Math.floor((build.meteria / maxMeteria) * 100) + "%",
                    () -> Color.purple,
                    () -> build.meteria / maxMeteria
            ));
        }

        public class MeteriaCrafterBuild extends GenericCrafterBuild {
            public float meteria = 0;

            @Override
            public void craft() {
                super.craft();
                meteria += meteriaGet;
                if(meteria > maxMeteria) {
                    meteria = maxMeteria;
                }
            }
        }
    }
}