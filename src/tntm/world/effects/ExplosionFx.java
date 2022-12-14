package tntm.world.effects;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static arc.math.Mathf.rand;

public class ExplosionFx extends Effect {
    public ExplosionFx(float lifetime, float scale) {
        super(lifetime, scale, b -> {
            color(Color.gray);
            //TODO awful borders with linear filtering here
            alpha(0.9f);
            float intensity = 1;
            for(int i = 0; i < 4; i++){
                rand.setSeed(b.id * 2L + i);

                int fi = i;
                b.scaled(scale * 2, e -> {
                    randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int) (3f * intensity), 14f * intensity, (x, y, in, out) -> {
                        float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                        Fill.circle(e.x + x, e.y + y, fout * ((2f + intensity) * 1.8f));
                    });
                });
            }

            b.scaled(scale, e -> {
                e.scaled(5 + intensity * 2.5f, i -> {
                    stroke((3.1f + intensity/5f) * i.fout());
                    Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                    Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
                });

                color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
                stroke((1.7f * e.fout()) * (1f + (intensity - 1f) / 2f));

                Draw.z(Layer.effect + 0.001f);
                randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(9 * intensity), 40f * intensity, (x, y, in, out) -> {
                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (3f + intensity));
                    Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                });
            });
        });
    }
}