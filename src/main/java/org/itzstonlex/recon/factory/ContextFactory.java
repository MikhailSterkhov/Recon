package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;

public class ContextFactory {

    public static ContextHandler createContext(RemoteChannel channel,
                                               ChannelListener listener,

                                               boolean isSuccess,
                                               Throwable error) {

        ContextHandler contextHandler = new ListenerContextHandler(channel, isSuccess, error);

        if (error != null) {
            listener.onExceptionCaught(channel, error);
        }

        return contextHandler;
    }

    private static class ListenerContextHandler
            implements ContextHandler {

        private final RemoteChannel channel;

        private final boolean isSuccess;
        private final Throwable error;

        public ListenerContextHandler(RemoteChannel channel, boolean isSuccess, Throwable error) {
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
