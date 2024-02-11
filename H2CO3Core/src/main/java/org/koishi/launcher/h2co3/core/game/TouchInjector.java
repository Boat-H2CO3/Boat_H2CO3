package org.koishi.launcher.h2co3.core.game;

import org.koishi.launcher.h2co3.core.utils.CommandBuilder;
import org.koishi.launcher.h2co3.core.H2CO3Tools;

import java.util.HashSet;
import java.util.List;

public class TouchInjector {

    private static final String TOUCH_INJECTOR_JAR_PATH = H2CO3Tools.APP_DATA_PATH + "/h2co3Launcher" + "/plugin/touch/TouchInjector.jar";
    private static final String TOUCH_INJECTOR_FORGE_JAR_PATH = H2CO3Tools.APP_DATA_PATH + "/h2co3Launcher" + "/plugin/touch/TouchInjector-forge.jar";

    public static CommandBuilder rebaseArguments(CommandBuilder args) {
        H2CO3GameHelper gameLaunchSetting = new H2CO3GameHelper();
        if (!gameLaunchSetting.touchInjector) {
            return args;
        }

        CommandBuilder newArgs = new CommandBuilder();
        HashSet<String> argSet = new HashSet<>(args.asList());

        if (isForge(argSet)) {
            if (isBootstrapLauncher(argSet)) {
                String version = getVersion(args.asList());
                addTouchInjectorVersionArg(newArgs.asList(), version);
                addTouchInjectorForgeJarArg(newArgs.asList());
            } else {
                addTouchInjectorForgeArg(newArgs.asList());
            }
        } else if (isOptiFine(argSet) || isLiteLoader(argSet)) {
            addTouchInjectorOptiFineArg(newArgs.asList());
        } else if (isFabricKnotClient(argSet)) {
            addTouchInjectorFabricKnotClientArg(newArgs.asList());
        } else if (isQuiltKnotClient(argSet)) {
            addTouchInjectorQuiltKnotClientArg(newArgs.asList());
        } else {
            addTouchInjectorVanillaArg(newArgs.asList());
        }

        return newArgs;
    }

    private static boolean isForge(HashSet<String> argSet) {
        return argSet.contains("Forge") || argSet.contains("cpw.mods.fml.common.launcher.FMLTweaker") ||
                argSet.contains("fmlclient") || argSet.contains("forgeclient");
    }

    private static boolean isBootstrapLauncher(HashSet<String> argSet) {
        return argSet.contains("cpw.mods.bootstraplauncher.BootstrapLauncher");
    }

    private static String getVersion(List<String> args) {
        String version = "unknown";
        boolean hit = false;
        for (String arg : args) {
            if (hit) {
                if (arg.startsWith("--")) {
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
        return version;
    }

    private static void addTouchInjectorVersionArg(List<String> newArgs, String version) {
        newArgs.add("-Dtouchinjector.version=" + version);
    }

    private static void addTouchInjectorForgeJarArg(List<String> newArgs) {
        for (int i = 0; i < newArgs.size(); i++) {
            if (newArgs.get(i).startsWith("-Xms")) {
                newArgs.add(i, TOUCH_INJECTOR_FORGE_JAR_PATH);
                break;
            }
        }
    }

    private static void addTouchInjectorForgeArg(List<String> newArgs) {
        for (String arg : newArgs) {
            if (arg.startsWith("-Xms")) {
                newArgs.add(0, "-javaagent:" + TOUCH_INJECTOR_JAR_PATH + "=forge");
                break;
            }
        }
    }

    private static boolean isOptiFine(HashSet<String> argSet) {
        return argSet.contains("optifine.OptiFineTweaker");
    }

    private static boolean isLiteLoader(HashSet<String> argSet) {
        return argSet.contains("com.mumfrey.liteloader.launch.LiteLoaderTweaker");
    }

    private static void addTouchInjectorOptiFineArg(List<String> newArgs) {
        for (String arg : newArgs) {
            if (arg.startsWith("-Xms")) {
                newArgs.add(0, "-javaagent:" + TOUCH_INJECTOR_JAR_PATH + "=optifine");
                break;
            }
        }
    }

    private static boolean isFabricKnotClient(HashSet<String> argSet) {
        return argSet.contains("net.fabricmc.loader.impl.launch.knot.KnotClient");
    }

    private static void addTouchInjectorFabricKnotClientArg(List<String> newArgs) {
        for (int i = 0; i < newArgs.size(); i++) {
            if (newArgs.get(i).equals("net.fabricmc.loader.impl.launch.knot.KnotClient")) {
                newArgs.set(i, "com.tungsten.touchinjector.launch.FabricKnotClient");
                break;
            }
        }
        addTouchInjectorJarArg(newArgs);
    }

    private static boolean isQuiltKnotClient(HashSet<String> argSet) {
        return argSet.contains("org.quiltmc.loader.impl.launch.knot.KnotClient");
    }

    private static void addTouchInjectorQuiltKnotClientArg(List<String> newArgs) {
        for (int i = 0; i < newArgs.size(); i++) {
            if (newArgs.get(i).equals("org.quiltmc.loader.impl.launch.knot.KnotClient")) {
                newArgs.set(i, "com.tungsten.touchinjector.launch.QuiltKnotClient");
                break;
            }
        }
        addTouchInjectorJarArg(newArgs);
    }

    private static void addTouchInjectorJarArg(List<String> newArgs) {
        for (int i = 0; i < newArgs.size(); i++) {
            if (newArgs.get(i).equals("-cp")) {
                newArgs.add(i + 1, TOUCH_INJECTOR_JAR_PATH);
                break;
            }
        }
    }

    private static void addTouchInjectorVanillaArg(List<String> newArgs) {
        for (String arg : newArgs) {
            if (arg.startsWith("-Xms")) {
                newArgs.add(0, "-javaagent:" + TOUCH_INJECTOR_JAR_PATH + "=vanilla");
                break;
            }
        }
    }
}