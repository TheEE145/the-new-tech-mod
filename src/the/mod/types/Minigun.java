package the.mod.types;

import mindustry.entities.bullet.BulletType;
import mindustry.ui.Bar;

import the.mod.utils.Tacker.*;
import the.mod.utils.Types.*;

import arc.graphics.Color;
import static arc.Core.*;

public class Minigun extends ModItemTurret {
    public Minigun(String name) {
        super(name);

        reload = 0;
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("@minigun.heat", (MinigunBuild e) -> {
            return new Bar(
                    () -> e.tacker.cool ? bundle.get("minigun.cool") : bundle.get("minigun.heat") + ": " + (int) e.tacker.heat + "%",
                    () -> e.tacker.cool ? Color.cyan : Color.orange,
                    () -> e.tacker.heat / 100f
            );
        });
    }

    public class MinigunBuild extends ModItemTurretBuild {
        MinigunLikeTicker tacker;
        final float delta = 1;
        float timer = 0;

        public MinigunBuild() {
            super();

            tacker = new MinigunLikeTicker();
        }

        @Override
        public void updateTile() {
            super.updateTile();
            tacker.tick(isShooting());
            timer--;
        }

        @Override
        protected void shoot(BulletType type) {
            if(tacker.cool) {
                return;
            }

            if(timer <= 0) {
                super.shoot(type);

                timer = delta * (tacker.reverse() + 5);
            }
        }
    }
}