package tntm.world.blocks.craft.meta;

import mindustry.ctype.UnlockableContent;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;

import static mindustry.type.ItemStack.with;

public class CraftPlan {
    public float meteria = 0;

    public float craftTime = -1;
    public UnlockableContent out;
    public float amount;

    public ItemStack[] items = with();
    public LiquidStack[] liquids = LiquidStack.with();

    public CraftPlan(UnlockableContent out, float amount) {
        this.out = out instanceof Liquid || out instanceof Item ? out : null;
        this.amount = amount;
    }
}