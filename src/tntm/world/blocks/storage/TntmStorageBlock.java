package tntm.world.blocks.storage;

import mindustry.world.blocks.storage.StorageBlock;
import tntm.TheTech;

public class TntmStorageBlock extends StorageBlock {
    public TntmStorageBlock(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void setStats() {
        super.setStats();
        TheTech.setup(this);
    }
}