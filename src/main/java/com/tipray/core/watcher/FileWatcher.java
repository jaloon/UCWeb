package com.tipray.core.watcher;


import java.io.File;

/**
 * 文件变化监听
 *
 * @author chenlong
 * @version 1.0 2018-07-04
 */
public abstract class FileWatcher extends Thread {
    /**
     * resources directory path.
     */
    public static final String RESOURCES_PATH = FileWatcher.class.getClassLoader().getResource("").getPath();
    /**
     * The default delay between every file modification check, set to 60 seconds.
     */
    public static final long DEFAULT_DELAY = 60000;
    /**
     * The name of the file to observe for changes.
     */
    protected String filename;
    /**
     * The delay to observe between every check. By default set {@link #DEFAULT_DELAY}.
     */
    protected long delay = DEFAULT_DELAY;

    File file;
    long lastModif = 0;
    boolean warnedAlready = false;
    boolean interrupted = false;

    public FileWatcher(String filename) {
        super("FileWatcher");
        this.filename = filename;
        file = new File(RESOURCES_PATH, filename);
        setDaemon(true);
        checkAndConfigure();
    }

    /**
     * Set the delay to observe between each check of the file changes.
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    abstract protected void doOnChange();

    protected void checkAndConfigure() {
        boolean fileExists;
        try {
            fileExists = file.exists();
        } catch (SecurityException e) {
            System.out.println("Was not allowed to read check file existance, file:[" + filename + "].");
            interrupted = true; // there is no point in continuing
            return;
        }

        if (fileExists) {
            long l = file.lastModified();   // this can also throw a SecurityException
            if (l > lastModif) {            // however, if we reached this point this
                lastModif = l;              // is very unlikely.
                doOnChange();
                warnedAlready = false;
            }
        } else {
            if (!warnedAlready) {
                System.out.println("[" + filename + "] does not exist.");
                warnedAlready = true;
            }
        }
    }

    @Override
    public void run() {
        while (!interrupted) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // no interruption expected
            }
            checkAndConfigure();
        }
    }

    /**
     * close watcher.
     */
    public void close() {
        interrupted = true;
    }
}
