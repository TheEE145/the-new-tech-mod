package tntm.world.blocks.lasers;

import mindustry.entities.TargetPriority;
import tntm.world.blocks.walls.TntmBlock;

public class LaserMirror extends TntmBlock {
    public float offset;

    public LaserMirror(String name) {
        super(name);
        canBurn = false;

        rotate = true;
        priority = TargetPriority.transport;
        conveyorPlacement = true;
        underBullets = true;

        update = true;
        noUpdateDisabled = false;
    }

    public class LaserMirrorBuild extends TntmBlockBuild {
        public float angle() {
            if(rotation == 0 || rotation == 2) {
                return -offset;
            }

            return offset;
        }
    }
}