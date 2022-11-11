package tntm.utils.ticker;

public class RotationTicker extends Ticker {
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