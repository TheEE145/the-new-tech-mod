package tntm.world.blocks.craft;

import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;
import tntm.TheTech;
import tntm.asset.TntmAssets;

import static arc.Core.atlas;
import static arc.Core.bundle;

public class TntmCrafter extends GenericCrafter {
    public TntmCrafter(String name) {
        super(name);

        localizedName = TheTech.prefix(localizedName);
    }

    @Override
    public void load() {
        super.load();

        if(atlas.has(name + "-preview")) {
            uiIcon = TntmAssets.get(name + "-preview");
        }
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.input);
        stats.remove(Stat.output);
        TheTech.setup(this);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("progress", (TntmCrafterBuild b) -> {
            return new Bar(
                    () -> bundle.get("bar.progress"),
                    () -> Pal.bar,
                    () -> b.progress
            );
        });
    }

    public class TntmCrafterBuild extends GenericCrafterBuild {
    }
}