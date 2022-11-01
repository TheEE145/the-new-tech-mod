package the.mod;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.TextureRegion;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.ctype.UnlockableContent;
import mindustry.mod.*;
import mindustry.game.EventType;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import the.mod.content.*;

import java.util.Random;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TheTech extends Mod {
    public static final Seq<UnlockableContent> all = new Seq<>();
    public static final String modId = "the-new-tech-mod";
    public static final String prefix = "[red][THE][]";
    public static Mods.LoadedMod mod;
    private String subtitle, dName;

    //Vars.mods.locateMod("the-new-tech-mod");
    public TheTech() {

        on(EventType.ClientLoadEvent.class, () -> {
            show("@beta.title", d -> {
                d.addCloseButton();
                d.cont.pane(t -> {
                    t.margin(60f);

                    t.image(mod("liquid-unloader-center")).size(60f).row();
                    t.add("@beta.cont.title").row();
                    t.image().color(Color.darkGray).height(4f).row();
                    t.add("@beta.cont.text");
                }).grow();
            });

            if(!headless) {
                mod = mods.locateMod(modId);
                Log.info(all);

                Random rand = new Random();
                UnlockableContent content = all.get(rand.nextInt(all.size - 1));

                mod.meta.displayName = dName = "[gray]< [cyan]TheNewTech[] >[] - [#66CDAA]v" + mod.meta.version + "[]";
                mod.meta.subtitle = subtitle = "mod have this " + content.getContentType().name() + ": " + content.localizedName;

                if(!mobile && mods.locateMod("ol") == null) {
                    Table t = new Table();
                    t.margin(4f);
                    t.labelWrap("[white]" + dName + "[]\n[#66CDAA]" + subtitle);
                    t.pack();
                    t.visible(() -> state.isMenu());
                    scene.add(t);
                }
            }
        });
    }

    @Override
    public void loadContent() {
        Effects.load();
        Itemsx.load();
        Blocks.load();

        ModTechTree.load();
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