package tntm.world.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import tntm.content.TntmFx;
import tntm.graphics.TntmDraw;
import tntm.utils.TntmBullets;
import tntm.utils.TntmMath;

public class BlockBulletType extends BasicBulletType {
    public float maxScale, startScale;
    public float fragOffset;
    public Block block;

    public BlockBulletType(Block block) {
        super();

        this.block = block;
        collides = hittable = absorbable = reflectable = keepVelocity = false;
        TntmBullets.setup(this);
        trailColor = hitColor = Color.valueOf("ea8878");

        trailLength = 0;
        startScale = block.size * 8;
        maxScale = startScale * 2;
        shrinkY = shrinkX = 0;

        hitEffect = despawnEffect = TntmFx.explosion;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        b.data = new BData();
    }

    @Override
    public void load() {
        frontRegion = backRegion = block.uiIcon;
    }

    public float path(Bullet b) {
        return b.fin() > 0.5f ? 1 - b.fin() : b.fin();
    }

    public float calculateScale(Bullet b) {
        return startScale + maxScale * path(b);
    }

    @Override
    public void drawLight(Bullet b) {
        if(lightOpacity <= 0f || lightRadius <= 0f) {
            return;
        }

        Drawf.light(b, lightRadius * (1 - path(b)), lightColor, lightOpacity);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        if(b.data instanceof BData data) {
            float range = Math.max(splashDamageRadius / 2, homingRange);
            Teamc target = data.target = Units.bestTarget(b.team, b.x, b.y, range,
                    e -> !e.dead() && e.checkTarget(collidesAir, collidesGround),
                    t -> !t.dead() && collidesGround, UnitSorts.closest
            );

            if(homing(b) && target != null && b.within(target, homingRange)) {
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
            }
        }

        if(b.fin() > 0.5f && fragBullet != null && fragBullets > 0) {
            b.remove();
        }
    }

    @Override
    public void createFrags(Bullet b, float xx, float yy) {
        if(fragBullet != null && fragBullets > 0 && b.data instanceof BData data) {
            float x = !homing(b) ? data.targetPos.x : data.target.x();
            float y = !homing(b) ? data.targetPos.y : data.target.y();

            for(int i = 0; i < fragBullets; i++) {
                float lx = Mathf.random(x - fragOffset, x + fragOffset);
                float ly = Mathf.random(y - fragOffset, y + fragOffset);

                Bullet bullet = fragBullet.create(b, xx, yy, Angles.angle(xx, yy, lx, ly));
                bullet.lifetime = Math.min(fragBullet.lifetime, TntmMath.len(lx, xx, ly, yy) / fragBullet.speed);

                if(fragBullet instanceof BlockBulletType) {
                    BData dat = (BData) (bullet.data = new BData());

                    bullet.lifetime = Math.min(fragBullet.lifetime, TntmMath.len(lx, bullet.x, ly, bullet.y) / fragBullet.speed);
                    dat.targetPos = new Vec2(lx, ly);
                }
            }
        }
    }

    public boolean homing(Bullet b) {
        return homingPower > 0.0001f && b.time >= homingDelay;
    }

    @Override
    public void draw(Bullet b) {
        if(trailLength > 0) {
            drawTrail(b);
        }

        float height = this.height + calculateScale(b);
        float width = this.width + calculateScale(b);

        if(b.data instanceof BData data && data.target != null && homing(b)) {
            Draw.draw(Layer.overlayUI, () -> {
                TntmDraw.drawTarget(data.target.x(), data.target.y(), splashDamageRadius, b.team.color);
            });
        }

        if(b.data instanceof BData data && data.targetPos != null) {
            TntmDraw.drawTarget(data.targetPos.x, data.targetPos.y, splashDamageRadius, b.team.color);
        }

        Draw.draw(Layer.bullet + 20.345f, () -> {
            Draw.color(Color.white);
            Draw.alpha(1);

            Draw.rect(frontRegion, b.x, b.y, width, height);
            Draw.flush();
        });

        Draw.reset();
    }

    public static class BData {
        public Vec2 targetPos;
        public Teamc target;
    }
}