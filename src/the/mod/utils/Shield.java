package the.mod.utils;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.graphics.Layer;
import the.mod.TheTech;

//noise alpha - 212 or 212f/255f (0.83137254901960784313725490196078)
//noise - (intensity = 42, color = 0, coverage = 100). color: A4B8FA
public class Shield {
    public TextureRegion region;
    public float maxHealth = 100;
    public float reloadTime = 60;

    private float progress = 0;
    private float health;
    private boolean tmp;

    public float health() {
        return health/maxHealth;
    }

    public boolean isBroken() {
        return progress > 0;
    }

    public float progress() {
        return 1 - progress/reloadTime;
    }

    public Shield() {
        health = maxHealth;
    }

    public Effect brokeEffect;

    public void renderer(float x, float y) {
        Draw.draw(Layer.shields, () -> {
            Draw.alpha(isBroken() ? progress() : 1f);
            Draw.rect(region, x, y, region.width + 5, region.height + 5);
        });

        if(progress > 0) {
            progress--;
        }

        if(progress <= 0 && !tmp) {
            tmp = true;
            progress = 0;
            health = maxHealth;
        }

        if(health <= 0 && !isBroken()) {
            progress = reloadTime;

            if(tmp) {
                if(brokeEffect != null) {
                    brokeEffect.at(x, y);
                }

                tmp = false;
            }
        }
    }

    public float damage(float damage) {
        if(health <= 0) {
            return damage;
        }

        health -= damage;

        if(health > maxHealth) {
            health = maxHealth;
        }

        if(health < 0) {
            return damage + health;
        }

        return 0;
    }

    public void kill() {
        damage(health);
    }
}