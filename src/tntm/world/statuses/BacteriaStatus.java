package tntm.world.statuses;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class BacteriaStatus extends TntmStatusEffect {
    public Color aColor;
    public float alpha;

    public BacteriaStatus(String name) {
        super(name);
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);

        Draw.draw(Layer.shields - 1, () -> {
            Draw.color(aColor);
            Draw.alpha(alpha);
            Draw.rect(unit.type.shadowRegion, unit.x, unit.y, unit.rotation - 90);
            Draw.flush();
        });
    }
}