package the.mod.utils;

import arc.Events;
import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.io.*;
import mindustry.game.*;
import mindustry.ui.fragments.ConsoleFragment;
import the.mod.TheTech;

import java.util.Objects;

import static mindustry.Vars.mods;

public class Terminal {
    public static class TerminalBlock extends Types.ModBlock {
        public TerminalBlock(String name) {
            super(name);

            canBurn = false;
        }

        public static void loadEvent() {
            Events.on(EventType.TapEvent.class, e -> {
                if(e.tile.build instanceof TerminalBlockBuild termux) {
                    TheTech.show("command execute", d -> {
                        d.cont.pane(t -> {
                            t.field(termux.code, (code) -> termux.code = code).size(600f, 300f).row();
                            t.image().color(Color.gray).growX().height(4f).row();
                            t.pane(buttons -> {
                                buttons.button("apply", d::hide).size(150f, 50f);
                                buttons.button("run", () -> {
                                    termux.runtime.execute();
                                    d.hide();
                                }).size(150f, 50f).padLeft(6f);
                            }).grow().row();
                            t.add("[red]" + termux.out + "[]");
                        }).grow();
                    });
                }
            });
        }

        public class TerminalBlockBuild extends ModBlockBuild {
            TerminalRuntime runtime = new TerminalRuntime(this);
            public String code = "";
            public String out = "";

            @Override
            public void write(Writes write) {
                super.write(write);
                write.str(code);
            }

            @Override
            public void read(Reads read, byte revision) {
                super.read(read, revision);
                code = read.str();
            }
        }
    }

    public static class TerminalRuntime {
        TerminalBlock.TerminalBlockBuild build;

        public static class TerminalException extends Exception {
            public final int line;

            public TerminalException(int line) {
                this.line = line;
            }
        }

        public TerminalRuntime(TerminalBlock.TerminalBlockBuild build) {
            this.build = build;
        }

        public void execute() {
            boolean string = false, skip = false;
            String code = "", prev = "", word = "";
            for(String ch : build.code.split("")) {
                if((ch.equals(" ") || ch.equals("\n")) && !string) {
                    if(!prev.equals("")) {
                        if(prev.equals("let")) {
                            if(word.equals("mut")) {
                                word = "";
                                prev = "let";
                            } else {
                                prev = "";
                                word = "const";
                            }
                        }
                    }

                    if(word.equals("f^>")) {
                        word = "function";
                    }

                    if(word.equals("<::>")) {
                        word = "=>";
                    }

                    if(word.startsWith("G.>")) {
                        word = "Groups." + word.substring(3);
                    }

                    if(!word.equals("let")) {
                        code += prev + word;
                    }

                    prev = word;
                    word = "";
                }

                if(ch.equals("\\")) {
                    skip = true;
                    continue;
                }

                if(skip) {
                    skip = false;
                    continue;
                }

                if(ch.equals("\"")) {
                    string = !string;
                }

                word += ch;
            }

            Log.info(code);
            build.out = mods.getScripts().runConsole(code);
        }
    }
}