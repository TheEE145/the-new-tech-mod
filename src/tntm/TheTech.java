package tntm;

import arc.*;
import arc.graphics.*;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.mod.*;
import mindustry.game.EventType;
import mindustry.type.LiquidStack;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.consumers.*;
import mindustry.world.meta.Stat;
import tntm.asset.TntmAssets;
import tntm.content.*;
import tntm.utils.TntmTimer;
import tntm.world.blocks.craft.*;
import tntm.world.buffs.TntmBuff;

import java.util.Random;

import static arc.Core.*;
import static mindustry.Vars.*;
import static tntm.asset.TntmAssets.mod;
import static tntm.ui.TntmUI.*;

public class TheTech extends Mod {
    public static final Seq<UnlockableContent> all = new Seq<>();
    public static final String prefix = "[red][THE][]";
    public static final String modId = "tntm";
    public static Mods.LoadedMod mod;
    private String subtitle, dName;

    public static boolean isPart(Building b, Building b2) {
        return b.x == b2.x && b.y == b2.y;
    }

    public static Block toBlock(Building build) {
        if(build == null) {
            return null;
        }

        return world.tile(build.tileX(), build.tileY()).block();
    }

    public String bundle(String text) {
        return bundle.get(text);
    }

    //Vars.mods.locateMod("the-new-tech-mod");
    public TheTech() {
        on(EventType.ClientLoadEvent.class, () -> {
            show("@beta.title", d -> {
                d.addCloseButton();
                d.cont.pane(t -> {
                    t.margin(60f);

                    t.image(mod("modlogog")).size(650, 120).row();
                    t.add("@beta.cont.title").row();
                    t.image().color(Color.darkGray).height(4f).row();
                    t.add("@beta.cont.text");
                }).grow();
            });

            if(!headless) {
                mod = mods.locateMod(modId);

                Random rand = new Random();
                UnlockableContent contents = all.get(rand.nextInt(all.size - 1));

                dName = "< TheNewTech > - v" + mod.meta.version;
                mod.meta.displayName = "[gray]< [#66CDAA]TheNewTech[] >[] - [#66CDAA]v" + mod.meta.version + "[]";
                mod.meta.subtitle = subtitle = "mod have this " + contents.getContentType().name() + ": " + contents.localizedName;

                if(!mobile) {
                    if(mods.locateMod("ol") == null) {
                        Table t = new Table();
                        t.margin(4f);
                        t.labelWrap("[red]" + dName + "\n" + Log.removeColors(subtitle));
                        t.pack();
                        t.visible(() -> state.isMenu());
                        scene.add(t);
                    }

                    Table t = new Table();
                    t.margin(4f);
                    t.button(bundle("research.all"), () -> {
                        show(bundle("research.all.warn"), bg -> {
                            bg.cont.pane(h -> {
                                h.margin(60f);
                                h.add(bundle("research.all.text")).row();
                                h.add(bundle("bd.confirm")).row();

                                h.button(bundle("bd.confirm.y"), () -> {
                                    content.blocks().each(UnlockableContent::unlock);
                                    content.items().each(UnlockableContent::unlock);
                                    content.liquids().each(UnlockableContent::unlock);
                                    content.units().each(UnlockableContent::unlock);
                                    content.statusEffects().each(UnlockableContent::unlock);
                                    content.sectors().each(UnlockableContent::unlock);

                                    bg.hide();
                                }).width(300f).row();

                                h.button(bundle("bd.confirm.n"), bg::hide).width(300f).row();
                            });
                        });
                    }).size(300f, 50f);
                    t.button(bundle("buffs.all"), () -> {
                        show(bundle("buffs.all.warn"), bg -> {
                            bg.cont.pane(h -> {
                                h.margin(60f);
                                h.add(bundle("buffs.all.text")).row();
                                h.add(bundle("bd.confirm")).row();

                                h.button(bundle("bd.confirm.y"), () -> {
                                    TntmBuff.buffs.each(UnlockableContent::unlock);

                                    bg.hide();
                                }).width(300f).row();

                                h.button(bundle("bd.confirm.n"), bg::hide).width(300f).row();
                            });
                        });
                    }).size(300f, 50f);
                    t.pack();
                    t.visible(() -> state.isPaused() && state.isCampaign());
                    t.top();
                    scene.add(t);
                }
            }

            content.planets().each(e -> {
                e.unlock();
                e.accessible = true;
                e.alwaysUnlocked = true;
            });

            Team.blue.name = bundle.get("team.blue");

            for(TntmBuff buff : TntmBuff.buffs) {
                if(buff.unlocked()) {
                    buff.activate();
                }
            }

            TntmAssets.load();
            TntmTimer.load();
        });
    }


    public static void setup(UnlockableContent b) {
        b.stats.remove(Stat.buildCost);
        b.stats.add(Stat.buildCost, table -> {
            table.left().row();

            table.pane(t -> {
                addx(b instanceof Block x ? x.requirements : b.researchRequirements(), t);
            }).growX().height(48f);
        });

        if(b instanceof TntmCrafter b2 && !(b instanceof MultiCrafter)) {
            b.stats.add(TntmStats.requirements, t -> {
                t.row();
                t.pane(table -> {
                    table.setBackground(Styles.grayPanel);
                    table.pane(input -> {
                        for(Consume consume : b2.consumers) {
                            if(consume instanceof ConsumeItems x) {
                                addx(x.items, input);
                            }

                            if(consume instanceof ConsumeLiquids x) {
                                addx(x.liquids, input);
                            }

                            if(consume instanceof ConsumeLiquid l) {
                                addx(LiquidStack.with(l.liquid, l.amount), table);
                            }

                            if(consume instanceof ConsumePower x) {
                                addx(x.usage * 60, Blocks.powerNode.uiIcon, table);
                            }
                        }
                    }).padLeft(6f);

                    arrowx(table);

                    table.pane(output -> {
                        if(b2.outputItems != null) {
                            addx(b2.outputItems, output);
                        }

                        if(b2.outputLiquids != null) {
                            addx(b2.outputLiquids, output);
                        }
                    }).padRight(6f);
                    table.left();
                });
            });
        }
    }

    @Override
    public void loadContent() {
        TntmStats.load();
        TntmFx.load();
        TntmStatusEffects.load();
        TntmItems.load();
        TntmLiquids.load();
        TntmUnits.load();
        TntmBlocks.load();

        TntmPlanets.load();
        TntmTech.load();
    }

    public static String prefix(String text) {
        return prefix + " " + text;
    }

    public static void on(Class<?> cl, Runnable runnable) {
        Events.on(cl, (e) -> {
            runnable.run();
        });
    }
}