package the.mod.utils;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import mindustry.graphics.Pal;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import the.mod.TheTech;

import java.util.Arrays;

public class Drawer {
    TextureRegion region;
    String prefix;

    public Drawer(String prefix) {
        this.prefix = prefix;
    }

    public <T extends Block> void load(T block) {
        region = TheTech.get(block.name + prefix);
    }

    public void update() {
    }

    public TextureRegion region() {
        return region;
    }

    public void draw(float x, float y) {
        Draw.rect(region, x, y);
    }

    public static class BaseDrawer extends Drawer {
        public BaseDrawer() {
            super("");
        }
    }

    public static class Plan {
        public String prefix, type;

        public Plan(String type) {
            this("", type);
        }

        public Plan(String prefix, String type) {
            this.prefix = prefix;
            this.type = type;
        }

        public Drawer get() {
            if(type.equals("rotor")) {
                return new DrawRotor(prefix);
            }

            if(type.equals("heat")) {
                return new HeatDrawer(prefix);
            }

            if(type.equals("base")) {
                return new BaseDrawer();
            }

            if(type.equals("liquid")) {
                return new DrawLiquid(prefix);
            }

            return new Drawer(prefix);
        }
    }

    public static class DrawRotor extends Drawer {
        public Tacker.RotationTicker tacker;
        public float rotation;

        public DrawRotor(String prefix) {
            super(prefix);
        }

        @Override
        public void update() {
            tacker.tack(rotation);
        }

        @Override
        public void draw(float x, float y) {
            if(tacker == null) {
                return;
            }

            Draw.rect(region, x, y, tacker.rotation);
        }
    }

    public static class HeatDrawer extends Drawer {
        public float heat = 0;
        public Color color = Pal.turretHeat;

        public HeatDrawer(String prefix) {
            super(prefix);
        }

        @Override
        public void draw(float x, float y) {
            if(heat > 0) {
                Draw.blend(Blending.additive);
                Draw.color(color, heat);
                Draw.rect(region, x, y);
                Draw.blend();
                Draw.color();
            }
        }
    }

    public static class DrawLiquid extends Drawer {
        public LiquidStack stack;
        public float cap;

        public DrawLiquid(String prefix) {
            super(prefix);
        }

        @Override
        public void draw(float x, float y) {
            if(stack == null) {
                return;
            }

            Draw.color(stack.liquid.color);
            Draw.alpha(stack.amount / cap);
            Draw.rect(region, x, y);
            Draw.color();
        }
    }

    public static class MultiDrawer extends Drawer {
        public Drawer[] drawers;

        public MultiDrawer() {
            super("");
        }

        @Override
        public <T extends Block> void load(T block) {
            for(Drawer d : drawers) {
                d.load(block);
            }
        }

        @Override
        public void draw(float x, float y) {
            for(Drawer d : drawers) {
                d.draw(x, y);
            }
        }

        public MultiDrawer get() {
            MultiDrawer x = new MultiDrawer();
            x.drawers = new Drawer[drawers.length];
            System.arraycopy(this.drawers, 0, x.drawers, 0, x.drawers.length);
            return x;
        }
    }
}