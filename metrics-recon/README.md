<div style="letter-spacing: 10px" align="center">

# METRICS RECON

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
which were the source of information and code: [Click to redirect](src/test/java/org/itzstonlex/recon/metrics/tests)

---

## How to use?

This module allows you to store and process 
metric data, which can then be orientated, displayed, displayed, 
or stored somewhere.

```java
import org.itzstonlex.recon.metrics.ReconMetrics;

public class MetricRecon {

    public static void main(String[] args) {
        ReconMetrics reconMetrics = ReconMetrics.newMetricInstance();
    }
}
```
---

## How to add custom Time Snippets?

You can add your own time intervals, in the interval 
of which the metric will store data.

Let's use `ReconMetrics.TOTAL_READS` as an example:

```java
MetricCounter metricCounter = ReconMetrics.TOTAL_READS;

metricCounter.addTimeSnippet(MetricTime.of(20, TimeUnit.SECONDS));
metricCounter.addTimeSnippet(MetricTime.of(4, TimeUnit.DAYS));
// ...and more
```

---

## How to create, get or update metrics data?
### `Create:`

The API of this module allows you to both 
create your own custom metric and use template templates, 
receiving ready-made data and registering it in 
the remote connection channel:

```java
public class ExampleServer extends AbstractServer {
    private final ReconMetrics reconMetrics;

    public ExampleServer(ReconMetrics reconMetrics) {
        super("localhost", 3305);

        this.reconMetrics = reconMetrics;
    }

    @Override
    public void initChannel(ConnectionLogger logger, ChannelConfig channelConfig) {
        
        // Registration of a prepared metric.
        reconMetrics.appendPipeline(channelConfig.pipeline());
    }
}
```

---

### `Get:`

The methods for obtaining data from a metric depend on 
what exactly you need: Get a value from a certain period of 
time or the current value.

```java
int totalReads = ReconMetrics.TOTAL_READS
        .currentValue();
```

```java
int totalReadsInFiveSeconds = ReconMetrics.TOTAL_READS
        .valueOf(5, TimeUnit.SECONDS);
```

And also there is the possibility of a quick dump of the counter:

```java
ReconMetrics.TOTAL_READS.printDump(System.out);
```

---

### `Update:`


Updating data is as convenient as possible: you do not 
need to manually change and specify the time, the library 
does everything for you.

To update values, it is possible to use methods such as 
`MetricCounter#increment()`, `MetricCounter#decrement()`, 
`MetricCounter#add(int)`, `MetricCounter#take(int)` and 
`MetricCounter#set(int)`

For example:

```java
ReconMetrics.TOTAL_READS.set(53);

ReconMetrics.TOTAL_READS.add(10_000);

ReconMetrics.TOTAL_READS.decrement();

// ...and more
```