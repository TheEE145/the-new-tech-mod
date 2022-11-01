package the.mod.content;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.TechTree.TechNode;
import mindustry.game.Objectives;

import static mindustry.content.TechTree.*;

public class ModTechTree {
    public static TechNode tech;

    public static void load() {
        tech = nodeRoot("the new tech mod", Blocksx.terra, () -> {
            //energy
            node(Blocksx.meteriaNode, () -> {
                node(Blocksx.largeMeteriaNode);
                node(Blocksx.meteriaBooster);

                node(Blocksx.coalMeteriaGenerator, Seq.with(new Objectives.Research(Items.coal)), () -> {
                });
            });

            //drills
            node(Blocksx.silicaDrill, () -> {
                node(Blocksx.updatedDrill, () -> {
                    node(Blocksx.meteriaDrill, Seq.with(
                            new Objectives.Research(Blocksx.meteriaNode),
                            new Objectives.Research(Blocksx.coalMeteriaGenerator)
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
            node(Blocksx.silicaPress, () -> {
                node(Blocksx.meteriaPress, Seq.with(new Objectives.Research(Blocksx.meteriaNode)), () -> {

                });
            });

            //defence
            node(Blocksx.silicaWall, () -> {
                node(Blocksx.largeSilicaWall);

                node(Blocksx.virusMWall, () -> {
                    node(Blocksx.virusMWallLarge);
                });
            });
        });
    }
}