package tntm.world.blocks.walls;

import arc.graphics.g2d.Draw;
import arc.scene.ui.layout.Table;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.world.meta.BuildVisibility;
import tntm.graphics.TntmDraw;

import static mindustry.type.ItemStack.with;

public class DPSBlock extends TntmBlock {
    public TntmDraw.FontType type = TntmDraw.FontType.UNDER_LINE;
    public float reloadTime = 180;

    public DPSBlock(String name) {
        super(name);
        configurable = true;

        update = true;
        buildVisibility = BuildVisibility.sandboxOnly;
        requirements(Category.defense, with());
    }

    public class DPSBlockBuild extends TntmBlockBuild {
        private float dmgTimer = 0;
        private float timer = 0;
        public float damage = 0;

        @Override
        public void draw() {
            super.draw();

            if(damage < 1) {
                return;
            }

            Draw.draw(Layer.overlayUI, () -> {
                TntmDraw.text(x, y + size * 4f + 3f, team.color, ((int) damage) + " damage", type);
            });
        }

        public boolean justDamaged() {
            return dmgTimer > 0;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(dmgTimer > 0) {
                dmgTimer--;
            }

            if(justDamaged()) {
                timer = reloadTime;
            } else {
                timer--;
            }

            if(timer < 0) {
                timer = reloadTime;
                heal();

                if(!justDamaged()) {
                    damage = 0;
                }
            }

            if(!justDamaged()) {
                damage -= 1f;
            }
        }

        @Override
        public float handleDamage(float amount) {
            damage += amount;
            dmgTimer = 10;

            return super.handleDamage(amount);
        }

        @Override
        public void buildConfiguration(Table table) {
            table.slider(0, TntmDraw.FontType.values().length - 1, 1, type.ordinal(), value -> {
                type = TntmDraw.FontType.values()[(int) value];
            }).size(300f, 50f);
        }
    }
}