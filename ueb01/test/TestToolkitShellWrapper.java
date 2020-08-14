import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hilfsklasse, die eine Shell kapselt.
 * 
 * @author kar, mhe
 */
public class TestToolkitShellWrapper extends Thread {

    private static Runtime run;

    private final static int _CLOCK = 50;

    static {
        TestToolkitShellWrapper.run = Runtime.getRuntime();
    }

    Thread guard;

    private Process pr;

    private String[] command;

    private String directory, stdout_str, stderr_str;

    int time, timeout, retval;

    boolean completed, dead;

    @Deprecated
    public TestToolkitShellWrapper(String directory, String command, int timeout) {
        this(directory, command.split(" "), timeout);
    }

    public TestToolkitShellWrapper(String directory, String[] command,
            int timeout) {
        stdout_str = "";
        stderr_str = "";
        this.directory = directory;
        this.command = command;
        this.time = 0;
        this.timeout = timeout;
        this.retval = 0;
        this.completed = false;
        this.dead = false;
    }

    @Override
    public void run() {
        try {
           
            pr = run.exec(this.command, null, new File(this.directory));
            final BufferedReader stderr =
                    new BufferedReader(new InputStreamReader(
                            pr.getErrorStream()));
            final BufferedReader stdout =
                    new BufferedReader(new InputStreamReader(
                            pr.getInputStream()));

            guard = new Thread(new Runnable() {
                @Override
                synchronized public void run() {
                    try {
                        while (!completed && (timeout == 0 || time <= timeout)) {
                            readIfPossible(false);
                            wait(_CLOCK);
                            TestToolkitShellWrapper.this.time += _CLOCK;
                        }
                        readIfPossible(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (time > timeout) {
                        System.out.print("killing process...");
                        pr.destroy();
                        TestToolkitShellWrapper.this.dead = true;
                    }
                }

                private void readIfPossible(boolean careIfReady)
                        throws IOException {
                    String line = "";
                    while ((careIfReady ? stdout.ready() : true)
                            && (line = stdout.readLine()) != null) {
                        stdout_str = stdout_str + line + "\n";
                    }
                    while (careIfReady ? stdout.ready() : true && (line =
                            stderr.readLine()) != null) {
                        stderr_str = stderr_str + line + "\n";
                    }
                }
            });
            guard.start();

            try {
                pr.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.retval = pr.exitValue();
            this.completed = true;
            guard.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getReturnValue() {
        return this.retval;
    }

    public String getStdOut() {
        return this.stdout_str;
    }

    public String getStdErr() {
        return this.stderr_str;
    }

    public int getTimeUsed() {
        return this.time;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public boolean isDead() {
        return this.dead;
    }

    public boolean isSuccessful() {
        return this.isCompleted() && !this.isDead()
                && (this.getReturnValue() == 0);
    }

    public boolean isFailed() {
        return this.isDead()
                || (this.isCompleted() && this.getReturnValue() != 0);
    }

    public void execute() {
        this.start();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
