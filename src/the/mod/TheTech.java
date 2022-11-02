package the.mod;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.TextureRegion;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.content.Blocks;
import mindustry.content.SerpuloTechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.mod.*;
import mindustry.game.EventType;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import mindustry.world.Tile;
import the.mod.content.*;
import the.mod.types.Lasers;

import java.util.Random;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TheTech extends Mod {
    public static final Seq<UnlockableContent> all = new Seq<>();
    public static final String modId = "the-new-tech-mod";
    public static final String prefix = "[red][THE][]";
    public static Mods.LoadedMod mod;
    private String subtitle, dName;

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
                    t.pack();
                    t.visible(() -> state.isPaused() && state.isCampaign());
                    t.top();
                    scene.add(t);
                }
            }
        });
    }

    public static float len(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt(TheTech.pow(x2 - x1) + TheTech.pow(y2 - y1));
    }

    public static float pow(float n) {
        return n*n;
    }

    @Override
    public void loadContent() {
        Effects.load();
        Statuses.load();
        Itemsx.load();
        Liquids.load();
        Blocksx.load();

        ModTechTree.load();
        Redcon.load();

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

    public static TextureRegion loadRegion(Block block, String prefix) {
        return TheTech.mod(block.getContentType().name() + "." + block.name + "-" + prefix);
    }
}