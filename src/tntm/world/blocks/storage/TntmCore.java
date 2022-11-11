package tntm.world.blocks.storage;

import arc.graphics.Color;
import mindustry.game.Team;
import mindustry.ui.Bar;
import mindustry.world.blocks.storage.CoreBlock;
import tntm.TheTech;
import tntm.asset.TntmAssets;
import tntm.utils.TntmShield;

import static arc.Core.bundle;

public class TntmCore extends CoreBlock {
    public float shieldReload = 60f;
    public float shieldHp = 1;

    public TntmCore(String name) {
        super(name);
        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("shield", (ModCoreBuild build) -> new Bar(
                () -> build.shield.isBroken() ? bundle.get("shield.progress") : bundle.get("shield.health"),
                () -> build.shield.isBroken() ? Color.orange : Color.cyan,
                () -> build.shield.isBroken() ? build.shield.progress() : build.shield.health()
        ));
    }

    public class ModCoreBuild extends CoreBuild {
        public TntmShield shield;

        public ModCoreBuild() {
            shield = new TntmShield() {{
                maxHealth = shieldHp;
                reloadTime = shieldReload;
            }};
        }

        @Override
        public void draw() {
            super.draw();
            if(shield.region == null) {
                shield.region = TntmAssets.mod("shield-" + size);
            }

            shield.renderer(x, y);
        }

        @Override
        public void damage(Team source, float damage) {
            super.damage(source, shield.damage(damage));
        }
    }
}