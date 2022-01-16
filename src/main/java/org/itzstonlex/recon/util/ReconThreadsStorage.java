package org.itzstonlex.recon.util;

import java.util.ArrayList;
import java.util.List;

public final class ReconThreadsStorage {

    private static final List<Thread> allThreads    = new ArrayList<>();
    private static final List<Thread> serverThreads = new ArrayList<>();
    private static final List<Thread> clientThreads = new ArrayList<>();

    public static void addServerThread(Thread thread) {
        allThreads.add(thread);
        serverThreads.add(thread);
    }

    public static void addClientThread(Thread thread) {
        allThreads.add(thread);
        clientThreads.add(thread);
    }

    public static List<Thread> getAllThreads() {
        return allThreads;
    }

    public static List<Thread> getClientThreads() {
        return clientThreads;
    }

    public static List<Thread> getServerThreads() {
        return serverThreads;
    }
}
