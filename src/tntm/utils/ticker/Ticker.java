package tntm.utils.ticker;

public abstract class Ticker {
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
}