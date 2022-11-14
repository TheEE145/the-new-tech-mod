package tntm.brutality;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import tntm.graphics.TntmPal;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Draw.reset;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;

public class BloodFx extends Effect {
    public BloodFx(float lifetime, float scale) {
        super(lifetime, scale, b -> {
            b.scaled(scale, e -> {
                color(TntmPal.blood);
                stroke((1.7f * e.fout()) * 0.5f);

                Draw.z(Layer.effect + 0.001f);
                randLenVectors(e.id + 1, e.finpow() + 0.001f, 9, 40f, (x, y, in, out) -> {
                    color(TntmPal.blood);
                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 16);

                    Drawf.light(e.x + x, e.y + y, out * 56, Draw.getColor(), 0.8f);
                });

                reset();
            });
        });
    }
}
