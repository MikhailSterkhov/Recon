package org.itzstonlex.recon;

public interface ChannelPipeline {

    RemoteChannel channel();

    void putLast(String id, ChannelListener handler);

    void putBefore(String target, String id, ChannelListener handler);

    void putAfter(String target, String id, ChannelListener handler);

    <Handler extends ChannelListener> Handler get(String id);

    <Handler extends ChannelListener> Handler get(Class<Handler> clazz);

    ChannelListener[] nodes();
}
