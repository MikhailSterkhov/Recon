<div style="letter-spacing: 10px" align="center">

# ANNOTATED RECON

 <div style="letter-spacing: 3px">

#### Simplify your code!

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
which were the source of information and code: [Click to redirect](src/test/java/org/itzstonlex/recon/annotation)

---

## How to bind a Server?

This operation is performed by the `@BindServer` annotation

For example:
```java
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.annotation.type.BindServer;

public class BindTests {

    @BindServer(port = 3305)
    private RemoteChannel channel;

    public static void main(String[] args) {
        BindTests bindTests = new BindTests();

        AnnotatedReconScanner.scanInstance(bindTests);
        System.out.println("Channel address: " + bindTests.channel.address());
    }

}
```

**IMPORTANT!** The type of the variable for this
operation must necessarily inherit from the `RemoteChannel`

---

## How to create Client connection?

The situation is similar as with the Server, 
only in this case the `@ConnectClient` annotation is used

For example:
```java
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.annotation.type.ConnectClient;

public class ConnectTests {

    @ConnectClient(port = 3305, timeout = 5000)
    private RemoteChannel channel;

    public static void main(String[] args) {
        ConnectTests connectTests = new ConnectTests();

        AnnotatedReconScanner.scanInstance(connectTests);
        System.out.println("Channel address: " + connectTests.channel.address());
    }

}
```

**IMPORTANT!** The type of the variable for this 
operation must necessarily inherit from the `RemoteChannel`

---

## Fields values as Property


This module supports the initialization of 
string fields from the values of custom property instances

For example:
```java
import org.itzstonlex.recon.annotation.type.Property;

public class PropertyTests {

    @Property(key = "github")
    private String githubLink;

    @Property(key = "author", defaultValue = "ItzStonlex")
    private String author;
    
    public void testVariables() {
        AnnotatedReconScanner.scanInstance(this);

        System.out.println("github: " + propertyTests.githubLink);
        System.out.println("author: " + propertyTests.author);
    }

    
    public static void main(String[] args) {

        // Property init.
        ReconProperty reconProperty = new ReconProperty();

        reconProperty.set("github", "https://github.com/ItzStonlex/Recon");
        reconProperty.set("key", "value");

        AnnotatedReconScanner.addProperty(reconProperty);
        
        
        // Instance fields tests.
        PropertyTests propertyTests = new PropertyTests();
        propertyTests.testVariables();
    }
}
```

**IMPORTANT!** The type of the variable for this operation must be `String`.
