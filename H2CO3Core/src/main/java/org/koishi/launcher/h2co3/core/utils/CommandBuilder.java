package org.koishi.launcher.h2co3.core.utils;

import static org.koishi.launcher.h2co3.core.utils.Logging.LOG;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CommandBuilder {
    private static final Pattern UNSTABLE_OPTION_PATTERN = Pattern.compile("-XX:(?<key>[a-zA-Z0-9]+)=(?<value>.*)");
    private static final Pattern UNSTABLE_BOOLEAN_OPTION_PATTERN = Pattern.compile("-XX:(?<value>[+\\-])(?<key>[a-zA-Z0-9]+)");

    private final List<String> raw = new ArrayList<>();

    public CommandBuilder() {
    }

    public CommandBuilder add(String... args) {
        Collections.addAll(raw, args);
        return this;
    }

    public CommandBuilder addAll(Collection<String> args) {
        raw.addAll(args);
        return this;
    }

    public CommandBuilder addWithoutParsing(String... args) {
        Collections.addAll(raw, args);
        return this;
    }

    public CommandBuilder addAllWithoutParsing(Collection<String> args) {
        raw.addAll(args);
        return this;
    }

    public void addAllDefault(Collection<String> args) {
        addAllDefault(args, true);
    }

    public void addAllDefaultWithoutParsing(Collection<String> args) {
        addAllDefault(args, false);
    }

    private void addAllDefault(Collection<String> args, boolean parse) {
        loop:
        for (String arg : args) {
            if (arg.startsWith("-D")) {
                int idx = arg.indexOf('=');
                if (idx >= 0) {
                    addDefault(arg.substring(0, idx + 1), arg.substring(idx + 1), parse);
                } else {
                    String opt = arg + "=";
                    for (String item : raw) {
                        if (item.startsWith(opt)) {
                            LOG.info("Default option '" + arg + "' is suppressed by '" + item + "'");
                            continue loop;
                        } else if (item.equals(arg)) {
                            continue loop;
                        }
                    }
                    raw.add(arg);
                }
                continue;
            }

            if (arg.startsWith("-XX:")) {
                Matcher matcher = UNSTABLE_OPTION_PATTERN.matcher(arg);
                if (matcher.matches()) {
                    addUnstableDefault(matcher.group("key"), matcher.group("value"), parse);
                    continue;
                }

                matcher = UNSTABLE_BOOLEAN_OPTION_PATTERN.matcher(arg);
                if (matcher.matches()) {
                    addUnstableDefault(matcher.group("key"), "+".equals(matcher.group("value")), parse);
                    continue;
                }
            }

            if (arg.startsWith("-X")) {
                String opt = null;
                String value = null;

                for (String prefix : new String[]{"-Xmx", "-Xms", "-Xmn", "-Xss"}) {
                    if (arg.startsWith(prefix)) {
                        opt = prefix;
                        value = arg.substring(prefix.length());
                        break;
                    }
                }

                if (opt != null) {
                    addDefault(opt, value, parse);
                    continue;
                }
            }

            for (String item : raw) {
                if (item.equals(arg)) {
                    continue loop;
                }
            }
            raw.add(arg);
        }
    }

    public String addDefault(String opt, String value) {
        return addDefault(opt, value, true);
    }

    private String addDefault(String opt, String value, boolean parse) {
        for (String item : raw) {
            if (item.startsWith(opt)) {
                LOG.info("Default option '" + opt + value + "' is suppressed by '" + item + "'");
                return item;
            }
        }
        raw.add(opt + value);
        return null;
    }

    public String addUnstableDefault(String opt, boolean value) {
        return addUnstableDefault(opt, value, true);
    }

    private String addUnstableDefault(String opt, boolean value, boolean parse) {
        for (String item : raw) {
            final Matcher matcher = UNSTABLE_BOOLEAN_OPTION_PATTERN.matcher(item);
            if (matcher.matches()) {
                if (matcher.group("key").equals(opt)) {
                    return item;
                }
            }
        }

        if (value) {
            raw.add("-XX:+" + opt);
        } else {
            raw.add("-XX:-" + opt);
        }
        return null;
    }

    public String addUnstableDefault(String opt, String value) {
        return addUnstableDefault(opt, value, true);
    }

    private String addUnstableDefault(String opt, String value, boolean parse) {
        for (String item : raw) {
            final Matcher matcher = UNSTABLE_OPTION_PATTERN.matcher(item);
            if (matcher.matches()) {
                if (matcher.group("key").equals(opt)) {
                    return item;
                }
            }
        }

        raw.add("-XX:" + opt + "=" + value);
        return null;
    }

    public boolean removeIf(Predicate<String> pred) {
        return raw.removeIf(pred);
    }

    public boolean noneMatch(Predicate<String> predicate) {
        return raw.stream().noneMatch(predicate);
    }

    @Override
    public String toString() {
        return raw.stream().collect(Collectors.joining(" "));
    }

    public List<String> asList() {
        return new ArrayList<>(raw);
    }

    public List<String> asMutableList() {
        return new ArrayList<>(raw);
    }

    public static String pwshString(String str) {
        return "'" + str.replace("'", "''") + "'";
    }

    public static boolean hasExecutionPolicy() {
        try {
            final Process process = Runtime.getRuntime().exec(new String[]{"powershell", "-Command", "Get-ExecutionPolicy"});
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroy();
                return false;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), OperatingSystem.NATIVE_CHARSET))) {
                String policy = reader.readLine();
                return "Unrestricted".equalsIgnoreCase(policy) || "RemoteSigned".equalsIgnoreCase(policy);
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static boolean setExecutionPolicy() {
        try {
            final Process process = Runtime.getRuntime().exec(new String[]{"powershell", "-Command", "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser"});
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroy();
                return false;
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    private static boolean containsEscape(String str, String escapeChars) {
        for (int i = 0; i < escapeChars.length(); i++) {
            if (str.indexOf(escapeChars.charAt(i)) >= 0)
                return true;
        }
        return false;
    }

    private static String escape(String str, char... escapeChars) {
        for (char ch : escapeChars) {
            str = str.replace(String.valueOf(ch), "\\" + ch);
        }
        return str;
    }

    public static String toBatchStringLiteral(String s) {
        return containsEscape(s, " \t\"^&<>|") ? '"' + escape(s, '\\', '"') + '"' : s;
    }

    public static String toShellStringLiteral(String s) {
        return containsEscape(s, " \t\"!#$&'()*,;<=>?[\\]^`{|}~") ? '"' + escape(s, '"', '$', '&', '`') + '"' : s;
    }
}