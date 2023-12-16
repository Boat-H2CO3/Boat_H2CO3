package org.koishi.launcher.h2co3.boat.utils;

import static org.koishi.launcher.h2co3.boat.H2CO3LoadMe.chdir;
import static org.koishi.launcher.h2co3.boat.H2CO3LoadMe.dlexec;
import static org.koishi.launcher.h2co3.boat.H2CO3LoadMe.dlopen;
import static org.koishi.launcher.h2co3.boat.H2CO3LoadMe.patchLinker;
import static org.koishi.launcher.h2co3.boat.H2CO3LoadMe.redirectStdio;
import static org.koishi.launcher.h2co3.boat.H2CO3LoadMe.setenv;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class H2CO3Utils implements Runnable {

    public static final String H2CO3_ENV_WINDOW_WIDTH = "window_width";
    public static final String H2CO3_ENV_WINDOW_HEIGHT = "window_height";
    public static final String H2CO3_ENV_TMPDIR = "tmpdir";

    private static final Pattern variablePattern;

    static {
        variablePattern = Pattern.compile("\\$\\{[a-zA-Z_]+\\}");
    }

    private final Map<String, String> variables;
    private final List<String[]> commands;
    private String scriptFile;
    public static final String H2CO3_TASK_SCRIPT_PATH = "h2co3_script_path";
    private H2CO3Utils script;

    public static LinkedList<String[]> parseJson(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[(int) fis.available()];
        fis.read(buffer);
        fis.close();
        String json = new String(buffer, StandardCharsets.UTF_8);

        Type type = new TypeToken<LinkedList<String[]>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    private String replaceVariables(String str) {
        if (str == null) {
            str = "";
        }
        Matcher m = variablePattern.matcher(str);
        while (m.find()) {
            String varRef = m.group();
            String varName = varRef.substring(2, varRef.length() - 1);
            String varValue = this.variables.get(varName);
            if (varValue == null) {
                varValue = "";
            }
            str = str.replace(varRef, varValue);
        }
        return str;
    }
    private Thread thread;

    public H2CO3Utils(Map<String, String> envvars, boolean priv, List<String[]> cmds, String file) {
        if (priv) {
            this.variables = new HashMap<>();
            this.variables.putAll(envvars);
        } else {
            this.variables = envvars;
        }
        this.commands = new LinkedList<>();
        this.commands.addAll(cmds);
        this.scriptFile = file;
        if (this.scriptFile == null) {
            this.scriptFile = "";
        }
    }

    public H2CO3Utils(Map<String, String> variables, Map<String, String> envvars, String scriptPath) {
        this.variables = variables;
        this.commands = new LinkedList<>();

        try {
            List<String[]> cmds = H2CO3Utils.parseJson(scriptPath);
            this.commands.addAll(cmds);
            this.script = new H2CO3Utils(envvars, false, cmds, scriptPath);
            this.thread = new Thread(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        int line = 0;
        try {
            for (; line < this.commands.size(); line++) {
                String[] args = this.commands.get(line);
                if (args == null) {
                    continue;
                }
                for (int i = 1; i < args.length; i++) {
                    args[i] = replaceVariables(args[i]);
                }
                switch (args[0]) {
                    case "patchLinker" -> {
                        patchLinker();
                    }
                    case "setenv" -> {
                        setenv(args[1], args[2]);
                    }
                    case "chdir" -> {
                        chdir(args[1]);
                    }
                    case "redirectStdio" -> {
                        redirectStdio(args[1]);
                    }
                    case "dlopen" -> {
                        dlopen(args[1]);
                    }
                    case "dlexec" -> {
                        String[] finalArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, finalArgs, 0, args.length - 1);
                        dlexec(finalArgs);
                    }
                    case "strdef" -> {
                        String value = args[2];
                        if (value == null) {
                            value = "";
                        }
                        this.variables.put(args[1], value);
                    }
                    case "strcat" -> {
                        String value = this.variables.get(args[1]);
                        if (value == null) {
                            value = "";
                        }
                        StringBuilder builder = new StringBuilder(value);
                        for (int i = 2; i < args.length; i++) {
                            if (args[i] != null) {
                                builder.append(args[i]);
                            }
                        }
                        this.variables.put(args[1], builder.toString());
                    }
                    case "json" -> {
                        if (this.scriptFile.equals(args[1])) {
                            throw new Exception("Recursive script!");
                        }
                        LinkedList<String[]> includes = parseJson(args[1]);
                        new H2CO3Utils(this.variables, false, includes, args[1]).execute();
                    }
                    default -> {
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occurred, " + this.scriptFile + ":" + line);
            System.out.println(Arrays.toString(this.commands.get(line)));
            e.printStackTrace();
        }
    }

    public void beforeExecute() {

    }

    public void afterExecute() {

    }

    public void startTask() {
        this.thread.start();
    }

    @Override
    public void run() {
        beforeExecute();
        this.script.execute();
        afterExecute();
    }
}
