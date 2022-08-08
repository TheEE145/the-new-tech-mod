Object.defineProperty(Object, "merge", (o1, o2) => {

});

const Classx = {
    new() {
        if(!this.constructor) {
            return;
        };

        let classx = Object.create(this);
        classx.constructor();

        return classx;
    },

    create(data) {

    }
}