package tntm.world.blocks.liquids;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import tntm.TheTech;
import tntm.asset.TntmAssets;
import tntm.world.blocks.walls.TntmBlock;

import static mindustry.Vars.content;
import static mindustry.Vars.world;

public class LiquidUnloader extends TntmBlock {
    public TextureRegion centerRegion;
    public Seq<Tile> nearbyBlocks;

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }

    public LiquidUnloader(String name) {
        super(name);
        update = solid = hasLiquids = configurable = saveConfig = noUpdateDisabled = true;

        health = 70;
        liquidCapacity = 0;

        config(Liquid.class, (LiquidUnloaderBuild tile, Liquid liquid) -> tile.config = liquid);
        configClear((LiquidUnloaderBuild build) -> build.config = Liquids.water);
    }

    @Override
    public void load() {
        super.load();
        centerRegion = TntmAssets.get(name + "-center");
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");
        removeBar("liquid-");
    }

    public class LiquidUnloaderBuild extends TntmBlockBuild {
        public Liquid config = Liquids.water;

        @Override
        public void draw() {
            super.draw();

            if (config == null) {
                config = Liquids.water;
            }

            Draw.draw(Layer.block, () -> {
                Draw.color(config.color);
                Draw.rect(centerRegion, x, y);
                Draw.flush();
            });

            nearbyBlocks();
            float total = 0;
            for (Tile tile : requireNotConduit()) {
                total += tile.build.liquids.get(config);
            }

            total = total / nearbyBlocks.size;
            for (Tile t : nearbyBlocks) {
                t.build.liquids.remove(config, t.build.liquids.get(config));
                t.build.liquids.add(config, total);
            }

            dumpLiquid(config);
        }

        public boolean needLiquid(Block block) {
            for (Consume e : block.consumers) {
                if (e instanceof ConsumeLiquid) {
                    if (((ConsumeLiquid) e).liquid == config) {
                        return true;
                    }
                }
            }

            return false;
        }

        public boolean canBeUnloaded(Tile tile, int rotation) {
            if (tile == null) {
                return false;
            }

            Block block = tile.block();
            if (block == null) {
                return false;
            }

            if (block.hasLiquids) {
                if (block instanceof Conduit) {
                    Building build = tile.build;

                    if (build == null) {
                        return false;
                    }

                    return rotation != build.rotation;
                }

                return !(block instanceof LiquidUnloader) && needLiquid(block);
            }

            return false;
        }

        public Seq<Tile> requireNotConduit() {
            Seq<Tile> result = new Seq<>();
            for (Tile b : nearbyBlocks) {
                if (!(b.block() instanceof Conduit)) {
                    result.add(b);
                }
            }

            return result;
        }

        public void nearbyBlocks() {
            nearbyBlocks = new Seq<>();
            Tile tile;

            tile = world.tile(tileX(), tileY() + 1);
            if (canBeUnloaded(tile, 3)) {
                nearbyBlocks.add(tile);
            }

            tile = world.tile(tileX() + 1, tileY());
            if (canBeUnloaded(tile, 2)) {
                nearbyBlocks.add(tile);
            }

            tile = world.tile(tileX(), tileY() - 1);
            if (canBeUnloaded(tile, 1)) {
                nearbyBlocks.add(tile);
            }

            tile = world.tile(tileX() - 1, tileY());
            if (canBeUnloaded(tile, 0)) {
                nearbyBlocks.add(tile);
            }
        }

        @Override
        public Liquid config() {
            return config;
        }

        public void configureERR(Object value) {
            super.configure(value);
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(config.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            config = content.liquid(read.i());
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(LiquidUnloader.this, table, content.liquids(), () -> config, this::configureERR);
        }
    }
}