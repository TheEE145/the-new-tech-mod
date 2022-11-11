package tntm.world.blocks;

import arc.func.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.struct.Seq;
import static mindustry.Vars.*;

import mindustry.gen.*;
import mindustry.graphics.*;
import tntm.graphics.TntmDraw;
import tntm.utils.TntmMath;
import tntm.world.blocks.walls.TntmBlock;

public class RadiusBlock extends TntmBlock {
    public Cons<RadiusBlockBuild> after;
    public Color radColor, outlineColor = Pal.darkOutline;
    public boolean colorByTeam;
    public float radAlpha = 0.40f;
    public float stroke = 2;
    public float radius;

    public RadiusBlock(String name) {
        super(name);
    }

    public class RadiusBlockBuild extends TntmBlockBuild {
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
                TntmDraw.radius(x, y, radius, stroke, outlineColor, colorx, radAlpha);
                Draw.flush();
            });

            if(after != null) {
                after.get(this);
            }
        }

        public boolean collision(float x, float y, float radius) {
            return TntmMath.collision(x, y, this.x, this.y, radius);
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