package the.mod.content;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.TechTree.TechNode;
import mindustry.game.Objectives;

import static mindustry.content.TechTree.*;

public class ModTechTree {
    public static TechNode tech;

    public static void load() {
        tech = nodeRoot("the new tech mod", Blocks.terra, () -> {
            //energy
            node(Blocks.meteriaNode, () -> {
                node(Blocks.largeMeteriaNode);
                node(Blocks.meteriaBooster);

                node(Blocks.coalMeteriaGenerator, Seq.with(new Objectives.Research(Items.coal)), () -> {
                });
            });

            //drills
            node(Blocks.silicaDrill, () -> {
                node(Blocks.updatedDrill, () -> {
                    node(Blocks.meteriaDrill, Seq.with(
                            new Objectives.Research(Blocks.meteriaNode),
                            new Objectives.Research(Blocks.coalMeteriaGenerator)
                    ), () -> {
                        //TODO tier 4 and 5 drills
                    });
                });
            });

            //items
            node(Itemsx.silica, () -> {
                node(Items.coal, () -> {
                    node(Itemsx.coalSand);
                });

                node(Itemsx.virusM, () -> {
                    node(Itemsx.virusMSand);
                });

                node(Itemsx.silicaSand);
            });

            //crafters
            node(Blocks.sander);

            //defence
            node(Blocks.silicaWall, () -> {
                node(Blocks.largeSilicaWall);

                node(Blocks.virusMWall, () -> {
                    node(Blocks.virusMWallLarge);
                });
            });
        });
    }
}