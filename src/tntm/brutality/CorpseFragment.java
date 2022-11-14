package tntm.brutality;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.entities.Effect;
import mindustry.graphics.Layer;
import tntm.content.TntmFx;
import tntm.graphics.TntmDraw;
import tntm.graphics.TntmPal;

public class CorpseFragment {
    public void draw(Corpse.CorpseBuild corpse, float x, float y) {
    }

    public static class BloodFrag extends CorpseFragment {
        public float radius;

        public BloodFrag(float radius) {
            this.radius = radius;
        }

        @Override
        public void draw(Corpse.CorpseBuild corpse, float x, float y) {
            if(!Brutality.bloodEnabled()) {
                return;
            }

            Draw.color(TntmPal.blood);
            Draw.alpha(corpse.fin() * 0.5f);
            Fill.circle(x, y, radius);
            Draw.reset();
        }
    }

    public static class BloodFxFrag extends CorpseFragment {
        @Override
        public void draw(Corpse.CorpseBuild corpse, float x, float y) {
            TntmFx.noWeapon.at(x, y);
        }
    }
}