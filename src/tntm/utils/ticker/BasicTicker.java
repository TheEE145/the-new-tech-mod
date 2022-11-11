package tntm.utils.ticker;

public class BasicTicker extends Ticker {
    public float max = 100, min = 0, cur = 1, val = 0;
    public boolean fall = false;

    public float delta() {
        return (val - min) / (max - min);
    }

    @Override
    protected void hand() {
        if(fall) {
            val -= cur;

            if(val < min) {
                val = min;
                fall = false;
            }
        } else {
            val += cur;

            if(val > max) {
                val = max;
                fall = true;
            }
        }
    }
}