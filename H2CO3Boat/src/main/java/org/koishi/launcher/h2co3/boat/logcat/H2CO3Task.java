package org.koishi.launcher.h2co3.boat.logcat;

import org.koishi.launcher.h2co3.boat.utils.H2CO3Utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class H2CO3Task implements Runnable {

    public static final String H2CO3_TASK_SCRIPT_PATH = "h2co3_script_path";

    private H2CO3Utils script;
    private Thread thread;

    public H2CO3Task(Map<String, String> envvars, String scriptPath) {
        try {
            List<String[]> cmds = H2CO3Utils.parseJson(scriptPath);
            this.script = new H2CO3Utils(envvars, false, cmds, scriptPath);
            this.thread = new Thread(this);
        } catch (IOException e) {
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