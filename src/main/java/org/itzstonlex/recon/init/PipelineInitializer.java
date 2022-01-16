package org.itzstonlex.recon.init;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.ChannelPipeline;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.exception.PipelineNotFoundException;
import org.itzstonlex.recon.exception.TimeoutException;
import org.itzstonlex.recon.factory.ContextFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class PipelineInitializer
        implements ChannelPipeline {

    private static class Node {

        public final String id;
        public final ChannelListener handler;

        public Node(String id, ChannelListener handler) {
            this.id = id;
            this.handler = handler;
        }
    }

    private final RemoteChannel channel;
    private final List<Node> nodes = new ArrayList<>();

    public PipelineInitializer(RemoteChannel channel) {
        this.channel = channel;
    }

    public int indexOf(String id) {
        int index = 0;

        for (Node node : nodes) {
            if (node.id.equals(id)) {
                return index;
            }

            index++;
        }

        return -1;
    }

    public void fill(int index, String id, ChannelListener handler) {
        if (id == null || handler == null) {
            throw new PipelineNotFoundException("pipeline id or handler cannot be null");
        }

        Node newNode = new Node(id, handler);

        if (index >= nodes.size()) {
            nodes.add(newNode);
            return;
        }

        nodes.add(index, newNode);
    }

    @Override
    public RemoteChannel channel() {
        return channel;
    }

    @Override
    public void addLast(String id, ChannelListener handler) {
        fill(nodes.size(), id, handler);
    }

    @Override
    public void addBefore(String target, String id, ChannelListener handler) {
        int targetIndex = indexOf(target);
        if (targetIndex < 0) {
            throw new PipelineNotFoundException("ByteHandler target id:'%s' is`nt find", target);
        }

        fill(targetIndex, id, handler);
    }

    @Override
    public void addAfter(String target, String id, ChannelListener handler) {
        fill(indexOf(target) + 1, id, handler);
    }

    @Override
    public void remove(String id) {
        int targetIndex = indexOf(id);
        if (targetIndex < 0) {
            return;
        }

        nodes.remove(targetIndex);
    }

    @Override
    public void remove(Class<? extends ChannelListener> clazz) {
        for (Node node : nodes) {

            if (node.handler.getClass().equals(clazz)) {
                remove(node.id);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Handler extends ChannelListener> Handler get(String id) {
        int targetIndex = indexOf(id);
        if (targetIndex < 0) {
            return null;
        }

        return (Handler) nodes.get(targetIndex).handler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Handler extends ChannelListener> Handler get(Class<Handler> clazz) {
        for (Node node : nodes) {

            if (node.handler.getClass().equals(clazz)) {
                return (Handler) node.handler;
            }
        }

        return null;
    }

    @Override
    public ChannelListener[] nodes() {
        return nodes.stream().map(node -> node.handler).toArray(f -> new ChannelListener[nodes.size()]);
    }

    @Override
    public ChannelListener first() {
        if (nodes.isEmpty()) {
            return null;
        }

        return nodes.get(0).handler;
    }

    @Override
    public ChannelListener last() {
        if (nodes.isEmpty()) {
            return null;
        }

        return nodes.get(nodes.size() - 1).handler;
    }

    private void forEachNodes(Consumer<ChannelListener> nodeConsumer) {
        for (ChannelListener channelListener : nodes()) {
            nodeConsumer.accept(channelListener);
        }
    }

    @Override
    public void fireThreadActiveEvent() {
        forEachNodes(listener -> listener.onThreadActive(channel, ContextFactory.createSuccessEventContext(channel, listener)));
    }

    @Override
    public void fireClosedEvent() {
        forEachNodes(listener -> listener.onClosed(channel, ContextFactory.createSuccessEventContext(channel, listener)));
    }

    @Override
    public void fireReadEvent(ByteStream.Input buffer) {
        forEachNodes(listener -> listener.onRead(channel, ContextFactory.createSuccessEventContext(channel, listener), buffer));
    }

    @Override
    public void fireWriteEvent(ByteStream.Output buffer) {
        forEachNodes(listener -> listener.onWrite(channel, ContextFactory.createSuccessEventContext(channel, listener), buffer));
    }

    @Override
    public void fireExceptionCaughtEvent(Throwable throwable) {
        forEachNodes(listener -> listener.onExceptionCaught(channel, throwable));
    }

    @Override
    public void fireConnectedEvent() {
        forEachNodes(listener -> listener.onConnected(channel, ContextFactory.createSuccessEventContext(channel, listener)));
    }

    @Override
    public void fireTimedOutEvent() {
        forEachNodes(listener -> listener.onTimedOut(channel, ContextFactory.createErrorEventContext(channel, listener, new TimeoutException("timed out"))));
    }

    @Override
    public void fireBindEvent() {
        forEachNodes(listener -> listener.onBind(channel, ContextFactory.createSuccessEventContext(channel, listener)));
    }

    @Override
    public void fireClientConnectedEvent(RemoteChannel channel) {
        forEachNodes(listener -> listener.onClientConnected(channel, ContextFactory.createSuccessEventContext(channel, listener)));
    }

    @Override
    public void fireClientClosedEvent(RemoteChannel channel) {
        forEachNodes(listener -> listener.onClientClosed(channel, ContextFactory.createSuccessEventContext(channel, listener)));
    }
}
