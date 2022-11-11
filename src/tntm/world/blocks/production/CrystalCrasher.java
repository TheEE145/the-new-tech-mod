package tntm.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import mindustry.content.Blocks;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import tntm.world.blocks.enviroment.CrystalBlock;
import tntm.world.blocks.walls.TntmBlock;

import static mindustry.Vars.world;

public class CrystalCrasher extends TntmBlock {
    public int length;

    public CrystalCrasher(String name) {
        super(name);

        hasItems = configurable = true;
        config(Boolean.class, (CrystalCrasherBuild build, Boolean ignored) -> {});
    }

    @SuppressWarnings("all")
    public class CrystalCrasherBuild extends TntmBlockBuild {
        public boolean started = false, ended = false;
        public float progress;

        public void handler(int x, int y) {
            Tile tile = world.tile(x, y);
            if(tile.block() instanceof CrystalBlock) {
                tile.block().destroyEffect.at(tile.build.x, tile.build.y);

                CrystalBlock b = (CrystalBlock) tile.block();
                items.add(b.item, b.amount);
                tile.setNet(Blocks.air);
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            table.table(t -> {
                TextButton b = t.button(Core.bundle.get("start"), () -> started = true).size(160f, 50f).get();
                b.update(() -> {
                    b.visible(() -> !started);
                });
            });
        }

        public float sd() {
            return (progress / 100) * (length * 8);
        }

        @Override
        public void draw() {
            super.draw();

            Draw.draw(Layer.blockBuilding - 0.1f, () -> {
                if(started && !ended) {
                    Draw.color(Color.sky);
                    Lines.stroke(1.5f);

                    Draw.alpha(0.30f);
                    Fill.rect(x, y, sd()*2, sd()*2);

                    Draw.alpha(progress / 100);
                    Lines.rect(x - sd(), y - sd(), sd()*2, sd()*2);
                }
            });

            if(started && !ended) {
                progress++;

                if(progress == 100) {
                    ended = true;

                    for(int x = tileX() - length; x < tileX() + length; x++) {
                        for(int y = tileY() - length; y < tileY() + length; y++) {
                            handler(x, y);
                        }
                    }
                }
            }

            dump();
        }
    }
}