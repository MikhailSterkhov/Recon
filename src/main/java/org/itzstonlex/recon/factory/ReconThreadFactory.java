package org.itzstonlex.recon.factory;

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

    private String name;
    private ThreadGroup group;

    private ReconThreadFactory() {
    }

    public ReconThreadFactory setName(String name) {
        this.name = String.format(name, threadCounter++);
        return this;
    }

    public ReconThreadFactory setGroup(ThreadGroup threadGroup) {
        this.group = threadGroup;
        return this;
    }

    @Override
    public Thread newThread(Runnable command) {
        return new Thread(group, command, name);
    }
}
