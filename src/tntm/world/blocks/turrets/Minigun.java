package tntm.world.blocks.turrets;

import arc.graphics.Color;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import tntm.utils.ticker.MinigunLikeTicker;
import tntm.utils.ticker.Ticker;

import static arc.Core.bundle;

public class Minigun extends TntmItemTurret {
    public Minigun(String name) {
        super(name);

        reload = 0;
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("heat", (MinigunBuild e) -> {
            return new Bar(
                    () -> e.tacker.cool ? bundle.get("minigun.cool") : bundle.get("minigun.heat") + ": " + (int) e.tacker.heat + "%",
                    () -> e.tacker.cool ? Color.cyan : Color.orange,
                    () -> e.tacker.heat / 100f
            );
        });
    }

    public class MinigunBuild extends ItemTurretBuild {
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