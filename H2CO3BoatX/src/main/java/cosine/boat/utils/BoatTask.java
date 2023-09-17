package cosine.boat.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cosine.boat.utils.BoatScript;

public class BoatTask implements Runnable {

    public static final String BOAT_TASK_SCRIPT_PATH = "boat_script_path";

    private BoatScript script;
    private Thread thread;

    public BoatTask(Map<String, String> envvars, String scriptPath) {
        try {
            List<String[]> cmds = BoatScript.parseJson(scriptPath);
            this.script = new BoatScript(envvars, false, cmds, scriptPath);
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