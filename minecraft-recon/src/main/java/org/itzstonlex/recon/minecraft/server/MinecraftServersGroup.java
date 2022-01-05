package org.itzstonlex.recon.minecraft.server;

public final class MinecraftServersGroup {

    public static MinecraftServersGroup create(int id, String title, String prefix) {
        return new MinecraftServersGroup(id, title, prefix);
    }

    private final int id;

    private final String title;
    private final String prefix;

    private MinecraftServersGroup(int id, String title, String prefix) {
        this.id = id;
        this.title = title;
        this.prefix = prefix;
    }

    public int getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTitle() {
        return title;
    }

}
