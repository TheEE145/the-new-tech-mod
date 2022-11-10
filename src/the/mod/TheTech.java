package the.mod;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;

import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.Blocks;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.mod.*;
import mindustry.game.EventType;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.consumers.*;
import mindustry.world.meta.Stat;
import the.mod.content.*;
import the.mod.types.Lasers;
import the.mod.types.Other;
import the.mod.utils.Drawx;
import the.mod.utils.Types;

import java.util.Random;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TheTech extends Mod {
    public static final Seq<Types.ModUnitType> tacker = new Seq<>();
    public static final Seq<UnlockableContent> all = new Seq<>();
    public static final String modId = "the-new-tech-mod";
    public static final String prefix = "[red][THE][]";
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
                                    Types.Buff.buffs.each(UnlockableContent::unlock);

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

            //assets
            Drawx.circle = TheTech.mod("circlex");
            Drawx.sun    = TheTech.mod("sunx");
            Drawx.pix    = TheTech.mod("pix");
            Drawx.arrowx = TheTech.mod("arrowx");
            Drawx.air    = TheTech.mod("air");

            //renderer
            Timer.schedule(this::renderer, 1, 0.02f);

            content.planets().each(e -> {
                e.unlock();
                e.accessible = true;
                e.alwaysUnlocked = true;
            });

            Team.blue.name = bundle.get("team.blue");
            content.blocks().each(this::updateStats);

            for(Types.Buff buff : Types.Buff.buffs) {
                if(buff.unlocked()) {
                    buff.activate();
                }
            }
        });
    }

    public void clear(Stat stat, Block b) {
        b.stats.add(stat, "");
        b.stats.remove(stat);
    }

    public static void addx(LiquidStack[] liquids, Table table) {
        for(LiquidStack stack : liquids) {
            addx(stack.amount * 60, stack.liquid.uiIcon, table);
        }
    }

    public static void addx(ItemStack[] items, Table table) {
        for(ItemStack stack : items) {
            addx(stack.amount, stack.item.uiIcon, table);
        }
    }

    public static void addx(float value, TextureRegion icon, Table table) {
        table.image(icon).height(33).width(Math.min(icon.width, 32) + 1);
        table.add(" " + (int) value + ". ");
    }

    public static void arrowx(Table table) {
        table.image(Drawx.arrowx).color(Color.darkGray).size(48);
    }

    public static void addx(UnlockableContent[] content, Table table) {
        for(UnlockableContent cont : content) {
            table.image(cont.uiIcon).height(33).width(Math.min(cont.uiIcon.width, 32) + 1).padLeft(6);
        }
    }

    public static void setup(UnlockableContent b) {
        b.stats.remove(Stat.buildCost);
        b.stats.add(Stat.buildCost, table -> {
            table.left().row();

            table.pane(t -> {
                TheTech.addx(b instanceof Block x ? x.requirements : b.researchRequirements(), t);
            }).growX().height(48f);
        });
    }

    public void updateStats(Block b) {
        if(b instanceof Types.ModCrafter b2 && !(b instanceof Other.MultiCrafter)) {
            b.stats.add(Statsx.requirements, t -> {
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

    public void renderer() {
        Drawx.Math.renderer();
        tacker.each(Types.ModUnitType::tact);
    }

    public static float len(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt(TheTech.pow(x2 - x1) + TheTech.pow(y2 - y1));
    }

    public static float pow(float n) {
        return n*n;
    }

    @Override
    public void loadContent() {
        Statsx.load();
        Effects.load();
        Statuses.load();
        Itemsx.load();
        Liquids.load();
        Unitsx.load();
        Blocksx.load();

        Redcon.load();
        ModTechTree.load();

        Lasers.load();
    }

    public static Tile getTileByDraw(float x, float y) {
        return world.tile((int) Math.floor(x / 8), (int) Math.floor(y / 8));
    }

    public static String prefix(String text) {
        return prefix + " " + text;
    }

    public static TextureRegion get(String id) {
        return atlas.find(id);
    }

    public static TextureRegion mod(String id) {
        return get(modId + "-" + id);
    }

    public static void show(String name, Cons<BaseDialog> cons) {
        BaseDialog dialog = new BaseDialog(name);
        cons.get(dialog);
        dialog.show();
    }

    public static void on(Class<?> cl, Runnable runnable) {
        Events.on(cl, (e) -> {
            runnable.run();
        });
    }
}