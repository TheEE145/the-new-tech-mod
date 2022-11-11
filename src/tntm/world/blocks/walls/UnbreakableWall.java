package tntm.world.blocks.walls;

import arc.graphics.Color;
import mindustry.game.Team;
import mindustry.gen.Bullet;

import static mindustry.Vars.world;

public class UnbreakableWall extends TntmBlock {
    public UnbreakableWall(String name) {
        super(name);

        chanceDeflect = Integer.MAX_VALUE;
        flashHit = true;

        insulated = true;
        absorbLasers = true;
        flashColor = Color.white;
        health = Integer.MAX_VALUE;
        solid = true;

        update = true;
        canBurn = false;
        schematicPriority = Integer.MAX_VALUE;
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("health");
    }

    public class UnbreakableWallBuild extends TntmBlockBuild {
        @Override
        public void damage(Bullet bullet, Team source, float damage) {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void damage(float amount, boolean withEffect) {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void damage(float damage) {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void damage(Team source, float damage) {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void damageContinuous(float amount) {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void damageContinuousPierce(float amount) {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void damagePierce(float amount) {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void damagePierce(float amount, boolean withEffect) {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public boolean dead() {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
            return false;
        }

        @Override
        public void dead(boolean dead) {
            super.dead(false);
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void kill() {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void killed() {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public float health() {
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
            return Integer.MAX_VALUE;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            health = Integer.MAX_VALUE;
        }

        @Override
        public void afterDestroyed() {
            super.afterDestroyed();
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            world.tile(tileX(), tileY()).setNet(block, team, rotation);
        }
    }
}