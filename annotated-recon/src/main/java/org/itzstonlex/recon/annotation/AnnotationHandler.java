package org.itzstonlex.recon.annotation;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.annotation.type.BindServer;
import org.itzstonlex.recon.annotation.type.ConnectClient;
import org.itzstonlex.recon.annotation.type.Property;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.side.Server;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

final class AnnotationHandler {

    public void handle(BindServer annotation, Object instance, Field field)
    throws Exception {

        if (!field.getType().isAssignableFrom(RemoteChannel.class)) {
            return;
        }

        RemoteChannel channel = new Server().bind(
                new InetSocketAddress(annotation.host(), annotation.port())
        );

        field.setAccessible(true);
        field.set(instance, channel);
    }

    public void handle(ConnectClient annotation, Object instance, Field field)
    throws Exception {

        if (!field.getType().isAssignableFrom(RemoteChannel.class)) {
            return;
        }

        RemoteChannel channel = new Client().connect(
                new InetSocketAddress(annotation.host(), annotation.port()),
                annotation.timeout()
        );

        field.setAccessible(true);
        field.set(instance, channel);
    }

    public void handle(Property annotation, Object instance, Field field)
    throws Exception {

        for (ReconProperty property : AnnotatedReconScanner.getPropertyList()) {
            String value = property.getOrDefault(annotation.key(), annotation.defaultValue());

            field.setAccessible(true);
            field.set(instance, value);

            field.setAccessible(false);
        }
    }

}
