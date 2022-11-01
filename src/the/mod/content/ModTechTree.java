package the.mod.content;

import arc.func.*;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.Item;

import static mindustry.content.TechTree.*;

public class ModTechTree {
    public static TechNode tech;

    public static void load() {
        tech = nodeRoot("the new tech mod", Blocksx.terra, () -> {
            Cons2<UnlockableContent, Runnable> nodeI = (i, r) -> {
                node(i, Seq.with(new Objectives.Produce(i)), r);
            };

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

            //conveyors
            node(Blocksx.silicaConveyor, () -> {
               node(Blocksx.silicaRouter);
            });

            //items
            node(Itemsx.silica, () -> {
                nodeI.get(Items.coal, () -> {
                    nodeI.get(Itemsx.coalSand, () -> {

                    });
                });

                nodeI.get(Itemsx.virusM, () -> {
                    nodeI.get(Itemsx.virusMSand, () -> {

                    });
                });

                nodeI.get(Itemsx.silicaSand, () -> {

                });
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