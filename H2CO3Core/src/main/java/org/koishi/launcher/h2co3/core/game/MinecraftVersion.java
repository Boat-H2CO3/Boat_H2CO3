package org.koishi.launcher.h2co3.core.game;

import com.google.gson.Gson;

import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class MinecraftVersion {
    private static final String LIBRARIES_PATH = H2CO3GameHelper.getGameDirectory() + File.separator + "libraries";
    private static final String CLASSPATH_SEPARATOR = ":";
    private static final String APP_NAME = "Boat_H2CO3";
    private static final String LAUNCHER_VERSION = "1.0.0";
    private static final String DEFAULT_VALUE = "0";
    public String mainClass;
    private AssetsIndex assetIndex;
    public int minimumLauncherVersion;
    private String assets;
    private HashMap<String, Download> downloads;
    private String id;
    private Library[] libraries;
    private String minecraftArguments;
    private String releaseTime;
    private Map<String, String> SHAs;
    private String time;
    private String type;
    private Arguments arguments;
    private String inheritsFrom;
    private String minecraftPath;

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

    public String getClassPath(boolean high, boolean isJava8) {
        StringJoiner cp = new StringJoiner(CLASSPATH_SEPARATOR);

        for (Library lib : libraries) {
            if (shouldSkipLibrary(lib, isJava8)) {
                continue;
            }

            String path = parseLibNameToPath(lib.name);
            cp.add(path);
        }

        return high ? cp + CLASSPATH_SEPARATOR + minecraftPath : minecraftPath + (cp.length() > 0 ? CLASSPATH_SEPARATOR : "") + cp;
    }

    private boolean shouldSkipLibrary(Library lib, boolean isJava8) {
        return lib.name == null || lib.name.isEmpty() || lib.name.contains("org.lwjgl") || lib.name.contains("natives") || (isJava8 && lib.name.contains("java-objc-bridge"));
    }

    public String[] getJVMArguments() {
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
                        case "game_directory" -> H2CO3GameHelper.getGameDirectory();
                        case "assets_root" -> H2CO3GameHelper.getGameAssetsRoot();
                        case "user_properties" -> H2CO3Auth.getUserProperties();
                        case "auth_player_name" -> H2CO3Auth.getPlayerName();
                        case "auth_session" -> H2CO3Auth.getAuthSession();
                        case "auth_uuid" -> H2CO3Auth.getAuthUUID();
                        case "auth_access_token" -> H2CO3Auth.getAuthAccessToken();
                        case "user_type" -> H2CO3Auth.getUserType();
                        case "primary_jar_name" ->
                                H2CO3GameHelper.getGameCurrentVersion() + "/" + id + ".jar";
                        case "library_directory" -> H2CO3GameHelper.getGameDirectory() + "/libraries";
                        case "classpath_separator" -> ":";
                        default -> "0";
                    };
                    result.append(value);
                    state = 0;
                }
            }
        }
        return result.toString().split(" ");
    }

    public String[] getMinecraftArguments(boolean isHighVer) {
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
                        case "game_directory" -> H2CO3GameHelper.getGameDirectory();
                        case "assets_root" -> H2CO3GameHelper.getGameAssetsRoot();
                        case "user_properties" -> H2CO3Auth.getUserProperties();
                        case "auth_player_name" -> H2CO3Auth.getPlayerName();
                        case "auth_session" -> H2CO3Auth.getAuthSession();
                        case "auth_uuid" -> H2CO3Auth.getAuthUUID();
                        case "auth_access_token" -> H2CO3Auth.getAuthAccessToken();
                        case "user_type" -> H2CO3Auth.getUserType();
                        case "primary_jar_name" ->
                                H2CO3GameHelper.getGameCurrentVersion() + "/" + id + ".jar";
                        case "library_directory" -> H2CO3GameHelper.getGameDirectory() + "/libraries";
                        case "classpath_separator" -> ":";
                        default -> "0";
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

    private String parseLibNameToPath(String libName) {
        String[] parts = libName.split(":");
        return String.format("%s%s%s%s%s%s%s%s%s-%s.jar", LIBRARIES_PATH, File.separator, parts[0].replaceAll("\\.", File.separator), File.separator, parts[1], File.separator, parts[2], File.separator, parts[1], parts[2]);
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