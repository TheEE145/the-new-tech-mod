const fs = require("fs");

let file = process.argv[2];
let target = process.argv[3];
let replace = process.argv[4];
let text = fs.readFileSync(file, "utf-8");

let tmp, tmp2, tmp3, result = "";
for(word of text.split("\n")) {
    if(word.startsWith("#")) {
        result += word + "\n";
        continue;
    }

    tmp2 = (tmp = word.split(" = "))[0];
    delete(tmp[0]);

    tmp2 = tmp2.replace(target, replace);
    tmp3 = tmp2 + " = " + tmp.join(" = ").substring(3) + "\n";
    result += tmp3.length == 5 ? "\n" : tmp3;
}

fs.appendFileSync(file + "_old.txt", text, "utf-8");
fs.writeFileSync(file, result, "utf-8");
console.log("operation complated");

//node refactor.js bundle.properties the-new-tech-mod tntm