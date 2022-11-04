package the.mod.types;

import arc.func.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.struct.Seq;
import static mindustry.Vars.*;

import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import the.mod.TheTech;
import the.mod.utils.*;

public class RadiusBlock extends Types.ModBlock {
    public static boolean collision(float x1, float y1, float x2, float y2, float radius) {
        return TheTech.len(x1, x2, y1, y2) < radius;
    }

    public Cons<RadiusBlockBuild> after;
    public Color radColor, outlineColor = Pal.darkOutline;
    public boolean colorByTeam;
    public float radAlpha = 0.40f;
    public float stroke = 2;
    public float radius;

    public RadiusBlock(String name) {
        super(name);
    }

    public class RadiusBlockBuild extends ModBlockBuild {
        public Color colorx;

        public RadiusBlockBuild() {
            super();

            //with some reason this need
            flashColor = Color.white;
        }

        @Override
        public void draw() {
            super.draw();

            colorx = colorByTeam ? team.color : radColor;
            Draw.draw(Layer.buildBeam - 10, () -> {
                Drawx.radius(x, y, radius, stroke, outlineColor, colorx, radAlpha);
                Draw.flush();
            });

            if(after != null) {
                after.get(this);
            }
        }

        public boolean collision(float x, float y, float radius) {
            return TheTech.len(this.x, x, this.y, y) < radius;
        }

        public boolean collision(float x, float y) {
            return collision(x, y, radius);
        }

        public Seq<Building> buildings(Boolf<Building> boolf) {
            return buildings(boolf, radius);
        }

        public Seq<Building> buildings(Boolf<Building> boolf, float radius) {
            Seq<Building> buildings = new Seq<>();
            world.tiles.eachTile(t -> {
                if(t.build == null) {
                    return;
                }

                if(boolf.get(t.build)) {
                    if(collision(t.worldx(), t.worldy(), radius)) {
                        buildings.add(t.build);
                    }
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