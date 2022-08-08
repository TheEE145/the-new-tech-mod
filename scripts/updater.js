let dl;
Events.on(ClientLoadEvent, () => {
    dl = new BaseDialog("@mod.bata.title");
    dl.addCloseButton();

    dl.cont.pane(cons(t => {
        t.center();
        t.margin(60);

        t.image(new TextureRegion(Core.atlas.find("the-new-tech-mod-modlogog", ""))).height(128).width(820).row();
        t.image().growX().height(3).pad(4).color(Color.lightGray).row();
        t.add("@mod.beta");
    })).grow();
    
    dl.show();
});