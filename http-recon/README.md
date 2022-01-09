<div style="letter-spacing: 10px" align="center">

# HTTP RECON

 <div style="letter-spacing: 3px">

#### Responses & Executes

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
which were the source of information and code: [Click to redirect](src/test/java/org/itzstonlex/recon/http)

---

## How to create http client?

First, let's create a client that will already process all client requests.

```java
import org.itzstonlex.recon.http.HttpClient;

public class HttpRecon {

    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient();
    }
}
```

After that, all kinds of request options open before us:

```java
[GET]: HttpResponse httpResponse = httpClient.executeGet("SOME-URL");
[HEAD]: HttpResponse httpResponse = httpClient.executeHead("SOME-URL");
[POST]: HttpResponse httpResponse = httpClient.executePost("SOME-URL");
[PUT]: HttpResponse httpResponse = httpClient.executePut("SOME-URL");
[DELETE]: HttpResponse httpResponse = httpClient.executeDelete("SOME-URL");
[CONNECT]: HttpResponse httpResponse = httpClient.executeConnect("SOME-URL");
[OPTIONS]: HttpResponse httpResponse = httpClient.executeOptions("SOME-URL");
[TRACE]: HttpResponse httpResponse = httpClient.executeTrace("SOME-URL");
[PATCH]: HttpResponse httpResponse = httpClient.executePatch("SOME-URL");
```

And you can also execute a custom request with your own parameters:

```java
HttpRequestConfig requestConfig = new HttpRequestConfig(HttpUtils.REQUEST_POST);

requestConfig.setConnectTimeout(3000);
requestConfig.setReadTimeout(500);
requestConfig.setRequestProperty("request-key", "request-value");

HttpResponse httpResponse = httpClient.execute("SOME-URL", requestConfig);
// ...
```

This module allows you to change the request parameters in 
the original URL. For this, the `HttpParameters` utility is used:

```java
String someURL = HttpParameters.create("http://SOME-URL/")
    .addParameter("example_access_token", "example_01928374fhdj29jcmfsdl9ehl")
    .addParameter("author", "ItzStonlex")
        
    .appendParameters();
```