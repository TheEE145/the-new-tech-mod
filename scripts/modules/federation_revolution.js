let table, button, unit;

function getFederation() {
    unit = Vars.player.unit();

    if(unit == null) {
        return "no federation";
    };

    if(unit.team == team.crux) {
        return "Crocs";
    };

    if(unit.team == team.sharded) {
        return "Imperium";
    };

    if(unit.team == team.blue) {
        return "Sirions";
    };

    if(unit.team == team.green) {
        return "Emeralds";
    };

    if(unit.team == team.purple || unit.team == team.malis) {
        return "Sporans";
    };

    if(unit.team == team.derelict) {
        return "Decstroted";
    };

    return unit.team;
};

Events.on(ClientLoadEvent, function() {
    table = new Table().bottom().left();
    button = TextButton("federation info");

    table.add(button).size(200, 50).padLeft(6).padBottom(6);
    button.clicked(function() {
        Vars.ui.showCustomConfirm(
            "federation info", 
            "name: " + getFederation(), 
            
            "Call federation revolution",
            "Close",
            
            function() {
                Vars.ui.showCustomConfirm(
                    "federation revolution",
                    "requiments: \n5,000 copper\n5,000 lead\n2,500 titanium",
                
                    "FEDERATION REVOLUTION",
                    "Cancel",

                    function() {
                        try {
                            unit = Vars.player.unit();
                        } catch(err) {
                            print("unit is null");
                        };

                        if(unit == null) {
                            Vars.ui.announce("[red]no unit");
                            return;
                        };

                        Vars.ui.announce("[red]FEDERATION REVOLUTION");

                        Groups.unit.each(u => {
                            if(u.team === unit.team) {
                                return;
                            };

                            u.health /= 2;
                        });
                    },

                    function() {
                        Vars.ui.announce("canceled");
                    }
                );
            }, 

            function() {
                Vars.ui.announce("canceled");
            }
        );
    });

    Vars.ui.hudGroup.addChild(table);
});