let dl;

Events.on(ClientLoadEvent, () => {
    dl = new BaseDialog("@mod.bata.title");
    dl.addCloseButton();

    dl.cont.pane(cons(t => {
        for(let i in t) {
            if(typeof t[i] === 'function') {
                print(i); 
            };
        };

        t.center();
        t.margin(60);

        t.add("@mod.name").pad(6).row();
        t.add("@mod.beta");
    })).grow();
    
    dl.show();
});