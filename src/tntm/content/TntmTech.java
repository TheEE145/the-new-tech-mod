package tntm.content;

import arc.Core;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.game.Objectives;
import tntm.world.buffs.DrillSpeedBuff;
import tntm.world.buffs.MeteriaCapacityBuff;

import static mindustry.content.TechTree.*;

public class TntmTech {
    public static MeteriaCapacityBuff capacity, capacity2, capacity3;
    public static DrillSpeedBuff speed, speed2, speed3;

    public static void loadBuffs() {
        capacity = new MeteriaCapacityBuff("capacity-booster") {{
            boost = 1.25f;

            requirements(
                    TntmItems.silica, 1000,
                    TntmItems.virusM, 500
            );
        }};

        capacity2 = new MeteriaCapacityBuff("capacity-booster2") {{
            boost = 1.50f;

            requirements(
                    TntmItems.silica,     2000,
                    TntmItems.virusM,     1000,
                    TntmItems.silicaSand, 500
            );
        }};

        capacity3 = new MeteriaCapacityBuff("capacity-booster3") {{
            boost = 1.75f;

            requirements(
                    TntmItems.silica,     4000,
                    TntmItems.virusM,     2000,
                    TntmItems.silicaSand, 1000,
                    TntmItems.virusMSand, 500
            );
        }};

        speed = new DrillSpeedBuff("drill-speed") {{
            boost = 1.25f;

            requirements(
                    TntmItems.silica, 800,
                    TntmItems.virusM, 300
            );
        }};

        speed2 = new DrillSpeedBuff("drill-speed2") {{
            boost = 1.50f;

            requirements(
                    TntmItems.silica,     1800,
                    TntmItems.virusM,     800,
                    TntmItems.silicaSand, 300
            );
        }};

        speed3 = new DrillSpeedBuff("drill-speed3") {{
            boost = 1.75f;

            requirements(
                    TntmItems.silica,     3800,
                    TntmItems.virusM,     1800,
                    TntmItems.silicaSand, 800,
                    TntmItems.virusMSand, 300
            );
        }};
    }

    public static void load() {
        loadBuffs();

        TntmPlanets.redcon.techTree = nodeRoot(TntmPlanets.redcon.localizedName, TntmBlocks.terra, () -> {
            node(TntmItems.silica, () -> {
                node(TntmBlocks.silicaDrill, () -> {
                    node(TntmBlocks.crasher, () -> {
                        node(TntmBlocks.silicaCrystal, () -> {

                        });

                        node(TntmBlocks.virusMCrystal, () -> {

                        });
                    });

                    node(TntmBlocks.updatedDrill, () -> {
                    });

                    node(TntmBlocks.silicaConveyor, () -> {
                        node(TntmBlocks.silicaRouter, () -> {
                        });
                    });

                    nodeProduce(TntmItems.virusM, () -> {
                        node(TntmBlocks.virusMWall, Seq.with(new Objectives.Research(TntmBlocks.silicaWall)) , () -> {
                            node(TntmBlocks.virusMWallLarge);
                        });

                        node(TntmBlocks.crusher, () -> {
                            nodeProduce(TntmItems.virusMSand, () -> {

                            });

                            nodeProduce(TntmItems.silicaSand, () -> {
                                node(TntmBlocks.amot);
                                node(TntmBlocks.sonicPulsar);
                                node(TntmBlocks.cooler);
                            });

                            nodeProduce(TntmItems.coalSand, () -> {
                                node(TntmUnits.trident);
                            });

                            node(TntmBlocks.baseFactory);
                        });

                        node(TntmBlocks.silicaBridge);

                        node(TntmBlocks.basicPump, () -> {
                            nodeProduce(TntmLiquids.emethen, () -> {
                                node(TntmBlocks.emethenMeteriaGenerator, Seq.with(new Objectives.Research(TntmBlocks.coalMeteriaGenerator)), () -> {

                                });

                                node(TntmBlocks.magmaFabric, () -> {
                                    nodeProduce(TntmItems.magmaAlloy, () -> {
                                        node(TntmUnits.javelin);
                                        node(TntmBlocks.magmaWall);
                                    });

                                    node(TntmBlocks.updatedFactory, () -> {
                                        node(TntmUnits.delta);
                                        node(TntmUnits.tau);
                                    });
                                });
                            });
                        });
                    });

                    nodeProduce(Items.coal, () -> {
                        node(TntmBlocks.coalMeteriaGenerator, () -> {
                            node(TntmBlocks.meteriaNode, () -> {
                                node(capacity, () -> {
                                    node(capacity2, Seq.with(new Objectives.Research(TntmBlocks.coalMeteriaGenerator)), () -> {
                                        node(capacity3, Seq.with(
                                                new Objectives.Research(TntmBlocks.largeMeteriaNode),
                                                new Objectives.Research(TntmBlocks.meteriaBooster),
                                                new Objectives.Research(TntmBlocks.emethenMeteriaGenerator)
                                        ), () -> {

                                        });
                                    });
                                });

                                node(TntmBlocks.largeMeteriaNode, () -> {
                                    node(TntmBlocks.meteriaDrill, Seq.with(
                                            new Objectives.Research(TntmBlocks.coalMeteriaGenerator)
                                    ), () -> {
                                        //TODO tier 4 and 5 drills
                                    });

                                    node(speed, () -> {
                                        node(speed2, Seq.with(new Objectives.Research(TntmBlocks.meteriaDrill)), () -> {
                                            node(speed3);
                                        }) ;
                                    });
                                });

                                node(TntmBlocks.meteriaBooster);
                            });
                        });

                        node(TntmBlocks.scretch, () -> {
                        });
                    });
                });

                node(TntmBlocks.silicaTurret, () -> {
                    node(TntmBlocks.silicaWall, () -> {
                        node(TntmBlocks.largeSilicaWall);
                    });
                });

                node(TntmBlocks.laser, () -> {
                    node(TntmBlocks.longLaser);

                    node(TntmBlocks.mirror, () -> {
                        node(TntmBlocks.multiMirror);
                        node(TntmBlocks.mirror135);
                        node(TntmBlocks.mirror0);
                    });
                });
            });
        });
    }
}