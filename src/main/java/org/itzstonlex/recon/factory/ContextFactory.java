package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;

public final class ContextFactory {

    public static ContextHandler createEventContext(RemoteChannel channel, ChannelListener listener,
                                                    boolean isSuccess, Throwable error) {
        ContextHandler contextHandler
                = new EventContextHandler(channel, isSuccess, error);

        if (error != null) {
            listener.onExceptionCaught(channel, error);
        }

        return contextHandler;
    }

    public static ContextHandler createSuccessEventContext(RemoteChannel channel, ChannelListener listener) {
        return createEventContext(channel, listener, true, null);
    }

    public static ContextHandler createSuccessEventContext(RemoteChannel channel, ChannelListener listener, Throwable throwable) {
        return createEventContext(channel, listener, true, throwable);
    }

    public static ContextHandler createErrorEventContext(RemoteChannel channel, ChannelListener listener, Throwable throwable) {
        return createEventContext(channel, listener, false, throwable);
    }

    private static class EventContextHandler
            implements ContextHandler {

        private final RemoteChannel channel;

        private final boolean isSuccess;
        private final Throwable error;

        public EventContextHandler(RemoteChannel channel, boolean isSuccess, Throwable error) {
            this.channel = channel;
            this.isSuccess = isSuccess;
            this.error = error;
        }

        @Override
        public boolean isSuccess() {
            return isSuccess;
        }

        @Override
        public Throwable error() {
            return error;
        }

        @Override
        public RemoteChannel channel() {
            return channel;
        }
    }
}
