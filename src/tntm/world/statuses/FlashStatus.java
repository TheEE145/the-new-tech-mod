package tntm.world.statuses;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import tntm.graphics.TntmDraw;

import static mindustry.Vars.player;

public class FlashStatus extends TntmStatusEffect {
    public Color flashColor = Color.white;

    public FlashStatus(String name) {
        super(name);
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        if(player.unit() == unit) {
            Draw.draw(Layer.max, () -> {
                Draw.color(flashColor);
                Fill.rect(0, 0, TntmDraw.SCREEN_BOUNDS, TntmDraw.SCREEN_BOUNDS);
            });
        }
    }
}