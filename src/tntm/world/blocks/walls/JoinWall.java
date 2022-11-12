package tntm.world.blocks.walls;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.Tile;
import tntm.asset.TntmAssets;

import static mindustry.Vars.world;

public class JoinWall extends TntmBlock {
    public TextureRegion[] regions;
    public boolean healthMove = false;

    public JoinWall(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();

        regions = new TextureRegion[5];
        for(int i = 0; i < 5; i++) {
            regions[i] = TntmAssets.get(name + "-" + (i + 1));
        }
    }

    public class JoinWallBuild extends TntmBlockBuild {
        @Override
        public void updateTile() {
            super.updateTile();

            if(healthMove && haveConnections()) {
                float[] h = { health };
                Seq<JoinWallBuild> prox = prox();
                prox.each(b -> h[0] += b.health);

                h[0] = h[0] / (prox.size + 1);
                prox.each(b -> b.health = h[0]);
            }
        }

        @Override
        public void draw() {
            if(!haveConnections()) {
                super.draw();
                return;
            }

            if(heightConnections() && widthConnections()) {
                draw(3);
                return;
            }

            Seq<JoinWallBuild> prox = prox();
            if(prox.size == 2 && (heightConnections() || widthConnections())) {
                draw(4, heightConnections() ? 90 : 0);
                return;
            }

            if(prox.size == 2) {
                if(leftx()) {
                    draw(1, -90 + (top() ? -90 : 0));
                    return;
                }

                if(rightx()) {
                    draw(1, top() ? 90 : 0);
                    return;
                }
            }

            if(prox.size == 1) {
                if(top()) {
                    draw(0, 90);
                }

                if(bottom()) {
                    draw(0, -90);
                }

                if(leftx()) {
                    draw(0, 180);
                }

                if(rightx()) {
                    draw(0);
                }

                return;
            }

            if(prox.size == 3) {
                if(heightConnections()) {
                    draw(2, 90 + (leftx() ? 180 : 0));
                    return;
                }

                if(widthConnections()) {
                    draw(2, top() ? 180 : 0);
                }
            }
        }

        public void draw(int id) {
            draw(id, 0);
        }

        public boolean leftx() {
            return isThis(-1, 0);
        }

        public boolean rightx() {
            return isThis(1, 0);
        }

        public boolean top() {
            return isThis(0, 1);
        }

        public boolean bottom() {
            return isThis(0, -1);
        }

        public void draw(int id, float angle) {
            Draw.rect(regions[id], x, y, angle);
        }

        public boolean isThis(int x, int y) {
            return world.tile(x + tileX(), y + tileY()).build instanceof JoinWallBuild;
        }

        public boolean heightConnections() {
            return isThis(0, 1) && isThis(0, -1);
        }

        public boolean widthConnections() {
            return isThis(1, 0) && isThis(-1, 0);
        }

        public Seq<JoinWallBuild> prox() {
            Seq<JoinWallBuild> x = new Seq<>();
            for(Building b : proximity) {
                if(b instanceof JoinWallBuild b2) {
                    x.add(b2);
                }
            }

            return x;
        }

        public boolean haveConnections() {
            return prox().any();
        }
    }
}