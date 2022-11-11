package tntm.graphics.drawer;

import mindustry.world.Block;

public class MultiDrawer extends Drawer {
    public Drawer[] drawers;

    public MultiDrawer() {
        super("");
    }

    @Override
    public <T extends Block> void load(T block) {
        for(Drawer d : drawers) {
            d.load(block);
        }
    }

    @Override
    public void draw(float x, float y) {
        for(Drawer d : drawers) {
            d.draw(x, y);
        }
    }

    public MultiDrawer get() {
        MultiDrawer x = new MultiDrawer();
        x.drawers = new Drawer[drawers.length];
        System.arraycopy(this.drawers, 0, x.drawers, 0, x.drawers.length);
        return x;
    }
}