package org.itzstonlex.recon.init;

import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.ChannelPipeline;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.error.PipelineNotFoundError;

import java.util.ArrayList;
import java.util.List;

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
            throw new PipelineNotFoundError("pipeline id or handler cannot be null");
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
    public void putLast(String id, ChannelListener handler) {
        fill(nodes.size(), id, handler);
    }

    @Override
    public void putBefore(String target, String id, ChannelListener handler) {
        int targetIndex = indexOf(target);
        if (targetIndex < 0) {
            throw new PipelineNotFoundError("ByteHandler target id:'%s' is`nt find", target);
        }

        fill(targetIndex, id, handler);
    }

    @Override
    public void putAfter(String target, String id, ChannelListener handler) {
        fill(indexOf(target) + 1, id, handler);
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

            if (node.handler.getClass().isAssignableFrom(clazz)) {
                return (Handler) node.handler;
            }
        }

        return null;
    }

    @Override
    public ChannelListener[] nodes() {
        return nodes.stream().map(node -> node.handler).toArray(f -> new ChannelListener[nodes.size()]);
    }
}
