package tntm.utils.ticker;

public class MinigunLikeTicker extends Ticker {
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