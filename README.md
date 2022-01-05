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

#### Server Examples:
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

#### Client Examples:
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

Listening for events makes it possible to notice 
changes in time and perform the necessary processes 
related to channels and processing bytes

#### Server Examples:
```java
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;

public class ServerChannelListener extends ChannelListenerAdapter {

    @Override
    public void onConnected(ContextHandler contextHandler) {
        System.out.println("[Server] Connection was success bind on "
                + contextHandler.channel().address());
    }

    @Override
    public void onClosed(ContextHandler contextHandler) {
        System.out.println("[Server] Connection is closed!");
    }

    @Override
    public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {
        throwable.printStackTrace();
    }
}
```
---

Listeners are registered via `ChannelPipeline` as follows:

```java
Server server = new Server();
server.bindLocal(BIND_PORT, config -> {
    
    config.pipeline().putLast("channel-handler", new ServerChannelListener(server));
    ...
});
```

---

Some of the available events work on specific 
sides of the connection:

- Client: 
    - `onConnected(ContextHandler contextHandler)`
    - `onTimedOut(RemoteChannel channel, ContextHandler contextHandler)`
    
- Server:
    - `onClientConnected(RemoteChannel remoteChannel, ContextHandler contextHandler)`
    - `onClientClosed(RemoteChannel remoteChannel, ContextHandler contextHandler)`

- For all:
    - `onThreadActive(ContextHandler contextHandler)`
    - `onClosed(ContextHandler contextHandler)`
    - `onRead(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Input buffer)`
    - `onWrite(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Output buffer)`
    - `onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable)`
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
import org.itzstonlex.recon.handler.OutgoingByteHandler;

import java.util.Arrays;

public class WriteHandler extends OutgoingByteHandler {

    @Override
    public void onWrite(RemoteChannel remoteChannel, ContextHandler contextHandler,
                        ByteStream.Output buffer) {

        byte[] bytes = buffer.array();

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
                .array();

        // ...read or handle bytes
        remoteChannel.logger().info("onRead: [bytes]:" + Arrays.toString(bytes));
    }
}
```

---

`ReadHandler` and `WriteHandler` are registered exactly the same way 
through the `ChannelPipeline`, like all other event handlers 
for channels, because they inherit the `ChannelListener` interface

---

## Client Reconnection

The Recon library has functionality for 
reconnecting clients to a crashed server

To do this, it is enough to use one method, which adds a special
listener to check the data it needs and start the reconnection task.

**IMPORTANT!** This method works exclusively for clients.

```java
Client client = new Client();

client.connectLocal(CONNECT_PORT, config -> {
    
    // vars init.
    boolean hasDebug = true;
    long reconnectDelay = 5;

    // add reconnect listener.
    config.addClientReconnector(hasDebug, reconnectDelay, TimeUnit.SECONDS);
    ...
});
```
---
## Fast Recon

Sometimes we use large and functional libraries for simple 
algorithms, which is why we have to write many lines of code

Therefore, Recon Library offers the `FastRecon` utility for use, 
which in a couple of lines can **connect via http**, 
**bind a free port**, **connect to an existing one**, or **create a byte buffer**

#### FastRecon Connection-Builder Examples:
```java
Client client = FastRecon.newLocalConnection( CONNECT_PORT )
    .client_setTimeout(5000)

    .pipeline_addLast("read-handler", new ReadHandler())
    .pipeline_addLast("connection-handler", new ConnectionListener())

    .asClient();
```

#### FastRecon.Buffer Examples:
```java
byte[] bytes = FastRecon.Buffer.initBytes(buffer -> {

    buffer.writeBoolean(true);
    buffer.writeInt(512_000);
    buffer.writeString("github.com");
});
```

#### FastRecon.HTTP Examples:
```java
String url = "https://some-url/";
...

FastRecon.HTTP.fastHttpConnect(url, (callback, error) -> {

    if (error != null) {
        error.printStackTrace();
        return;
    }
    
    System.out.println(callback);
});
```

#### FastRecon.Machine Examples:
```java
Server server = FastRecon.Machine.fastBind("127.0.0.1", BIND_PORT, null);
Client client = FastRecon.Machine.fastConnect("127.0.0.1", CONNECT_PORT, null);
```

---
## Meta Dump


The data dump is very important in 
interacting and working with the protocol, therefore the 
library has a UI interface for tracking metric changes

**IN DEVELOPMENT...**
