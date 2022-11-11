package tntm.ui;

import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import mindustry.ctype.UnlockableContent;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.dialogs.BaseDialog;
import tntm.graphics.TntmDraw;

public class TntmUI {
    public static void show(String name, Cons<BaseDialog> cons) {
        BaseDialog dialog = new BaseDialog(name);
        cons.get(dialog);
        dialog.show();
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
        table.image(TntmDraw.arrowx).color(Color.darkGray).size(48);
    }

    public static void addx(UnlockableContent[] content, Table table) {
        for(UnlockableContent cont : content) {
            table.image(cont.uiIcon).height(33).width(Math.min(cont.uiIcon.width, 32) + 1).padLeft(6);
        }
    }
}