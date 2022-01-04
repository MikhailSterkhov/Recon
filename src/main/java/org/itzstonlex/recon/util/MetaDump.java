package org.itzstonlex.recon.util;

public final class MetaDump {

    public static final class MemoryDump {

        public long free;
        public long used;
        public long max;

        public MemoryDump(long free, long used, long max) {
            update(free, used, max);
        }

        public void update(long free, long used, long max) {
            this.free = free;
            this.used = used;
            this.max = max;
        }

        public void reset(long free, long used, long max) {
            System.gc();
            update(free, used, max);
        }
    }

    public static final class ProcessorDump {

        public long cpuLoad;
        public long cpuProcess;

        public long threads;

        public ProcessorDump(long cpuLoad, long cpuProcess, long threads) {
            update(cpuLoad, cpuProcess, threads);
        }

        public void update(long cpuLoad, long cpuProcess, long threads) {
            this.cpuLoad = cpuLoad;
            this.cpuProcess = cpuProcess;
            this.threads = threads;
        }

        public void reset(long cpuLoad, long cpuProcess, long threads) {
            System.gc();
            update(cpuLoad, cpuProcess, threads);
        }
    }

    public static MemoryDump memoryDump(long free, long used, long max) {
        return new MemoryDump(free, used, max);
    }

    public static MemoryDump currentMemoryDump() {
        Runtime runtime = Runtime.getRuntime();
        return memoryDump(runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory());
    }


    public static ProcessorDump processorDump(long cpuLoad, long cpuProcess, long availableThreads) {
        return new ProcessorDump(cpuLoad, cpuProcess, availableThreads);
    }

    public static ProcessorDump currentProcessorDump() {
        Runtime runtime = Runtime.getRuntime();
        return processorDump(runtime.freeMemory(), runtime.totalMemory(), runtime.availableProcessors());
    }


    public static void openDumpUI() {
        // TODO
    }

}
