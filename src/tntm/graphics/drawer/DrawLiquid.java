package tntm.graphics.drawer;

import arc.graphics.g2d.Draw;
import mindustry.type.LiquidStack;

public class DrawLiquid extends Drawer {
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