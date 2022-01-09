<div style="letter-spacing: 10px" align="center">

# MINECRAFT RECON

 <div style="letter-spacing: 3px">

#### Minecraft Protocol & Connection Library

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

## Help

If something of what was said below was 
not clear to you, then you can refer to the tests, 
which were the source of information and code: [Click to redirect](src/test/java/org/itzstonlex/recon/minecraft/)

---

## How to bind a minecraft application?

To begin with, you need to understand that the 
main **two components of any Minecraft Application** are the `Player` and the `Server`.

The library initializes both of these parameters 
through the prepared `Handshake` packets, but the initialization 
algorithm itself will be completely transferred to you.

---

These objects are initialized through 
the special `HandshakeInitializer` interface, 
which is required to implement `MinecraftManagementService`

The methods of initializing the `Player` and the `Server` 
can be divided into two options:

1. Use `MinecraftManagementService` as `DefaultMinecraftManagementService`
2. Use custom initializers for example:

```java
import org.itzstonlex.recon.minecraft.packet.handshake.HandshakeInitializer;

private final class PlayerInitializer
        implements HandshakeInitializer<PlayerHandshake, CorePlayer> {

    @Override
    public CorePlayer init(RemoteChannel channel, PlayerHandshake handshake) {
        UUID uniqueId = handshake.getUniqueId();

        String playerName = handshake.getName();
        InetSocketAddress address = handshake.getAddress();

        CorePlayer instance = new CorePlayer(uniqueId, playerName, address);

        // Init non-final other data.
        instance.init(serverManager,
                handshake.getDisplayName(), handshake.getProxyServer(), handshake.getCurrentServer()
        );

        return instance;
    }
}
```
```java
import org.itzstonlex.recon.minecraft.packet.handshake.HandshakeInitializer;

private final class ServerInitializer
        implements HandshakeInitializer<ServerHandshake, CoreServer> {

    @Override
    public CoreServer init(RemoteChannel channel, ServerHandshake handshake) {
        String serverName = handshake.getName();
        boolean isProxy = handshake.isProxy();

        InetSocketAddress address = handshake.getAddress();

        return new CoreServer(minecraftCore, channel, isProxy, serverName, address);
    }
}
```

---

If, as a result, you do not use the `DefaultMinecraftManagementService` 
to initialize the Player and Server services, then according 
to the following example we create our own `MinecraftManagementService`:
```java
import org.itzstonlex.recon.minecraft.service.MinecraftManagementService;

@SuppressWarnings("unchecked")
public final class CoreService implements MinecraftManagementService {

    private final MinecraftCore minecraftCore;

    private final PlayerManager<CorePlayer> playerManager = PlayerManager.newManager(CorePlayer.class, new PlayerInitializer());
    private final ServerManager<CoreServer> serverManager = ServerManager.newManager(CoreServer.class, new ServerInitializer());

    public CoreService(MinecraftCore minecraftCore) {
        this.minecraftCore = minecraftCore;
    }

    @Override
    public PlayerManager<CorePlayer> getPlayerManager() {
        return playerManager;
    }

    @Override
    public ServerManager<CoreServer> getServerManager() {
        return serverManager;
    }

}
```

---

Fine! The most difficult and tedious step is behind, there are very 
simple steps ahead that you can repeat from scratch in a couple for 
the completely correct operation of this module.

Now all that remains to be done for at least some result is 
to create the application itself, register the packages you use 
or recently created by you, server groups, packet or byte handlers, 
it can be anything

We act according to the following example:
```java
import org.itzstonlex.recon.minecraft.api.ReconMinecraftApi;

public class MinecraftCore {
    
// ==================================================================================== //

    public static final ReconMinecraftApi MINECRAFT_API = new ReconMinecraftApi();
    
    public static final int BIND_PORT = 1000;

    public static void main(String[] args) {
        MinecraftCore minecraftCore = new MinecraftCore();
        minecraftCore.launchCore();
    }

// ==================================================================================== //


    // This is the MinecraftManagementService we just created
    private final CoreService coreService = new CoreService(this);

    public final CoreService getService() {
        return coreService;
    }

    /**
     * Process of the application launch.
     */
    public void launchCore() {
        // ...your launch code

        bindLocal();
    }

    /**
     * Bind a Local Server on
     * the port of 1000.
     */
    private void bindLocal() {
        ReconMinecraftApi.MinecraftApplication server = MINECRAFT_API.newMinecraftApplication();

        // Create a service-factory.
        ReconMinecraftApi.ServiceFactory serviceFactory = ReconMinecraftApi.ServiceFactory.create(coreService,
                (bossHandler, channelConfig) -> {

                    // TODO: Your channel initialize logic...
                });

        // Bind a local server on BIND_PORT (1000)
        server.bindLocal(BIND_PORT, serviceFactory);
    }

    /**
     * Getting a player by UUID
     *
     * @param uuid - Player UUID
     */
    public CorePlayer getPlayer(UUID uuid) {
        return coreService.getPlayerManager().getPlayer(uuid);
    }

    /**
     * Getting a player by name.
     *
     * @param name - Player name.
     */
    public CorePlayer getPlayer(String name) {
        return coreService.getPlayerManager().getPlayer(name);
    }

    /**
     * Getting a proxy server by name.
     *
     * @param name - Proxy server name.
     */
    public Proxy getProxy(String name) {
        CoreServer coreServer = coreService.getServerManager().getServer(name);

        if (coreServer.isProxy()) {
            return (Proxy) coreServer;
        }

        return null;
    }

    /**
     * Getting a bukkit server by name.
     *
     * @param name - Bukkit server name.
     */
    public Bukkit getBukkit(String name) {
        CoreServer coreServer = coreService.getServerManager().getServer(name);

        if (!coreServer.isProxy()) {
            return (Bukkit) coreServer;
        }

        return null;
    }
}
```

---
## How to create my first Minecraft Packet?

Let's consider this question using an example 
of a situation: `The player needs to send a message to the chat`.

This means that you need to send a 
packet with processed 
data to the server where the player is now in 
order to send our message.

Packet class example:
```java
public final class PlayerChat extends MinecraftPacket {

    // Packet variables
    private String playerName;
    private String message;

    public PlayerChat() {
    }

    public PlayerChat(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }

    // Write data.
    @Override
    public void write(ByteStream.Output buffer) {
        buffer.writeString(playerName);
        buffer.writeString(message);
    }

    // Read data.
    @Override
    public void read(ByteStream.Input buffer) {
        this.playerName = buffer.readString();
        this.message = buffer.readString();
    }


    // Readeable data getters.
    public String getPlayerName() {
        return playerName;
    }

    public String getMessage() {
        return message;
    }

}
```

Packet register example:
```java
// ...in MinecraftCore class code

/**
  * Registering all packets.
  *
  * ATTENTION! The system has already pre-occupied
  * identifiers 0 and 1 handshake packages,
  * so if you want to specify identifiers
  * manually, pay attention to this information
  *
  * @param registry - Recon Minecraft registry-service.
  */
 private void registerPackets(ReconMinecraftRegistry registry) {
     registry.registerPacket(PlayerChat::new);
     // ...
 }
```

```java
// ...in channel initializer code.
registerPackets(MINECRAFT_API.getRegistry());
```

Packet use example:
```java
// ...in MinecraftPlayer implementation code
public void sendMessage(String message){

    if (currentServer != null) {
        currentServer.sendPacket(new PlayerChat(name,message));
    }
}
```

Packet handle example:

```java
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacketHandler;

public class BukkitPacketHandler extends MinecraftPacketHandler {
    private final MinecraftCore minecraftCore;
    
    public BukkitPacketHandler(MinecraftCore minecraftCore) {
        this.minecraftCore = minecraftCore;
    }

    @PacketHandler
    public void handle(PlayerChat packet) {
        CorePlayer player = minecraftCore.getPlayer(packet.getPlayerName());
        String message = packet.getMessage();

        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public void onExceptionCaught(RemoteChannel channel, Throwable throwable) {
        throwable.printStackTrace();
    }
}
```

```java
// ...in channel initializer code.
bossHandler.addHandler( new BukkitPacketHandler(MinecraftCore.this) );
```
