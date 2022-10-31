package the.mod;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.TextureRegion;

import mindustry.mod.*;
import mindustry.game.EventType;
import mindustry.ui.dialogs.BaseDialog;
import the.mod.content.Blocks;
import the.mod.content.Effects;

import static arc.Core.*;

public class TheTech extends Mod {
    public static final String modId = "the-new-tech-mod";
    public static final String prefix = "[red][THE][]";

    public TheTech() {
        on(EventType.ClientLoadEvent.class, () -> {
            show("@beta.title", d -> {
                d.addCloseButton();
                d.cont.pane(t -> {
                    t.margin(60f);

                    t.image(modR("icon")).size(60f).row();
                    t.add("@beta.cont.title").row();
                    t.image().color(Color.darkGray).height(4f).row();
                    t.add("@bata.cont.text");
                }).grow();
            });
        });
    }

    @Override
    public void loadContent() {
        Effects.load();
        Blocks.load();
    }

    public static String prefix(String text) {
        return prefix + " " + text;
    }

    public static Texture get(String id) {
        return atlas.find(id).texture;
    }

    public static Texture mod(String id) {
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

    public static TextureRegion modR(String id) {
        return new TextureRegion(mod(id));
    }
}