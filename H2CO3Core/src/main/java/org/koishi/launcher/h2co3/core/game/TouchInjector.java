package org.koishi.launcher.h2co3.core.game;

import android.util.Log;

import org.koishi.launcher.h2co3.core.H2CO3Game;
import org.koishi.launcher.h2co3.core.H2CO3Tools;

import java.util.HashSet;
import java.util.Vector;

public class TouchInjector {

    public static Vector<String> rebaseArguments(Vector<String> args) {
        H2CO3Game gameLaunchSetting = new H2CO3Game();
        if (!gameLaunchSetting.touchInjector) {
            return args;
        }
        Vector<String> newArgs = new Vector<>();
        HashSet<String> argSet = new HashSet<>(args);

        if (argSet.contains("Forge") || argSet.contains("cpw.mods.fml.common.launcher.FMLTweaker") || argSet.contains("fmlclient") || argSet.contains("forgeclient")) {
            if (argSet.contains("cpw.mods.bootstraplauncher.BootstrapLauncher")) {
                String version = "unknown";
                boolean hit = false;
                for (String arg : args) {
                    if (hit) {
                        if (arg.startsWith("--")) {
                            // arg doesn't seem to be a value
                            // maybe the previous argument is a value, but we wrongly recognized it as an option
                            hit = false;
                        } else {
                            if (arg.startsWith("1.17")) {
                                version = "1.17";
                            } else if (arg.startsWith("1.18")) {
                                version = "1.18";
                            } else if (arg.startsWith("1.19")) {
                                version = "1.19";
                            }
                            break;
                        }
                    }
                    if ("--assetIndex".equals(arg)) {
                        hit = true;
                    }
                }
                hit = false;
                for (int i = 0; i < args.size(); i++) {
                    if (hit) {
                        newArgs.add(args.get(i) + ":" + H2CO3Tools.APP_DATA_PATH + "/boat" + "/plugin/touch/TouchInjector-forge.jar");
                        hit = false;
                    } else if (args.get(i).startsWith("-Xms")) {
                        newArgs.add("-Dtouchinjector.version=" + version);
                        newArgs.add(args.get(i));
                    } else if (args.get(i).equals("-cp")) {
                        hit = true;
                        newArgs.add(args.get(i));
                    } else {
                        newArgs.add(args.get(i));
                    }
                }
            } else {
                for (int i = 0; i < args.size(); i++) {
                    if (args.get(i).startsWith("-Xms")) {
                        newArgs.add("-javaagent:" + H2CO3Tools.APP_DATA_PATH + "/boat" + "/plugin/touch/TouchInjector.jar=forge");
                    }
                    newArgs.add(args.get(i));
                }
            }
            return newArgs;
        } else if (argSet.contains("optifine.OptiFineTweaker") || argSet.contains("com.mumfrey.liteloader.launch.LiteLoaderTweaker")) {
            for (int i = 0; i < args.size(); i++) {
                if (args.get(i).startsWith("-Xms")) {
                    newArgs.add("-javaagent:" + H2CO3Tools.APP_DATA_PATH + "/boat" + "/plugin/touch/TouchInjector.jar=optifine");
                }
                newArgs.add(args.get(i));
            }
            return newArgs;
        } else if (argSet.contains("net.fabricmc.loader.impl.launch.knot.KnotClient")) {
            boolean hit = false;
            for (int i = 0; i < args.size(); i++) {
                if (hit) {
                    newArgs.add(args.get(i) + ":" + H2CO3Tools.APP_DATA_PATH + "/boat" + "/plugin/touch/TouchInjector.jar");
                    hit = false;
                } else if (args.get(i).equals("net.fabricmc.loader.impl.launch.knot.KnotClient")) {
                    newArgs.add("com.tungsten.touchinjector.launch.FabricKnotClient");
                } else if (args.get(i).equals("-cp")) {
                    hit = true;
                    newArgs.add(args.get(i));
                } else {
                    newArgs.add(args.get(i));
                }
            }
            return newArgs;
        } else if (argSet.contains("org.quiltmc.loader.impl.launch.knot.KnotClient")) {
            boolean hit = false;
            for (int i = 0; i < args.size(); i++) {
                if (hit) {
                    newArgs.add(args.get(i) + ":" + H2CO3Tools.APP_DATA_PATH + "/boat" + "/plugin/touch/TouchInjector.jar");
                    hit = false;
                } else if (args.get(i).equals("org.quiltmc.loader.impl.launch.knot.KnotClient")) {
                    newArgs.add("com.tungsten.touchinjector.launch.QuiltKnotClient");
                } else if (args.get(i).equals("-cp")) {
                    hit = true;
                    newArgs.add(args.get(i));
                } else {
                    newArgs.add(args.get(i));
                }
            }
            return newArgs;
        } else {
            for (int i = 0; i < args.size(); i++) {
                if (args.get(i).startsWith("-Xms")) {
                    newArgs.add("-javaagent:" + H2CO3Tools.APP_DATA_PATH + "/boat" + "/plugin/touch/TouchInjector.jar=vanilla");
                }
                newArgs.add(args.get(i));
            }
            return newArgs;
        }
    }
}