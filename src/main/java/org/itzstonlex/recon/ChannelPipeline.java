package org.itzstonlex.recon;

public interface ChannelPipeline {

    RemoteChannel channel();

    void putLast(String id, ChannelListener handler);

    void putBefore(String target, String id, ChannelListener handler);

    void putAfter(String target, String id, ChannelListener handler);

    void remove(String id);

    void remove(Class<? extends ChannelListener> clazz);

    <Handler extends ChannelListener> Handler get(String id);

    <Handler extends ChannelListener> Handler get(Class<Handler> clazz);

    ChannelListener[] nodes();

    ChannelListener first();

    ChannelListener last();

    // Fire channel-listener events.
    void fireThreadActiveEvent();

    void fireClosedEvent();

    void fireReadEvent(ByteStream.Input buffer);

    void fireWriteEvent(ByteStream.Output buffer);

    void fireExceptionCaughtEvent(Throwable throwable);

    void fireConnectedEvent();

    void fireTimedOutEvent();

    void fireClientConnectedEvent();

    void fireClientClosedEvent();
}
