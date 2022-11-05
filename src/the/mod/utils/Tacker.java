package the.mod.utils;

public abstract class Tacker {
    private boolean paused = false;

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public boolean paused() {
        return paused;
    }

    public void tick() {
        if(!paused) {
            hand();
        }
    }

    protected abstract void hand();

    public static class RotationTicker extends Tacker {
        public boolean reverse;
        public float rotation;

        public RotationTicker(float rotation) {
            this.rotation = rotation;
        }

        public float rotation() {
            return rotation/360f;
        }

        public void tack(float angle) {
            if(reverse) {
                rotation -= angle;

                if(rotation <= 0) {
                    rotation = 360;
                }
            } else {
                rotation += angle;

                if(rotation >= 360) {
                    rotation = 0;
                }
            }
        }

        @Override
        protected void hand() {
            tack(1);
        }
    }

    public static class MinigunLikeTicker extends Tacker {
        public boolean shoot = false;
        public boolean cool = false;
        public float heat = 0f;

        public float heatMultiplayer = 0.1f;
        public float coolantMultiplayer = 0.2f;

        @Override
        protected void hand() {
        }

        public float reverse() {
            return 100f - heat;
        }

        public void tick(boolean shoot) {
            if(paused()) {
                return;
            }

            this.shoot = shoot;

            if(shoot) {
                if(cool) {
                    return;
                }

                heat += heatMultiplayer;

                if(heat > 100f) {
                    heat = 100f;
                    cool = true;
                }

                return;
            }

            if(cool) {
                heat -= coolantMultiplayer;

                if(heat < 0f) {
                    heat = 0f;
                    cool = false;
                }

                return;
            }

            heat -= heatMultiplayer;

            if(heat < 0f) {
                heat = 0f;
            }
        }
    }
}