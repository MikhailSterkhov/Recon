<div style="letter-spacing: 10px" align="center">

# RECON

 <div style="letter-spacing: 3px">

#### Protocol & Connection Library

   <div style="color: red">
      The Library in development... <br> For contact me see "Feedback" 
   </div>

 </div>

---
</div>

### Feedback

+ **[Discord Server](https://discord.gg/GmT9pUy8af)**
+ **[VKontakte Page](https://vk.com/itzstonlex)**

---

## Use & Installation

soon...

---

## How to create remote connection?

Create a server connection:

```java
import org.itzstonlex.recon.side.Server;

public class ServerConnection {

    public static final int BIND_PORT = 1010;

    public void launchApplication() {
        Server server = new Server();

        server.bindLocal(BIND_PORT);
    }

    // That method running from manifest classpath.
    public static void main(String[] args) {
        ServerConnection serverConnection = new ServerConnection();
        serverConnection.launchApplication();
    }
}
```

Create a client connection:

```java
import org.itzstonlex.recon.side.Client;

public class ClientConnection {

    public static final int CONNECT_PORT = 1010;

    public void launchApplication() {
        Client client = new Client();

        client.connectLocal(CONNECT_PORT);
    }

    // That method running from manifest classpath.
    public static void main(String[] args) {
        ClientConnection clientConnection = new ClientConnection();
        clientConnection.launchApplication();
    }
}
```

---

## Events Listening

For server example:

```java
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;

public class ConnectionListener extends ChannelListenerAdapter {

    @Override
    public void onActive(ContextHandler contextHandler) {
        System.out.println("[Server] Connection was success bind on "
                + contextHandler.channel().address());
    }

    @Override
    public void onInactive(ContextHandler contextHandler) {
        System.out.println("[Server] Connection is closed!");
    }


    @Override
    public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {
        throwable.printStackTrace();
    }
}
```

Before add the listener to connection:

```java
Server server = new Server();

server.addListener(new ConnectionListener()); [<---]
server.bindLocal(1010);
```

---

## Bytes Write

Create a bytes-buffer:

```java
ByteStream.Output output = BufferFactory.createPooledOutput();
```

Put a data as bytes:

```java
output.writeString("ItzStonlex");
output.writeBoolean(true);
```

And write to connection channel:

```java
server.channel().write(output);
```

Method `RemoteChannel#write(ByteStream.Output)` write a bytes to all connected channels


Sending bytes can be intercepted using `OutgoingByteHandler#onRead()`

For example:

```java
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.handler.OutgoingByteHandler;

import java.util.Arrays;

public class WriteHandler extends OutgoingByteHandler {

    @Override
    public void onWrite(RemoteChannel remoteChannel, ContextHandler contextHandler, 
                        ByteStream.Output buffer) {
        
        byte[] bytes = buffer.toByteArray();

        // ...handle write bytes
        remoteChannel.logger().info("onWrite: [bytes]:" + Arrays.toString(bytes));
    }
}
```

---

## Bytes Read

Reading bytes is handled through `IncomingByteHandler#onRead()`

For example:

```java
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.handler.IncomingByteHandler;

import java.util.Arrays;

public class ReadHandler extends IncomingByteHandler {

    @Override
    public void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler, 
                       ByteStream.Input buffer) {
        
        byte[] bytes = BufferFactory.transformOutput(buffer)
                .toByteArray();

        // ...read or handle bytes
        remoteChannel.logger().info("onRead: [bytes]:" + Arrays.toString(bytes));
    }
}
```

Register handler to connection:
```java

```