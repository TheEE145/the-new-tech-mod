package the.mod.utils;

import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import the.mod.TheTech;

import static mindustry.Vars.*;
import static the.mod.TheTech.mod;

public class Types {
    public static class ModItemTurret extends ItemTurret {
        public ModItemTurret(String name) {
            super(name);

            localizedName = TheTech.prefix(localizedName);
        }

        public class ModItemTurretBuild extends ItemTurretBuild {
        }
    }

    public static class ModCore extends CoreBlock {
        public ModCore(String name) {
            super(name);

            localizedName = TheTech.prefix(localizedName);
        }

        public class ModCoreBuild extends CoreBuild {
        }
    }

    public static class ModCrafter extends GenericCrafter {
        public ModCrafter(String name) {
            super(name);

            localizedName = TheTech.prefix(localizedName);
        }

        public class ModCrafterBuild extends GenericCrafterBuild {
        }
    }

    public static class ModBlock extends Wall {
        public float dynamicEffectChange = 0f;
        public Effect dynamicEffect = Fx.none;
        public boolean canBurn = true;

        public ModBlock(String name) {
            super(name);

            localizedName = TheTech.prefix(localizedName);

            flashHit = true;
            flashColor = null;
            update = true;
        }

        public class ModBlockBuild extends WallBuild {
            public void extinguish(float x, float y) {
                Fires.extinguish(world.tileWorld(this.x + x, this.y + y), 9000);
            }

            public float diner() {
                return Mathf.range(size * 2.356f);
            }

            @Override
            public void updateTile() {
                super.updateTile();

                if(!canBurn) {
                    extinguish(2, 2);
                    for(Point2 p : Geometry.d8) {
                        extinguish(p.x * tilesize, p.y * tilesize);
                    }
                }
            }

            @Override
            public void update() {
                super.update();
                if(Mathf.chanceDelta(dynamicEffectChange)) {
                    dynamicEffect.at(x + diner(), y + diner());
                }
            }
        }
    }

    public static class ModItem extends Item {
        public ModItem(String name, Color color) {
            super(name, color);

            localizedName = TheTech.prefix(localizedName);
        }
    }

    public static class ModDrill extends Drill {
        public ModDrill(String name) {
            super(name);

            localizedName = TheTech.prefix(localizedName);

            hasItems = true;
            itemCapacity = 15;
        }

        public class ModDrillBuild extends DrillBuild {
        }
    }

    public static class LiquidUnloader extends ModBlock {
        public TextureRegion centerRegion;
        public Seq<Tile> nearbyBlocks;

        public LiquidUnloader(String name) {
            super(name);
            update = solid = hasLiquids = configurable = saveConfig = noUpdateDisabled = true;

            health = 70;
            liquidCapacity = 0;

            config(Liquid.class, (LiquidUnloaderBuild tile, Liquid liquid) -> tile.config = liquid);
            configClear((LiquidUnloaderBuild build) -> build.config = Liquids.water);
            //centerRegion = mod(name + "-center");
        }

        /*
            @Override
            public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list){
                drawPlanConfigCenter(plan, plan.config, name + "-center");
            }
         */

        @Override
        public void setBars(){
            super.setBars();
            removeBar("liquid");
            removeBar("liquid-");
        }

        public class LiquidUnloaderBuild extends ModBlockBuild {
            public Liquid config = Liquids.water;

            @Override
            public void draw() {
                super.draw();
                /**
                 *                 Draw.draw(Layer.block, () -> {
                 *                     Draw.color(Color.purple); //config.color
                 *                     Draw.rect(centerRegion, x, y);
                 *                     Draw.flush();
                 *                 });
                 */

                if (config == null) {
                    config = Liquids.water;
                }

                Draw.draw(Layer.turret, () -> {
                    Draw.color(config.color);
                    Draw.rect(config.uiIcon, x, y, size * 5, size * (config.gas ? 5 : 7));
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

    public static class ModFloor extends Floor {
        public ModFloor(String name) {
            super(name);
            localizedName = TheTech.prefix(localizedName);
        }

        public ModFloor(String name, int variants) {
            super(name, variants);
            localizedName = TheTech.prefix(localizedName);
        }
    }

    public static class ModStaticWall extends StaticWall {
        public ModStaticWall(String name) {
            super(name);
            localizedName = TheTech.prefix(localizedName);
        }
    }

    public static class ModLiquid extends Liquid {
        public ModLiquid(String name, Color color) {
            super(name, color);
            localizedName = TheTech.prefix(localizedName);
        }
    }
}