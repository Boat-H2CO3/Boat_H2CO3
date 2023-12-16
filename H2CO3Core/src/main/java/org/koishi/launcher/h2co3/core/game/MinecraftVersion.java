package org.koishi.launcher.h2co3.core.game;

import android.util.Log;

import com.google.gson.Gson;

import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Game;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MinecraftVersion {
    public AssetsIndex assetIndex;
    public String assets;
    public HashMap<String, Download> downloads;
    public String id;
    public Library[] libraries;
    public String mainClass;
    public String minecraftArguments;
    public int minimumLauncherVersion;
    public String releaseTime;
    public String time;
    public String type;
    public Arguments arguments;
    // forge
    public String inheritsFrom;
    public String minecraftPath;
    private Map<String, String> SHAs;

    public static MinecraftVersion fromDirectory(File file) {
        String json = new String(FileTools.readFile(new File(file, file.getName() + ".json")), StandardCharsets.UTF_8);
        MinecraftVersion result = new Gson().fromJson(json, MinecraftVersion.class);
        result.minecraftPath = new File(file, file.getName() + ".jar").getAbsolutePath();

        if (result.inheritsFrom != null && !result.inheritsFrom.isEmpty()) {
            MinecraftVersion self = result;
            result = MinecraftVersion.fromDirectory(new File(file.getParentFile(), self.inheritsFrom));

            if (self.assetIndex != null) {
                result.assetIndex = self.assetIndex;
            }
            if (!self.assets.isEmpty()) {
                result.assets = self.assets;
            }
            if (self.downloads != null && !self.downloads.isEmpty()) {
                if (result.downloads == null) {
                    result.downloads = new HashMap<>();
                }
                result.downloads.putAll(self.downloads);
            }
            if (self.libraries != null && self.libraries.length > 0) {
                Library[] newLibs = new Library[result.libraries.length + self.libraries.length];
                System.arraycopy(self.libraries, 0, newLibs, 0, self.libraries.length);
                System.arraycopy(result.libraries, 0, newLibs, self.libraries.length, result.libraries.length);
                result.libraries = newLibs;
            }
            if (!self.mainClass.isEmpty()) {
                result.mainClass = self.mainClass;
            }
            if (!self.minecraftArguments.isEmpty()) {
                result.minecraftArguments = self.minecraftArguments;
            }
            if (self.minimumLauncherVersion > result.minimumLauncherVersion) {
                result.minimumLauncherVersion = self.minimumLauncherVersion;
            }
            if (!self.releaseTime.isEmpty()) {
                result.releaseTime = self.releaseTime;
            }
            if (!self.time.isEmpty()) {
                result.time = self.time;
            }
            if (!self.type.isEmpty()) {
                result.type = self.type;
            }
            if (!self.minecraftPath.isEmpty()) {
                result.minecraftPath = self.minecraftPath;
            }
        }
        result.minecraftArguments = "--username ${auth_player_name} --version ${version_name} --gameDir ${game_directory} --assetsDir ${assets_root} --assetIndex ${assets_index_name} --uuid ${auth_uuid} --accessToken ${auth_access_token} --userProperties ${user_properties} --userType ${user_type} --versionType ${version_type}";

        return result;
    }

    public String getClassPath(H2CO3Game config, boolean high, boolean isJava17) {
        StringBuilder cp = new StringBuilder();
        int count = 0;
        String librariesPath = H2CO3Game.getGameDirectory() + "/libraries/";

        for (Library lib : this.libraries) {
            if (lib.name == null || lib.name.isEmpty() || lib.name.contains("org.lwjgl") || lib.name.contains("natives") || (isJava17 && lib.name.contains("java-objc-bridge"))) {
                continue;
            }
            String[] names = lib.name.split(":");
            String packageName = names[0];
            String mainName = names[1];
            String versionName = names[2];

            String path = librariesPath + packageName.replaceAll("\\.", "/") + "/" + mainName + "/" + versionName + "/" + mainName + "-" + versionName + ".jar";
            Log.d("路径",path);

            if (count > 0) {
                cp.append(":");
            }
            cp.append(path);
            count++;
        }
        String split = count > 0 ? ":" : "";
        if (high) {
            cp.append(split).append(minecraftPath);
        } else {
            cp.insert(0, minecraftPath + split);
        }
        cp.insert(0, minecraftPath);

        return cp.toString();
    }

    public String[] getJVMArguments(H2CO3Game gameLaunchSetting) {
        StringBuilder test = new StringBuilder();
        if (arguments != null && arguments.jvm != null) {
            for (Object obj : this.arguments.jvm) {
                if (obj instanceof String && !((String) obj).startsWith("-Djava.library.path") && !((String) obj).startsWith("-cp") && !((String) obj).startsWith("${classpath}")) {
                    test.append(obj).append(" ");
                }
            }
        } else {
            return new String[0];
        }
        StringBuilder result = new StringBuilder();

        int state = 0;
        int start = 0;
        int stop;
        for (int i = 0; i < test.length(); i++) {
            if (state == 0) {
                if (test.charAt(i) != '$') {
                    result.append(test.charAt(i));
                } else {
                    if (i + 1 < test.length() && test.charAt(i + 1) == '{') {
                        state = 1;
                        start = i;
                    } else {
                        result.append(test.charAt(i));
                    }
                }
            } else {
                if (test.charAt(i) == '}') {
                    stop = i;
                    String key = test.substring(start + 2, stop);
                    String value = switch (key) {
                        case "version_name" -> id;
                        case "launcher_name" -> "Boat_H2CO3";
                        case "launcher_version" -> "1.0.0";
                        case "version_type" -> type;
                        case "assets_index_name" -> assetIndex != null ? assetIndex.id : assets;
                        case "game_directory" -> H2CO3Game.getGameDirectory();
                        case "assets_root" -> H2CO3Game.getGameAssetsRoot();
                        case "user_properties" -> H2CO3Auth.getUserProperties();
                        case "auth_player_name" -> H2CO3Auth.getPlayerName();
                        case "auth_session" -> H2CO3Auth.getAuthSession();
                        case "auth_uuid" -> H2CO3Auth.getAuthUUID();
                        case "auth_access_token" -> H2CO3Auth.getAuthAccessToken();
                        case "user_type" -> H2CO3Auth.getUserType();
                        case "primary_jar_name" ->
                                H2CO3Game.getGameCurrentVersion() + "/" + FileTools.getFolderName(H2CO3Game.getGameCurrentVersion()) + ".jar";
                        case "library_directory" -> H2CO3Game.getGameDirectory() + "/libraries";
                        case "classpath_separator" -> ":";
                        default -> "";
                    };
                    result.append(value);
                    state = 0;
                }
            }
        }
        Log.d("路径",(Arrays.toString(result.toString().split(" "))));
        return result.toString().split(" ");
    }

    public String[] getMinecraftArguments(H2CO3Game gameLaunchSetting, boolean isHighVer) {
        StringBuilder test = new StringBuilder();
        if (isHighVer) {
            for (Object obj : this.arguments.game) {
                if (obj instanceof String) {
                    test.append(obj).append(" ");
                }
            }
        } else {
            test = new StringBuilder(this.minecraftArguments);
        }
        StringBuilder result = new StringBuilder();

        int state = 0;
        int start = 0;
        int stop;
        for (int i = 0; i < test.length(); i++) {
            if (state == 0) {
                if (test.charAt(i) != '$') {
                    result.append(test.charAt(i));
                } else {
                    if (i + 1 < test.length() && test.charAt(i + 1) == '{') {
                        state = 1;
                        start = i;
                    } else {
                        result.append(test.charAt(i));
                    }
                }
            } else {
                if (test.charAt(i) == '}') {
                    stop = i;
                    String key = test.substring(start + 2, stop);
                    String value = switch (key) {
                        case "version_name" -> id;
                        case "launcher_name" -> "Boat_H2CO3";
                        case "launcher_version" -> "1.0.0";
                        case "version_type" -> type;
                        case "assets_index_name" -> assetIndex != null ? assetIndex.id : assets;
                        case "game_directory" -> H2CO3Game.getGameDirectory();
                        case "assets_root" -> H2CO3Game.getGameAssetsRoot();
                        case "user_properties" -> H2CO3Auth.getUserProperties();
                        case "auth_player_name" -> H2CO3Auth.getPlayerName();
                        case "auth_session" -> H2CO3Auth.getAuthSession();
                        case "auth_uuid" -> H2CO3Auth.getAuthUUID();
                        case "auth_access_token" -> H2CO3Auth.getAuthAccessToken();
                        case "user_type" -> H2CO3Auth.getUserType();
                        case "primary_jar_name" ->
                                H2CO3Game.getGameCurrentVersion() + "/" + FileTools.getFolderName(H2CO3Game.getGameCurrentVersion()) + ".jar";
                        case "library_directory" -> H2CO3Game.getGameDirectory() + "/libraries";
                        case "classpath_separator" -> ":";
                        default -> "";
                    };
                    result.append(value);
                    state = 0;
                }
            }
        }
        if (!isHighVer && arguments != null && arguments.game != null) {
            for (Object obj : this.arguments.game) {
                if (obj instanceof String) {
                    result.append(" ").append(obj);
                }
            }
        }
        return result.toString().split(" ");
    }

    public List<String> getLibraries() {
        List<String> libs = new ArrayList<>();
        for (Library lib : this.libraries) {
            if (lib.name == null || lib.name.isEmpty() || lib.name.contains("net.java.jinput") || lib.name.contains("org.lwjgl") || lib.name.contains("platform")) {
                continue;
            }
            libs.add(parseLibNameToPath(lib.name));
        }
        return libs;
    }

    public String getSHA1(String libName) {
        if (SHAs == null) {
            SHAs = new HashMap<>();
            for (Library lib : this.libraries) {
                if (lib.name == null || lib.name.isEmpty() || lib.name.contains("net.java.jinput") || lib.name.contains("org.lwjgl") || lib.name.contains("platform")) {
                    continue;
                }
                String sha1;
                try {
                    sha1 = Objects.requireNonNull(lib.downloads.get("artifact")).sha1;
                } catch (Exception e) {
                    continue;
                }
                SHAs.put(parseLibNameToPath(lib.name), sha1);
            }
        }
        return SHAs.get(libName);
    }

    public String parseLibNameToPath(String libName) {
        String[] tmp = libName.split(":");
        return tmp[0].replace(".", "/") + "/" + tmp[1] + "/" + tmp[2] + "/" + tmp[1] + "-" + tmp[2] + ".jar";
    }

    public static class AssetsIndex {
        public String id;
        public String sha1;
        public int size;
        public int totalSize;
        public String url;
    }

    public static class Download {
        public String path;
        public String sha1;
        public int size;
        public String url;
    }

    public static class Arguments {
        private Object[] game;
        private Object[] jvm;
    }

    public static class Library {
        public String name;
        public HashMap<String, Download> downloads;
    }
}