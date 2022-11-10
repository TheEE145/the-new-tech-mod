package the.mod.utils;

import arc.graphics.Color;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import the.mod.content.Effects;

public class Bullets {
    public static void setup(BulletType type, Color color, float width) {
        type.hitEffect = type.despawnEffect = Effects.bulletCollision;

        type.trailChance = 1f;
        type.trailLength = 24;
        type.trailColor = color;
        type.trailWidth = width/2;
    }

    public static void setup(BulletType type, float width) {
        setup(type, Color.white, width);
    }

    public static void setup(BasicBulletType type, Color color) {
        setup(type, color, type.width);
    }

    public static void setup(BasicBulletType type) {
        setup(type, type.width);
    }
}