package tntm.world.effects;

import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.entities.Effect;
import tntm.graphics.TntmDraw;

import static arc.graphics.g2d.Draw.color;

public class SparkEffect extends Effect {
    public SparkEffect(float lifetime, int count, float length, float max, boolean f, float sides) {
        super(lifetime, e -> {
            e.scaled(25f, s -> {
                color(Color.white, e.color, f ? s.finpow() : s.fin());
                TntmDraw.randomVectors(e.x, e.y, e.id, count, length, (x, y, i) -> {
                    TntmDraw.sideShard(x, y, 5f * s.fout() + Mathf.randomSeed(e.id + i, max), 4f * s.fout(), sides, Mathf.angle(x - e.x, y - e.y));
                });
            });
        });
    }
}