package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.util.ReconThreadsStorage;

import java.util.concurrent.ThreadFactory;

public final class ReconThreadFactory implements ThreadFactory {

    private static int threadCounter = 0;

    public static ReconThreadFactory asBuilder() {
        return new ReconThreadFactory();
    }

    public static ThreadFactory asInstance() {
        return ReconThreadFactory.asBuilder();
    }

    public static ThreadFactory asInstance(String name) {
        return ReconThreadFactory.asBuilder().setName(name);
    }

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private String name;
    private ThreadGroup group;

    private boolean daemon;

    private int priority = -1, stackSize;

    private ReconThreadFactory() {
    }

    public ReconThreadFactory setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }

    public ReconThreadFactory setName(String name) {
        this.name = String.format(name, threadCounter++);
        return this;
    }

    public ReconThreadFactory setGroup(ThreadGroup threadGroup) {
        this.group = threadGroup;
        return this;
    }

    public ReconThreadFactory setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public ReconThreadFactory setStackSize(int stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    public ReconThreadFactory setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    @Override
    public Thread newThread(Runnable command) {
        Thread thread = new Thread(group, command, name, stackSize);
        thread.setDaemon(daemon);

        if (priority >= 0) {
            thread.setPriority(priority);
        }

        if (uncaughtExceptionHandler != null) {
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        }

        ReconThreadsStorage.getAllThreads().add(thread);
        return thread;
    }
}
