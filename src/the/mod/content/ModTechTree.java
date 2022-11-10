package the.mod.content;

import arc.Core;
import arc.func.*;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.world.meta.Stat;
import the.mod.types.Meteria;
import the.mod.utils.Types;

import static mindustry.content.TechTree.*;

public class ModTechTree {
    public static MeteriaCapacityBuff capacity, capacity2, capacity3;
    public static DrillSpeedBuff speed, speed2, speed3;
    public static TechNode tech;

    public static void loadBuffs() {
        capacity = new MeteriaCapacityBuff("capacity-booster") {{
            boost = 1.25f;

            requirements(
                    Itemsx.silica, 1000,
                    Itemsx.virusM, 500
            );
        }};

        capacity2 = new MeteriaCapacityBuff("capacity-booster2") {{
            boost = 1.50f;

            requirements(
                    Itemsx.silica,     2000,
                    Itemsx.virusM,     1000,
                    Itemsx.silicaSand, 500
            );
        }};

        capacity3 = new MeteriaCapacityBuff("capacity-booster3") {{
            boost = 1.75f;

            requirements(
                    Itemsx.silica,     4000,
                    Itemsx.virusM,     2000,
                    Itemsx.silicaSand, 1000,
                    Itemsx.virusMSand, 500
            );
        }};

        speed = new DrillSpeedBuff("drill-speed") {{
            boost = 1.25f;

            requirements(
                    Itemsx.silica, 800,
                    Itemsx.virusM, 300
            );
        }};

        speed2 = new DrillSpeedBuff("drill-speed2") {{
            boost = 1.50f;

            requirements(
                    Itemsx.silica,     1800,
                    Itemsx.virusM,     800,
                    Itemsx.silicaSand, 300
            );
        }};

        speed3 = new DrillSpeedBuff("drill-speed3") {{
            boost = 1.75f;

            requirements(
                    Itemsx.silica,     3800,
                    Itemsx.virusM,     1800,
                    Itemsx.silicaSand, 800,
                    Itemsx.virusMSand, 300
            );
        }};
    }

    public static void load() {
        loadBuffs();

        tech = nodeRoot(Core.bundle.get("planet.the-new-tech-mod-redcon.name"), Blocksx.terra, () -> {
            Cons2<UnlockableContent, Runnable> nodeI = (i, r) -> {
                node(i, Seq.with(new Objectives.Produce(i)), r);
            };

            node(Itemsx.silica, () -> {
                node(Blocksx.silicaDrill, () -> {
                    node(Blocksx.crasher, () -> {
                        node(Blocksx.silicaCrystal, () -> {

                        });

                        node(Blocksx.virusMCrystal, () -> {

                        });
                    });

                    node(Blocksx.updatedDrill, () -> {
                        node(Blocksx.meteriaDrill, Seq.with(
                                new Objectives.Research(Blocksx.meteriaNode),
                                new Objectives.Research(Blocksx.coalMeteriaGenerator)
                        ), () -> {
                            //TODO tier 4 and 5 drills
                        });

                        node(speed, () -> {
                            node(speed2, Seq.with(new Objectives.Research(Blocksx.meteriaDrill)), () -> {
                                node(speed3);
                            }) ;
                        });
                    });

                    node(Blocksx.silicaConveyor, () -> {
                        node(Blocksx.silicaRouter, () -> {
                            node(Blocksx.silicaBridge);
                        });
                    });

                    nodeI.get(Itemsx.virusM, () -> {
                        node(Blocksx.virusMWall, Seq.with(new Objectives.Research(Blocksx.silicaWall)) , () -> {
                            node(Blocksx.virusMWallLarge);
                        });
                    });
                });

                node(Blocksx.meteriaNode, () -> {
                    node(capacity, () -> {
                        node(capacity2, Seq.with(new Objectives.Research(Blocksx.coalMeteriaGenerator)), () -> {
                            node(capacity3, Seq.with(
                                    new Objectives.Research(Blocksx.largeMeteriaNode),
                                    new Objectives.Research(Blocksx.meteriaBooster),
                                    new Objectives.Research(Blocksx.emethenMeteriaGenerator)
                            ), () -> {

                            });
                        });
                    });

                    node(Blocksx.largeMeteriaNode, () -> {
                    });

                    node(Blocksx.meteriaBooster);
                });

                nodeI.get(Items.coal, () -> {
                    node(Blocksx.coalMeteriaGenerator, () -> {
                    });
                });

                nodeI.get(Liquids.emethen, () -> {
                    node(Blocksx.emethenMeteriaGenerator, Seq.with(new Objectives.Research(Blocksx.coalMeteriaGenerator)), () -> {

                    });

                });

                node(Blocksx.crusher, () -> {
                    nodeI.get(Itemsx.virusMSand, () -> {

                    });

                    nodeI.get(Itemsx.silicaSand, () -> {

                    });

                    nodeI.get(Itemsx.coalSand, () -> {
                        node(Unitsx.trident);
                    });

                    node(Blocksx.baseFactory);
                });

                node(Blocksx.silicaTurret, () -> {
                    node(Blocksx.silicaWall, () -> {
                        node(Blocksx.largeSilicaWall);
                    });
                });

                node(Blocksx.laser, () -> {
                    node(Blocksx.longLaser);

                    node(Blocksx.mirror, () -> {
                        node(Blocksx.multiMirror);
                        node(Blocksx.mirror135);
                        node(Blocksx.mirror0);
                    });
                });
            });
        });
    }

    public static class MeteriaCapacityBuff extends Types.Buff {
        public float boost;

        public MeteriaCapacityBuff(String name) {
            super(name);
        }

        @Override
        public void setStats() {
            super.setStats();

            stats.add(Statsx.meteriaBoost, 100 * boost + "%");
        }

        @Override
        public void handler(UnlockableContent c) {
            super.handler(c);

            if(c instanceof Meteria.MeteriaNode node) {
                node.maxMeteria *= boost;
            }
        }
    }

    public static class DrillSpeedBuff extends Types.Buff {
        public float boost;

        public DrillSpeedBuff(String name) {
            super(name);
        }

        @Override
        public void setStats() {
            super.setStats();

            stats.add(Stat.mineSpeed, 100 * boost + "%");
        }

        @Override
        public void handler(UnlockableContent c) {
            super.handler(c);

            if(c instanceof Types.ModDrill drill) {
                drill.drillTime /= boost;
            }
        }
    }
}