package tntm.world.buffs;

import arc.func.Cons;
import arc.struct.Seq;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.type.ItemStack;
import tntm.TheTech;

public class TntmBuff extends UnlockableContent {
    private ItemStack[] requirements = ItemStack.empty;

    public static final Seq<TntmBuff> buffs = new Seq<>();
    public Cons<UnlockableContent> handler;
    public boolean inited = false;

    public void requirements(Object... args) {
        requirements = ItemStack.with(args);
    }

    @Override
    public ItemStack[] researchRequirements() {
        return requirements;
    }

    public void handler(UnlockableContent c) {
        if(handler != null) {
            handler.get(c);
        }
    }

    public TntmBuff(String name) {
        super(name);

        buffs.add(this);
    }

    public void activate() {
        if(inited) {
            return;
        }

        TheTech.all.each(this::handler);
        inited = true;
    }

    @Override
    public void onUnlock() {
        activate();
    }

    @Override
    public ContentType getContentType() {
        return ContentType.error;
    }
}