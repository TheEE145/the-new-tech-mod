package tntm.graphics.drawer;

public class PlanDrawer {
    public String prefix, type;

    public PlanDrawer(String type) {
        this("", type);
    }

    public PlanDrawer(String prefix, String type) {
        this.prefix = prefix;
        this.type = type;
    }

    public Drawer get() {
        if(type.equals("rotor")) {
            return new DrawRotor(prefix);
        }

        if(type.equals("heat")) {
            return new HeatDrawer(prefix);
        }

        if(type.equals("base")) {
            return new BaseDrawer();
        }

        if(type.equals("liquid")) {
            return new DrawLiquid(prefix);
        }

        return new Drawer(prefix);
    }
}