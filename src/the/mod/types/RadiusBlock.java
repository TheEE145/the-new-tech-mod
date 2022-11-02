package the.mod.types;

import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.struct.Seq;
import static mindustry.Vars.*;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import the.mod.TheTech;
import the.mod.utils.Types;

public class RadiusBlock extends Types.ModBlock {
    public Cons<RadiusBlockBuild> after;
    public Color radColor, outlineColor;
    public boolean colorByTeam;
    public float radius;

    public RadiusBlock(String name) {
        super(name);
    }

    public class RadiusBlockBuild extends ModBlockBuild {
        @Override
        public void draw() {
            super.draw();

            if(colorByTeam) {
                radColor = team.color;
            }

            float segments = radius > 360 ? 175 : radius;
            float len = 360/segments - 5;
            float od = len/360;
            float angle = 0;
            for(int i = 0; i < segments; i++) {
                Lines.stroke(2.5f);
                Draw.color(outlineColor);
                Lines.arc(x, y, radius, od, angle);
                Lines.stroke(2f);
                Draw.color(radColor);
                Lines.arc(x, y, radius, od, angle);
                angle += len + 5;
            }

            if(after != null) {
                after.get(this);
            }
        }

        public boolean collision(float x, float y) {
            return TheTech.len(this.x, x, this.y, y) < radius;
        }

        public Block toBlock(Building build) {
            if(build == null) {
                return null;
            }

            return world.tile(build.tileX(), build.tileY()).block();
        }

        public Seq<Building> buildings() {
            Seq<Building> buildings = new Seq<>();
            world.tiles.eachTile(t -> {
                if(t.build == null) {
                    return;
                }

                if(collision(t.worldx(), t.worldy())) {
                    buildings.add(t.build);
                }
            });

            return buildings;
        }

        public Seq<Unit> units() {
            Seq<Unit> units = new Seq<>();
            Groups.unit.each(u -> {
                if(collision(u.x, u.y)) {
                    units.add(u);
                }
            });

            return units;
        }
    }
}